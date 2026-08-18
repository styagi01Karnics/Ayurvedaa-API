package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.enums.BillSection;
import com.ayurveda.billing.enums.InvoiceItemType;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.billing.enums.VisitType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private UUID id;
    private String invoiceId;
    private UUID patientId;
    private String patientDisplayId;
    private String formattedPatientId;
    private String patientCode;
    private String patientName;
    private String contactNumber;
    private LocalDate invoiceDate;
    private VisitType visitType;
    private BigDecimal serviceFees;
    private UUID packageMasterId;
    private String packageName;
    private String packageType;
    private BigDecimal packageCharges;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private Boolean taxEnabled;
    private BigDecimal cgstPercent;
    private BigDecimal cgstAmount;
    private BigDecimal sgstPercent;
    private BigDecimal sgstAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal leftAmount;
    private InvoiceStatus status;
    /** Sections included in this separate bill (any combination). */
    private List<BillSection> billSections;
    private List<InvoiceItemResponse> items;
    private List<InvoicePaymentResponse> payments;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemResponse {
        private UUID id;
        private InvoiceItemType itemType;
        private String itemName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal amount;
        private UUID medicineId;
        private UUID assignedTherapistId;
        private String assignedTherapistName;
        private LocalDate scheduleDate;
        private LocalTime scheduleTime;
        private Integer sessionDuration;
        private Integer sessionFrequency;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoicePaymentResponse {
        private UUID id;
        private BigDecimal amountPaid;
        private java.time.LocalDateTime paymentDate;
        private String paymentMethod;
        private String remarks;
    }

}
