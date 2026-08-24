package com.ayurveda.billing.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.billing.client.MedicineServiceClient;
import com.ayurveda.billing.constant.BillingMessages;
import com.ayurveda.billing.dto.client.MedicineClientResponse;
import com.ayurveda.billing.dto.client.StockAdjustClientRequest;
import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.dto.request.PartPaymentRequest;
import com.ayurveda.billing.dto.response.InvoiceListResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.entity.Invoice;
import com.ayurveda.billing.entity.InvoiceItem;
import com.ayurveda.billing.entity.InvoicePayment;
import com.ayurveda.billing.entity.PackageMaster;
import com.ayurveda.billing.enums.BillSection;
import com.ayurveda.billing.enums.InvoiceItemType;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.billing.mapper.InvoiceMapper;
import com.ayurveda.billing.repository.InvoiceRepository;
import com.ayurveda.billing.repository.PackageMasterRepository;
import com.ayurveda.billing.service.InvoiceService;
import com.ayurveda.billing.util.BillSectionResolver;
import com.ayurveda.billing.util.InvoiceCalculationUtil;
import com.ayurveda.billing.util.InvoiceNumberGenerator;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.activity.ActivityActionType;
import com.ayurveda.common.activity.ActivityLogPublisher;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PackageMasterRepository packageMasterRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceNumberGenerator invoiceNumberGenerator;
    private final MedicineServiceClient medicineServiceClient;
    private final ActivityLogPublisher activityLogPublisher;

    @Value("${billing.default-cgst-percent:3}")
    private BigDecimal defaultCgstPercent;

    @Value("${billing.default-sgst-percent:3}")
    private BigDecimal defaultSgstPercent;

    @Override
    public ApiResponse<InvoiceResponse> createInvoice(CreateInvoiceRequest request) {
        log.info("Creating invoice for patient {}", request.getPatientId());

        List<BillSection> sections = BillSectionResolver.resolveFromRequest(request);
        PackageMaster packageMaster = resolvePackageMaster(request.getPackageMasterId());

        String packageType = trimToNull(request.getPackageType());
        BigDecimal packageCharges = request.getPackageCharges();
        if (packageMaster != null) {
            if (packageType == null) {
                packageType = packageMaster.getName();
            }
            if (packageCharges == null) {
                packageCharges = packageMaster.getPackagePrice();
            }
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumberGenerator.generate())
                .patientId(request.getPatientId())
                .patientName(request.getPatientName().trim())
                .contactNumber(request.getContactNumber())
                .invoiceDate(request.getInvoiceDate())
                .visitType(request.getVisitType())
                .serviceFees(InvoiceCalculationUtil.money(request.getServiceFees()))
                .packageMasterId(packageMaster != null ? packageMaster.getId() : null)
                .packageType(packageType)
                .packageCharges(packageCharges != null
                        ? InvoiceCalculationUtil.money(packageCharges)
                        : InvoiceCalculationUtil.money(null))
                .discount(InvoiceCalculationUtil.money(request.getDiscount()))
                .taxEnabled(Boolean.TRUE.equals(request.getTaxEnabled()))
                .billSections(BillSectionResolver.toStorage(sections))
                .items(new ArrayList<>())
                .payments(new ArrayList<>())
                .build();

        addLineItems(invoice, request);
        validateMedicineStockAgainstRequest(invoice.getItems());
        applyTotals(invoice, request.getCgstPercent(), request.getSgstPercent());

        BigDecimal initialPaid = InvoiceCalculationUtil.money(request.getAmountPaid());
        if (initialPaid.compareTo(BigDecimal.ZERO) > 0) {
            applyPayment(invoice, initialPaid, request.getPaymentMethod(), request.getPaymentRemarks());
        } else {
            invoice.setPaidAmount(BigDecimal.ZERO.setScale(2));
            invoice.setLeftAmount(InvoiceCalculationUtil.leftAmount(invoice.getTotalAmount(), invoice.getPaidAmount()));
            invoice.setStatus(InvoiceCalculationUtil.resolveStatus(invoice.getTotalAmount(), invoice.getPaidAmount()));
        }

        Invoice saved = invoiceRepository.save(invoice);

        try {
            deductMedicineStock(saved.getItems());
        } catch (RuntimeException ex) {
            saved.setDeleted(true);
            invoiceRepository.save(saved);
            throw ex;
        }

        log.info("Invoice created successfully. Invoice ID: {}, Invoice Number: {}",
                saved.getId(), saved.getInvoiceNumber());

        activityLogPublisher.record(
                "Billing",
                ActivityActionType.CREATED,
                "Invoice " + saved.getInvoiceNumber());

        return ApiResponse.success(BillingMessages.INVOICE_GENERATED_SUCCESSFULLY, invoiceMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<InvoiceResponse> getInvoiceById(UUID invoiceId) {
        log.info("Fetching invoice details for invoiceId: {}", invoiceId);

        Invoice invoice = findActive(invoiceId);
        invoice.getItems().size();
        invoice.getPayments().size();

        log.info("Invoice fetched successfully. Invoice ID: {}, Invoice Number: {}",
                invoice.getId(), invoice.getInvoiceNumber());

        return ApiResponse.success(invoiceMapper.toResponse(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<InvoiceListResponse>> getInvoices(String patientId, InvoiceStatus status) {
        log.info("Fetching invoices with patientId={}, status={}", patientId, status);

        UUID parsedPatientId = null;
        String patientSearch = null;

        if (StringUtils.hasText(patientId)) {
            String trimmed = patientId.trim();
            try {
                parsedPatientId = UUID.fromString(trimmed);
            } catch (IllegalArgumentException ex) {
                patientSearch = trimmed;
            }
        }

        List<InvoiceListResponse> invoices = invoiceRepository
                .search(parsedPatientId, patientSearch, status)
                .stream()
                .map(invoiceMapper::toListResponse)
                .toList();

        log.info("Successfully fetched {} invoices.", invoices.size());

        return ApiResponse.success(BillingMessages.INVOICES_FETCHED_SUCCESSFULLY, invoices);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<InvoiceListResponse>> getInvoicesByPatientId(
            UUID patientId, InvoiceStatus status) {
        log.info("Fetching invoices for patientId={}, status={}", patientId, status);

        List<InvoiceListResponse> invoices = invoiceRepository
                .search(patientId, null, status)
                .stream()
                .map(invoiceMapper::toListResponse)
                .toList();

        log.info("Successfully fetched {} invoices for patientId={}.", invoices.size(), patientId);

        return ApiResponse.success(BillingMessages.INVOICES_FETCHED_SUCCESSFULLY, invoices);
    }

    @Override
    public ApiResponse<InvoiceResponse> recordPartPayment(UUID invoiceId, PartPaymentRequest request) {
        log.info("Recording part payment for invoiceId: {}, amount: {}", invoiceId, request.getAmountPaid());

        Invoice invoice = findActive(invoiceId);

        if (invoice.getStatus() == InvoiceStatus.COMPLETED) {
            throw new BadRequestException(BillingMessages.INVOICE_ALREADY_FULLY_PAID);
        }

        BigDecimal paymentAmount = InvoiceCalculationUtil.money(request.getAmountPaid());
        BigDecimal left = InvoiceCalculationUtil.leftAmount(invoice.getTotalAmount(), invoice.getPaidAmount());

        if (paymentAmount.compareTo(left) > 0) {
            throw new BadRequestException(BillingMessages.PAYMENT_EXCEEDS_LEFT_AMOUNT + left);
        }

        applyPayment(invoice, paymentAmount, request.getPaymentMethod(), request.getRemarks());
        Invoice saved = invoiceRepository.save(invoice);

        saved.getItems().size();
        saved.getPayments().size();

        log.info("Part payment recorded successfully. Invoice ID: {}, Status: {}, Left Amount: {}",
                saved.getId(), saved.getStatus(), saved.getLeftAmount());

        return ApiResponse.success(
                BillingMessages.PART_PAYMENT_RECORDED_SUCCESSFULLY, invoiceMapper.toResponse(saved));
    }

    @Override
    public ApiResponse<Void> deleteInvoice(UUID invoiceId) {
        log.info("Received request to delete invoice with ID: {}", invoiceId);

        Invoice invoice = findActive(invoiceId);
        invoice.getItems().size();
        restoreMedicineStock(invoice.getItems());
        invoice.setDeleted(true);
        invoiceRepository.save(invoice);

        log.info("Invoice deleted successfully. Invoice ID: {}", invoiceId);

        activityLogPublisher.record(
                "Billing",
                ActivityActionType.DELETED,
                "Invoice " + invoice.getInvoiceNumber());

        return ApiResponse.success(BillingMessages.INVOICE_DELETED_SUCCESSFULLY, null);
    }

    private void addLineItems(Invoice invoice, CreateInvoiceRequest request) {
        BigDecimal serviceFees = InvoiceCalculationUtil.money(request.getServiceFees());
        if (serviceFees.compareTo(BigDecimal.ZERO) > 0) {
            invoice.getItems().add(buildItem(
                    invoice,
                    InvoiceItemType.SERVICE,
                    "Service Fees",
                    1,
                    serviceFees,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null));
        }

        BigDecimal packageCharges = InvoiceCalculationUtil.money(invoice.getPackageCharges());
        if (packageCharges.compareTo(BigDecimal.ZERO) > 0) {
            String packageName = StringUtils.hasText(invoice.getPackageType())
                    ? invoice.getPackageType()
                    : "Package Charges";
            invoice.getItems().add(buildItem(
                    invoice,
                    InvoiceItemType.PACKAGE,
                    packageName,
                    1,
                    packageCharges,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null));
        }

        if (request.getMedicines() != null) {
            for (CreateInvoiceRequest.MedicineItemRequest item : request.getMedicines()) {
                invoice.getItems().add(toMedicineItem(invoice, item));
            }
        }

        if (request.getTherapies() != null) {
            for (CreateInvoiceRequest.TherapyItemRequest item : request.getTherapies()) {
                invoice.getItems().add(toTherapyItem(invoice, item));
            }
        }
    }

    private InvoiceItem toMedicineItem(Invoice invoice, CreateInvoiceRequest.MedicineItemRequest request) {
        MedicineClientResponse medicine = fetchMedicine(request.getMedicineId());

        int available = medicine.getStockQuantity() != null ? medicine.getStockQuantity() : 0;
        if (request.getQuantity() > available) {
            throw new BadRequestException(
                    BillingMessages.INSUFFICIENT_STOCK_PREFIX + medicine.getMedicineName()
                            + BillingMessages.INSUFFICIENT_STOCK_AVAILABLE_SUFFIX + available
                            + BillingMessages.INSUFFICIENT_STOCK_REQUESTED_SUFFIX + request.getQuantity());
        }

        BigDecimal unitPrice = request.getUnitPrice() != null
                ? request.getUnitPrice()
                : (medicine.getSellingPrice() != null ? medicine.getSellingPrice() : medicine.getPrice());

        if (unitPrice == null) {
            throw new BadRequestException(
                    BillingMessages.SELLING_PRICE_NOT_FOUND + medicine.getMedicineName());
        }

        return buildItem(
                invoice,
                InvoiceItemType.MEDICINE,
                medicine.getMedicineName(),
                request.getQuantity(),
                unitPrice,
                medicine.getId(),
                null,
                null,
                null,
                null,
                null,
                null);
    }

    /**
     * Ensures total billed quantity per medicine does not exceed available stock
     * (covers duplicate lines for the same medicineId).
     */
    private void validateMedicineStockAgainstRequest(List<InvoiceItem> items) {
        Map<UUID, Integer> requestedByMedicine = new HashMap<>();
        Map<UUID, String> namesByMedicine = new HashMap<>();

        for (InvoiceItem item : medicineItems(items)) {
            requestedByMedicine.merge(item.getMedicineId(), item.getQuantity(), Integer::sum);
            namesByMedicine.put(item.getMedicineId(), item.getItemName());
        }

        for (Map.Entry<UUID, Integer> entry : requestedByMedicine.entrySet()) {
            MedicineClientResponse medicine = fetchMedicine(entry.getKey());
            int available = medicine.getStockQuantity() != null ? medicine.getStockQuantity() : 0;
            int requested = entry.getValue();

            if (requested > available) {
                String name = namesByMedicine.getOrDefault(entry.getKey(), medicine.getMedicineName());
                throw new BadRequestException(
                        BillingMessages.INSUFFICIENT_STOCK_PREFIX + name
                                + BillingMessages.INSUFFICIENT_STOCK_AVAILABLE_SUFFIX + available
                                + BillingMessages.INSUFFICIENT_STOCK_REQUESTED_SUFFIX + requested);
            }
        }
    }

    private MedicineClientResponse fetchMedicine(UUID medicineId) {
        try {
            ApiResponse<MedicineClientResponse> response = medicineServiceClient.getMedicineById(medicineId);
            if (response == null || response.getData() == null) {
                throw new BadRequestException(BillingMessages.MEDICINE_NOT_FOUND_FOR_ID + medicineId);
            }
            return response.getData();
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException(BillingMessages.MEDICINE_NOT_FOUND_FOR_ID + medicineId);
        } catch (FeignException ex) {
            log.error("Failed to fetch medicine {}", medicineId, ex);
            throw new BadRequestException(BillingMessages.UNABLE_TO_LOAD_MEDICINE_DETAILS);
        }
    }

    private InvoiceItem toTherapyItem(Invoice invoice, CreateInvoiceRequest.TherapyItemRequest request) {
        return buildItem(
                invoice,
                InvoiceItemType.THERAPY,
                request.getItemName().trim(),
                request.getQuantity(),
                request.getUnitPrice(),
                null,
                request.getAssignedTherapistId(),
                request.getAssignedTherapistName(),
                request.getScheduleDate(),
                request.getScheduleTime(),
                request.getSessionDuration(),
                request.getSessionFrequency());
    }

    private InvoiceItem buildItem(
            Invoice invoice,
            InvoiceItemType itemType,
            String itemName,
            Integer quantity,
            BigDecimal unitPrice,
            UUID medicineId,
            UUID assignedTherapistId,
            String assignedTherapistName,
            java.time.LocalDate scheduleDate,
            java.time.LocalTime scheduleTime,
            Integer sessionDuration,
            Integer sessionFrequency) {

        BigDecimal amount = InvoiceCalculationUtil.money(unitPrice)
                .multiply(BigDecimal.valueOf(quantity));

        return InvoiceItem.builder()
                .invoice(invoice)
                .itemType(itemType)
                .itemName(itemName)
                .quantity(quantity)
                .unitPrice(InvoiceCalculationUtil.money(unitPrice))
                .amount(InvoiceCalculationUtil.money(amount))
                .medicineId(medicineId)
                .assignedTherapistId(assignedTherapistId)
                .assignedTherapistName(assignedTherapistName)
                .scheduleDate(scheduleDate)
                .scheduleTime(scheduleTime)
                .sessionDuration(sessionDuration)
                .sessionFrequency(sessionFrequency)
                .build();
    }

    private void applyTotals(Invoice invoice, BigDecimal cgstPercent, BigDecimal sgstPercent) {
        BigDecimal subtotal = invoice.getItems().stream()
                .map(InvoiceItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        subtotal = InvoiceCalculationUtil.money(subtotal);

        invoice.setSubtotal(subtotal);
        invoice.setDiscount(InvoiceCalculationUtil.money(invoice.getDiscount()));

        if (Boolean.TRUE.equals(invoice.getTaxEnabled())) {
            BigDecimal cgstPct = cgstPercent != null ? cgstPercent : defaultCgstPercent;
            BigDecimal sgstPct = sgstPercent != null ? sgstPercent : defaultSgstPercent;
            invoice.setCgstPercent(cgstPct);
            invoice.setSgstPercent(sgstPct);
            invoice.setCgstAmount(InvoiceCalculationUtil.percentOf(subtotal, cgstPct));
            invoice.setSgstAmount(InvoiceCalculationUtil.percentOf(subtotal, sgstPct));
        } else {
            invoice.setCgstPercent(null);
            invoice.setSgstPercent(null);
            invoice.setCgstAmount(BigDecimal.ZERO.setScale(2));
            invoice.setSgstAmount(BigDecimal.ZERO.setScale(2));
        }

        BigDecimal total = subtotal
                .add(InvoiceCalculationUtil.money(invoice.getCgstAmount()))
                .add(InvoiceCalculationUtil.money(invoice.getSgstAmount()))
                .subtract(InvoiceCalculationUtil.money(invoice.getDiscount()));

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(BillingMessages.DISCOUNT_GREATER_THAN_BILL_TOTAL);
        }

        invoice.setTotalAmount(InvoiceCalculationUtil.money(total));
    }

    private void applyPayment(Invoice invoice, BigDecimal amountPaid, String paymentMethod, String remarks) {
        BigDecimal payment = InvoiceCalculationUtil.money(amountPaid);
        if (payment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(BillingMessages.PAYMENT_AMOUNT_MUST_BE_POSITIVE);
        }

        BigDecimal currentPaid = InvoiceCalculationUtil.money(invoice.getPaidAmount());
        BigDecimal newPaid = currentPaid.add(payment);
        BigDecimal left = InvoiceCalculationUtil.leftAmount(invoice.getTotalAmount(), currentPaid);

        if (payment.compareTo(left) > 0) {
            throw new BadRequestException(BillingMessages.PAYMENT_EXCEEDS_LEFT_AMOUNT + left);
        }

        InvoicePayment paymentRecord = InvoicePayment.builder()
                .invoice(invoice)
                .amountPaid(payment)
                .paymentDate(LocalDateTime.now())
                .paymentMethod(paymentMethod)
                .remarks(remarks)
                .build();

        invoice.getPayments().add(paymentRecord);
        invoice.setPaidAmount(InvoiceCalculationUtil.money(newPaid));
        invoice.setLeftAmount(InvoiceCalculationUtil.leftAmount(invoice.getTotalAmount(), newPaid));
        invoice.setStatus(InvoiceCalculationUtil.resolveStatus(invoice.getTotalAmount(), newPaid));
    }

    private Invoice findActive(UUID invoiceId) {
        return invoiceRepository.findByIdAndDeletedFalse(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BillingMessages.INVOICE_NOT_FOUND_WITH_ID + invoiceId));
    }

    private void deductMedicineStock(List<InvoiceItem> items) {
        List<InvoiceItem> deducted = new ArrayList<>();
        try {
            for (InvoiceItem item : medicineItems(items)) {
                callDeduct(item.getMedicineId(), item.getQuantity(), item.getItemName());
                deducted.add(item);
            }
        } catch (RuntimeException ex) {
            for (InvoiceItem item : deducted) {
                try {
                    callRestore(item.getMedicineId(), item.getQuantity());
                } catch (Exception restoreEx) {
                    log.error("Failed to rollback stock for medicine {}", item.getMedicineId(), restoreEx);
                }
            }
            throw ex;
        }
    }

    private void restoreMedicineStock(List<InvoiceItem> items) {
        for (InvoiceItem item : medicineItems(items)) {
            try {
                callRestore(item.getMedicineId(), item.getQuantity());
            } catch (Exception ex) {
                log.error("Failed to restore stock for medicine {} on invoice delete", item.getMedicineId(), ex);
                throw new BadRequestException(
                        BillingMessages.FAILED_TO_RESTORE_STOCK + item.getItemName() + "'.");
            }
        }
    }

    private List<InvoiceItem> medicineItems(List<InvoiceItem> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .filter(item -> item.getItemType() == InvoiceItemType.MEDICINE)
                .filter(item -> item.getMedicineId() != null)
                .toList();
    }

    private void callDeduct(UUID medicineId, Integer quantity, String medicineName) {
        try {
            medicineServiceClient.deductStock(
                    medicineId,
                    StockAdjustClientRequest.builder().quantity(quantity).build());
        } catch (FeignException.BadRequest ex) {
            throw new BadRequestException(extractFeignMessage(ex, medicineName));
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException(BillingMessages.MEDICINE_NOT_FOUND_IN_INVENTORY + medicineName);
        } catch (FeignException ex) {
            log.error("Medicine stock deduct failed for {}", medicineId, ex);
            throw new BadRequestException(
                    BillingMessages.UNABLE_TO_UPDATE_MEDICINE_STOCK + medicineName + "'.");
        }
    }

    private void callRestore(UUID medicineId, Integer quantity) {
        medicineServiceClient.restoreStock(
                medicineId,
                StockAdjustClientRequest.builder().quantity(quantity).build());
    }

    private String extractFeignMessage(FeignException ex, String medicineName) {
        String body = ex.contentUTF8();
        if (StringUtils.hasText(body) && body.contains("\"message\"")) {
            int start = body.indexOf("\"message\"");
            int colon = body.indexOf(':', start);
            int firstQuote = body.indexOf('"', colon + 1);
            int secondQuote = body.indexOf('"', firstQuote + 1);
            if (firstQuote >= 0 && secondQuote > firstQuote) {
                return body.substring(firstQuote + 1, secondQuote);
            }
        }
        return BillingMessages.INSUFFICIENT_STOCK_FOR_MEDICINE_FALLBACK + medicineName + "'.";
    }

    private PackageMaster resolvePackageMaster(UUID packageMasterId) {
        if (packageMasterId == null) {
            return null;
        }
        return packageMasterRepository.findByIdAndDeletedFalse(packageMasterId)
                .orElse(null);
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

}
