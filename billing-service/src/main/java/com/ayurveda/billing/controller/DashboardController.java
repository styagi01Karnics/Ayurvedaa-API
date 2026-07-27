package com.ayurveda.billing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.billing.dto.response.DashboardBillingSummaryResponse;
import com.ayurveda.billing.enums.BillingPeriod;
import com.ayurveda.billing.service.SalesService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(
        name = "Dashboard",
        description = "APIs used on the main Dashboard page (Billing card and related widgets).")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Validated
public class DashboardController {

    private final SalesService salesService;

    @Operation(
            summary = "Dashboard – Billing card summary",
            description = """
                    For the Dashboard page Billing widget.

                    Returns:
                    - totalRevenue (large amount shown on card)
                    - totalBillsGenerated
                    - pendingPayments
                    - collectedPayments

                    Period dropdown: WEEKLY | MONTHLY (default) | YEARLY.
                    """)
    @GetMapping("/billing-summary")
    public ResponseEntity<ApiResponse<DashboardBillingSummaryResponse>> getBillingSummary(
            @RequestParam(required = false, defaultValue = "MONTHLY") BillingPeriod period) {

        return ResponseEntity.ok(salesService.getDashboardBillingSummary(period));
    }

}
