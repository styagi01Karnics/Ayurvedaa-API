package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

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
public class SalesInvoiceResponse {

    private String invoiceId;
    private LocalDate invoiceDate;
    private String treatmentCategory;
    private String serviceType;
    private BigDecimal totalAmount;

}
