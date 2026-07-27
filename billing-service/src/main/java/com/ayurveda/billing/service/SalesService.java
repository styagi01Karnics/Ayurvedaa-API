package com.ayurveda.billing.service;

import com.ayurveda.billing.dto.response.DashboardBillingSummaryResponse;
import com.ayurveda.billing.dto.response.MonthlyRevenueResponse;
import com.ayurveda.billing.dto.response.SalesPageResponse;
import com.ayurveda.billing.enums.BillingPeriod;
import com.ayurveda.common.ApiResponse;

import java.time.LocalDate;

public interface SalesService {

    ApiResponse<SalesPageResponse> getSales(String serviceType, LocalDate dateCreated);

    ApiResponse<MonthlyRevenueResponse> getMonthlyRevenue(Integer year, Integer month);

    /** Dashboard page – Billing card summary. */
    ApiResponse<DashboardBillingSummaryResponse> getDashboardBillingSummary(BillingPeriod period);

}
