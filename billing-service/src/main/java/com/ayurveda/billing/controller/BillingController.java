package com.ayurveda.billing.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.billing.dto.request.CreateBillingRequest;
import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.dto.response.BillingListResponse;
import com.ayurveda.billing.dto.response.BillingResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.enums.BillingStatus;
import com.ayurveda.billing.service.BillingService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Billing", description = "Doctor PENDING billing + receptionist invoice generation")
@RestController
@RequestMapping("/api/v1/billings")
@RequiredArgsConstructor
@Validated
public class BillingController {

    private final BillingService billingService;

    @Operation(
            summary = "Create billing (doctor)",
            description = "Services only. Status PENDING. No invoice ID. No discount/GST/medicine.")
    @PostMapping
    public ResponseEntity<ApiResponse<BillingResponse>> createBilling(
            @Valid @RequestBody CreateBillingRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(billingService.createBilling(request));
    }

    @Operation(summary = "List billings", description = "Filter: PENDING or COMPLETED")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BillingListResponse>>> getBillings(
            @RequestParam(required = false) BillingStatus status) {

        return ResponseEntity.ok(billingService.getBillings(status));
    }

    @Operation(summary = "Get billings by patient id")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<BillingResponse>>> getBillingsByPatientId(
            @PathVariable UUID patientId) {

        return ResponseEntity.ok(billingService.getBillingsByPatientId(patientId));
    }

    @Operation(summary = "Get billing by id")
    @GetMapping("/{billingId}")
    public ResponseEntity<ApiResponse<BillingResponse>> getBillingById(
            @PathVariable UUID billingId) {

        return ResponseEntity.ok(billingService.getBillingById(billingId));
    }

    @Operation(
            summary = "Generate invoice from billing (receptionist)",
            description = """
                    Same request body as POST /api/v1/invoices.
                    Add medicines, therapies, discount, GST here.
                    Patient/service can be sent in body or taken from PENDING billing.
                    Marks billing COMPLETED and saves invoiceId.
                    """)
    @PostMapping("/{billingId}/generate-invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> generateInvoice(
            @PathVariable UUID billingId,
            @Valid @RequestBody CreateInvoiceRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(billingService.generateInvoice(billingId, request));
    }

}
