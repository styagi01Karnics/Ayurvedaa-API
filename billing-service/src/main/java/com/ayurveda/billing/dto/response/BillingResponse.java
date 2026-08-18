package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.enums.BillingStatus;

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
public class BillingResponse {

    private UUID id;
    private UUID patientId;
    private String patientName;
    private String contactNumber;
    private LocalDate billingDate;
    private BillingStatus status;
    private UUID invoiceId;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private List<BillingServiceItemResponse> services;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingServiceItemResponse {
        private UUID id;
        private String serviceType;
        private BigDecimal serviceFees;
        private UUID packageMasterId;
        private String packageName;
        private String packageType;
        private BigDecimal packageCharges;
    }

}
