package com.ayurveda.billing.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.billing.constant.BillingMessages;
import com.ayurveda.billing.dto.request.CreateBillingRequest;
import com.ayurveda.billing.dto.request.CreateBillingRequest.BillingServiceItemRequest;
import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.dto.response.BillingListResponse;
import com.ayurveda.billing.dto.response.BillingResponse;
import com.ayurveda.billing.dto.response.BillingResponse.BillingServiceItemResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.entity.Billing;
import com.ayurveda.billing.entity.BillingServiceItem;
import com.ayurveda.billing.entity.PackageMaster;
import com.ayurveda.billing.enums.BillingStatus;
import com.ayurveda.billing.repository.BillingRepository;
import com.ayurveda.billing.repository.BillingServiceItemRepository;
import com.ayurveda.billing.repository.PackageMasterRepository;
import com.ayurveda.billing.service.BillingService;
import com.ayurveda.billing.service.InvoiceService;
import com.ayurveda.billing.util.InvoiceCalculationUtil;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final BillingServiceItemRepository billingServiceItemRepository;
    private final PackageMasterRepository packageMasterRepository;
    private final InvoiceService invoiceService;

    @Override
    public ApiResponse<BillingResponse> createBilling(CreateBillingRequest request) {
        log.info("Creating PENDING billing for patient: {}", request.getPatientId());

        Billing billing = Billing.builder()
                .patientId(request.getPatientId())
                .patientDisplayId(normalizeDisplayId(request.getPatientDisplayId()))
                .patientCode(trimToNull(request.getPatientCode()))
                .patientName(request.getPatientName().trim())
                .contactNumber(trimToNull(request.getContactNumber()))
                .billingDate(request.getBillingDate())
                .visitType(request.getVisitType())
                .status(BillingStatus.PENDING)
                .build();

        Billing saved = billingRepository.save(billing);
        List<BillingServiceItem> items = saveServiceItems(saved.getId(), request.getServices());

        log.info("Billing created successfully. Billing ID: {}, status: PENDING", saved.getId());
        return ApiResponse.success(BillingMessages.BILLING_CREATED, toResponse(saved, items));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<BillingResponse> getBillingById(UUID billingId) {
        Billing billing = findActive(billingId);
        List<BillingServiceItem> items =
                billingServiceItemRepository.findAllByBillingIdAndDeletedFalse(billingId);
        return ApiResponse.success(BillingMessages.BILLING_FETCHED, toResponse(billing, items));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<BillingListResponse>> getBillings(BillingStatus status) {
        List<Billing> billings = billingRepository.findAllByStatusOptional(status);
        Map<UUID, List<BillingServiceItem>> itemsByBilling = loadItemsGrouped(billings);

        List<BillingListResponse> responses = billings.stream()
                .map(billing -> {
                    List<BillingServiceItem> items =
                            itemsByBilling.getOrDefault(billing.getId(), List.of());
                    return BillingListResponse.builder()
                            .id(billing.getId())
                            .patientId(billing.getPatientId())
                            .patientDisplayId(billing.getPatientDisplayId())
                            .patientCode(billing.getPatientCode())
                            .patientName(billing.getPatientName())
                            .billingDate(billing.getBillingDate())
                            .visitType(billing.getVisitType())
                            .status(billing.getStatus())
                            .invoiceId(billing.getInvoiceId())
                            .invoiceNumber(billing.getInvoiceNumber())
                            .totalAmount(sumTotal(items))
                            .build();
                })
                .toList();

        return ApiResponse.success(BillingMessages.BILLINGS_FETCHED, responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<BillingResponse>> getBillingsByPatientId(UUID patientId) {
        List<Billing> billings = billingRepository
                .findAllByPatientIdAndDeletedFalseOrderByBillingDateDescCreatedAtDesc(patientId);
        Map<UUID, List<BillingServiceItem>> itemsByBilling = loadItemsGrouped(billings);

        List<BillingResponse> responses = billings.stream()
                .map(billing -> toResponse(
                        billing, itemsByBilling.getOrDefault(billing.getId(), List.of())))
                .toList();

        return ApiResponse.success(BillingMessages.BILLINGS_FETCHED, responses);
    }

    @Override
    public ApiResponse<InvoiceResponse> generateInvoice(
            UUID billingId, CreateInvoiceRequest request) {
        log.info("Generating invoice from billing: {}", billingId);

        if (request == null) {
            throw new BadRequestException("Invoice request body is required.");
        }

        Billing billing = findActive(billingId);
        if (billing.getStatus() == BillingStatus.COMPLETED) {
            throw new BadRequestException(BillingMessages.BILLING_ALREADY_COMPLETED);
        }

        List<BillingServiceItem> items =
                billingServiceItemRepository.findAllByBillingIdAndDeletedFalse(billingId);

        CreateInvoiceRequest invoiceRequest = mergeBillingIntoInvoiceRequest(billing, items, request);
        ApiResponse<InvoiceResponse> invoiceResponse = invoiceService.createInvoice(invoiceRequest);
        InvoiceResponse invoice = invoiceResponse.getData();

        billing.setStatus(BillingStatus.COMPLETED);
        billing.setInvoiceId(invoice.getId());
        billing.setInvoiceNumber(invoice.getInvoiceId());
        billingRepository.save(billing);

        log.info(
                "Invoice generated from billing {}. Invoice number: {}. Billing status: COMPLETED",
                billingId,
                invoice.getInvoiceId());

        return ApiResponse.success(BillingMessages.BILLING_INVOICE_GENERATED, invoice);
    }

    private CreateInvoiceRequest mergeBillingIntoInvoiceRequest(
            Billing billing,
            List<BillingServiceItem> items,
            CreateInvoiceRequest request) {

        Map<UUID, PackageMaster> packagesById = loadPackages(items);

        BigDecimal billingServiceFees = BigDecimal.ZERO;
        BigDecimal billingPackageCharges = BigDecimal.ZERO;
        List<String> packageTypes = new ArrayList<>();

        for (BillingServiceItem item : items) {
            billingServiceFees =
                    billingServiceFees.add(InvoiceCalculationUtil.money(item.getServiceFees()));

            PackageMaster packageMaster =
                    item.getPackageMasterId() != null
                            ? packagesById.get(item.getPackageMasterId())
                            : null;

            BigDecimal charges = item.getPackageCharges();
            if (charges == null && packageMaster != null) {
                charges = packageMaster.getPackagePrice();
            }
            billingPackageCharges =
                    billingPackageCharges.add(InvoiceCalculationUtil.money(charges));

            String packageLabel = item.getPackageType();
            if (!StringUtils.hasText(packageLabel) && packageMaster != null) {
                packageLabel = packageMaster.getName();
            }
            if (StringUtils.hasText(packageLabel)) {
                packageTypes.add(packageLabel.trim());
            }
        }

        String billingPackageType = packageTypes.isEmpty() ? null : String.join(", ", packageTypes);

        return CreateInvoiceRequest.builder()
                .patientId(request.getPatientId() != null ? request.getPatientId() : billing.getPatientId())
                .patientDisplayId(firstText(request.getPatientDisplayId(), billing.getPatientDisplayId()))
                .patientCode(firstText(request.getPatientCode(), billing.getPatientCode()))
                .patientName(firstText(request.getPatientName(), billing.getPatientName()))
                .contactNumber(firstText(request.getContactNumber(), billing.getContactNumber()))
                .invoiceDate(request.getInvoiceDate() != null
                        ? request.getInvoiceDate()
                        : billing.getBillingDate())
                .visitType(request.getVisitType() != null ? request.getVisitType() : billing.getVisitType())
                .serviceFees(request.getServiceFees() != null
                        ? request.getServiceFees()
                        : billingServiceFees)
                .packageType(firstText(request.getPackageType(), billingPackageType))
                .packageCharges(request.getPackageCharges() != null
                        ? request.getPackageCharges()
                        : billingPackageCharges)
                .medicines(request.getMedicines())
                .therapies(request.getTherapies())
                .discount(request.getDiscount())
                .taxEnabled(request.getTaxEnabled())
                .cgstPercent(request.getCgstPercent())
                .sgstPercent(request.getSgstPercent())
                .amountPaid(request.getAmountPaid())
                .paymentMethod(request.getPaymentMethod())
                .paymentRemarks(request.getPaymentRemarks())
                .build();
    }

    private List<BillingServiceItem> saveServiceItems(
            UUID billingId, List<BillingServiceItemRequest> requests) {

        List<BillingServiceItem> entities = new ArrayList<>();
        for (BillingServiceItemRequest request : requests) {
            PackageMaster packageMaster = null;
            if (request.getPackageMasterId() != null) {
                packageMaster = packageMasterRepository
                        .findByIdAndDeletedFalse(request.getPackageMasterId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                BillingMessages.PACKAGE_MASTER_NOT_FOUND));
            }

            String packageType = trimToNull(request.getPackageType());
            if (packageType == null && packageMaster != null) {
                packageType = packageMaster.getName();
            }

            BigDecimal packageCharges = request.getPackageCharges();
            if (packageCharges == null && packageMaster != null) {
                packageCharges = packageMaster.getPackagePrice();
            }

            entities.add(BillingServiceItem.builder()
                    .billingId(billingId)
                    .serviceType(trimToNull(request.getServiceType()))
                    .serviceFees(InvoiceCalculationUtil.money(request.getServiceFees()))
                    .packageMasterId(request.getPackageMasterId())
                    .packageType(packageType)
                    .packageCharges(InvoiceCalculationUtil.money(packageCharges))
                    .build());
        }
        return billingServiceItemRepository.saveAll(entities);
    }

    private Billing findActive(UUID billingId) {
        return billingRepository.findByIdAndDeletedFalse(billingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BillingMessages.BILLING_NOT_FOUND_WITH_ID + billingId));
    }

    private Map<UUID, List<BillingServiceItem>> loadItemsGrouped(List<Billing> billings) {
        if (billings.isEmpty()) {
            return Map.of();
        }
        List<UUID> ids = billings.stream().map(Billing::getId).toList();
        return billingServiceItemRepository.findAllByBillingIdInAndDeletedFalse(ids).stream()
                .collect(Collectors.groupingBy(BillingServiceItem::getBillingId));
    }

    private Map<UUID, PackageMaster> loadPackages(List<BillingServiceItem> items) {
        Set<UUID> packageIds = items.stream()
                .map(BillingServiceItem::getPackageMasterId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));

        if (packageIds.isEmpty()) {
            return Map.of();
        }

        return packageMasterRepository.findByIdInAndDeletedFalse(packageIds).stream()
                .collect(Collectors.toMap(PackageMaster::getId, Function.identity()));
    }

    private BillingResponse toResponse(Billing billing, List<BillingServiceItem> items) {
        Map<UUID, PackageMaster> packagesById = loadPackages(items);

        return BillingResponse.builder()
                .id(billing.getId())
                .patientId(billing.getPatientId())
                .patientDisplayId(billing.getPatientDisplayId())
                .patientCode(billing.getPatientCode())
                .patientName(billing.getPatientName())
                .contactNumber(billing.getContactNumber())
                .billingDate(billing.getBillingDate())
                .visitType(billing.getVisitType())
                .status(billing.getStatus())
                .invoiceId(billing.getInvoiceId())
                .invoiceNumber(billing.getInvoiceNumber())
                .totalAmount(sumTotal(items))
                .services(items.stream()
                        .map(item -> {
                            PackageMaster packageMaster =
                                    item.getPackageMasterId() != null
                                            ? packagesById.get(item.getPackageMasterId())
                                            : null;
                            return BillingServiceItemResponse.builder()
                                    .id(item.getId())
                                    .serviceType(item.getServiceType())
                                    .serviceFees(item.getServiceFees())
                                    .packageMasterId(item.getPackageMasterId())
                                    .packageName(packageMaster != null
                                            ? packageMaster.getName()
                                            : item.getPackageType())
                                    .packageType(item.getPackageType())
                                    .packageCharges(item.getPackageCharges())
                                    .build();
                        })
                        .toList())
                .createdAt(billing.getCreatedAt())
                .updatedAt(billing.getUpdatedAt())
                .build();
    }

    private BigDecimal sumTotal(List<BillingServiceItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (BillingServiceItem item : items) {
            total = total
                    .add(InvoiceCalculationUtil.money(item.getServiceFees()))
                    .add(InvoiceCalculationUtil.money(item.getPackageCharges()));
        }
        return total;
    }

    private String firstText(String preferred, String fallback) {
        if (StringUtils.hasText(preferred)) {
            return preferred.trim();
        }
        return trimToNull(fallback);
    }

    private String normalizeDisplayId(String displayId) {
        if (!StringUtils.hasText(displayId)) {
            return null;
        }
        String trimmed = displayId.trim();
        return trimmed.startsWith("#") ? trimmed : "#" + trimmed;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

}
