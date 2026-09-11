package com.ayurveda.payment.dto.client;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Minimal invoice fields from billing-service used by payment-service. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceClientResponse {

    private UUID id;
    private BigDecimal leftAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
}
