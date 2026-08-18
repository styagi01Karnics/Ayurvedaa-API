package com.ayurveda.billing.mapper;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ayurveda.billing.dto.response.InvoiceListResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.entity.Invoice;
import com.ayurveda.billing.entity.InvoiceItem;
import com.ayurveda.billing.entity.InvoicePayment;
import com.ayurveda.billing.entity.PackageMaster;
import com.ayurveda.billing.repository.PackageMasterRepository;
import com.ayurveda.billing.util.BillSectionResolver;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvoiceMapper {

    private final PackageMasterRepository packageMasterRepository;

    public InvoiceListResponse toListResponse(Invoice invoice) {
        return InvoiceListResponse.builder()
                .id(invoice.getId())
                .invoiceId(invoice.getInvoiceNumber())
                .patientId(invoice.getPatientId())
                .patientName(invoice.getPatientName())
                .invoiceDate(invoice.getInvoiceDate())
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .leftAmount(invoice.getLeftAmount())
                .status(invoice.getStatus())
                .billSections(BillSectionResolver.fromStorage(invoice.getBillSections()))
                .build();
    }

    public InvoiceResponse toResponse(Invoice invoice) {
        PackageMaster packageMaster = resolvePackageMaster(invoice.getPackageMasterId());
        String packageName = packageMaster != null
                ? packageMaster.getName()
                : invoice.getPackageType();

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceId(invoice.getInvoiceNumber())
                .patientId(invoice.getPatientId())
                .patientName(invoice.getPatientName())
                .contactNumber(invoice.getContactNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .visitType(invoice.getVisitType())
                .serviceFees(invoice.getServiceFees())
                .packageMasterId(invoice.getPackageMasterId())
                .packageName(StringUtils.hasText(packageName) ? packageName : null)
                .packageType(invoice.getPackageType())
                .packageCharges(invoice.getPackageCharges())
                .subtotal(invoice.getSubtotal())
                .discount(invoice.getDiscount())
                .taxEnabled(invoice.getTaxEnabled())
                .cgstPercent(invoice.getCgstPercent())
                .cgstAmount(invoice.getCgstAmount())
                .sgstPercent(invoice.getSgstPercent())
                .sgstAmount(invoice.getSgstAmount())
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .leftAmount(invoice.getLeftAmount())
                .status(invoice.getStatus())
                .billSections(BillSectionResolver.fromStorage(invoice.getBillSections()))
                .items(mapItems(invoice.getItems()))
                .payments(mapPayments(invoice.getPayments()))
                .build();
    }

    private PackageMaster resolvePackageMaster(java.util.UUID packageMasterId) {
        if (packageMasterId == null) {
            return null;
        }
        return packageMasterRepository.findByIdAndDeletedFalse(packageMasterId).orElse(null);
    }

    private List<InvoiceResponse.InvoiceItemResponse> mapItems(List<InvoiceItem> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> InvoiceResponse.InvoiceItemResponse.builder()
                        .id(item.getId())
                        .itemType(item.getItemType())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .amount(item.getAmount())
                        .medicineId(item.getMedicineId())
                        .assignedTherapistId(item.getAssignedTherapistId())
                        .assignedTherapistName(item.getAssignedTherapistName())
                        .scheduleDate(item.getScheduleDate())
                        .scheduleTime(item.getScheduleTime())
                        .sessionDuration(item.getSessionDuration())
                        .sessionFrequency(item.getSessionFrequency())
                        .build())
                .toList();
    }

    private List<InvoiceResponse.InvoicePaymentResponse> mapPayments(List<InvoicePayment> payments) {
        if (payments == null) {
            return List.of();
        }
        return payments.stream()
                .map(payment -> InvoiceResponse.InvoicePaymentResponse.builder()
                        .id(payment.getId())
                        .amountPaid(payment.getAmountPaid())
                        .paymentDate(payment.getPaymentDate())
                        .paymentMethod(payment.getPaymentMethod())
                        .remarks(payment.getRemarks())
                        .build())
                .toList();
    }

}
