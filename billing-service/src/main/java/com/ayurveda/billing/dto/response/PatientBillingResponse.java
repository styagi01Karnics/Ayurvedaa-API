package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
public class PatientBillingResponse {

    private UUID patientId;
    private BillingSummary summary;
    private List<InvoiceListResponse> invoices;
    private List<PatientPackageResponse> packages;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingSummary {
        private int invoiceCount;
        private int unpaidCount;
        private int ongoingCount;
        private int completedCount;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal leftAmount;
        private int packageCount;
    }

}
