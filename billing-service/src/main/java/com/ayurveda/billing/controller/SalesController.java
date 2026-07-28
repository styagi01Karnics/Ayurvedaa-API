package com.ayurveda.billing.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.billing.dto.response.MonthlyRevenueResponse;
import com.ayurveda.billing.dto.response.SalesPageResponse;
import com.ayurveda.billing.service.SalesService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Sales", description = "Sales page APIs (invoice table and monthly revenue). Not for Dashboard Billing card.")
@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Validated
public class SalesController {

    private final SalesService salesService;

    @Operation(
            summary = "Sales page – invoice list with revenue this month",
            description = """
                    For the Sales page (not Dashboard).

                    Returns sales rows (Invoice ID, Date, Treatment Category, Service Type, Total Amount)
                    plus revenue for the current month.
                    Filters: serviceType, dateCreated.
                    Treatment category is resolved via invoice → patientId → appointment therapy;
                    if not found, it is left null.
                    """)
    @GetMapping
    public ResponseEntity<ApiResponse<SalesPageResponse>> getSales(
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateCreated) {

        return ResponseEntity.ok(salesService.getSales(serviceType, dateCreated));
    }

    @Operation(
            summary = "Sales page – total revenue for a month",
            description = "For the Sales page revenue card. Defaults to current year/month when year or month is omitted.")
    @GetMapping("/revenue/month")
    public ResponseEntity<ApiResponse<MonthlyRevenueResponse>> getMonthlyRevenue(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        return ResponseEntity.ok(salesService.getMonthlyRevenue(year, month));
    }

}
