package com.ayurveda.billing.service;

import com.ayurveda.billing.dto.response.DashboardBillingSummaryResponse;
import com.ayurveda.billing.dto.response.MonthlyRevenueResponse;
import com.ayurveda.billing.dto.response.SalesPageResponse;
import com.ayurveda.billing.enums.BillingPeriod;
import com.ayurveda.common.ApiResponse;

import java.time.LocalDate;

public interface SalesService {

    /** Sales page list with optional service type and date filters. */
    ApiResponse<SalesPageResponse> getSales(String serviceType, LocalDate dateCreated);

    /** Monthly revenue for the given year/month (defaults to current when null). */
    ApiResponse<MonthlyRevenueResponse> getMonthlyRevenue(Integer year, Integer month);

    /** Dashboard page – Billing card summary. */
    ApiResponse<DashboardBillingSummaryResponse> getDashboardBillingSummary(BillingPeriod period);

}
