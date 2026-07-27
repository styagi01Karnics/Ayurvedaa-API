package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ayurveda.billing.enums.BillingPeriod;

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
public class DashboardBillingSummaryResponse {

    private BillingPeriod period;
    private LocalDate fromDate;
    private LocalDate toDate;
    /** Main dashboard amount (sum of invoice totals). */
    private BigDecimal totalRevenue;
    /** Total Bills Generated */
    private long totalBillsGenerated;
    /** Pending Payments (sum of leftAmount) */
    private BigDecimal pendingPayments;
    /** Collected Payments (sum of paidAmount) */
    private BigDecimal collectedPayments;

}
