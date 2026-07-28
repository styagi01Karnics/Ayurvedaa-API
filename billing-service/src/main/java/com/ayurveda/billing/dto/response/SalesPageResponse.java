package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
public class SalesPageResponse {

    private BigDecimal revenueThisMonth;
    private LocalDate revenueFrom;
    private LocalDate revenueTo;
    private List<SalesInvoiceResponse> sales;

}
