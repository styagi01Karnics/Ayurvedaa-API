package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class BillingListResponse {

    private UUID id;
    private UUID patientId;
    private String patientName;
    private LocalDate billingDate;
    private BillingStatus status;
    private UUID invoiceId;
    private String invoiceNumber;
    private BigDecimal totalAmount;

}
