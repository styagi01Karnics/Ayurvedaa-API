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
public class MonthlyRevenueResponse {

    private int year;
    private int month;
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalRevenue;
    private long invoiceCount;

}
