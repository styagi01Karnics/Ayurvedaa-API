package com.ayurveda.billing.controller;

import com.ayurveda.billing.dto.request.CreateBillingRequest;
import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.dto.response.BillingResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.enums.BillingStatus;
import com.ayurveda.billing.service.BillingService;
import com.ayurveda.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.UUID;

@Tag(name = "Billing", description = "Doctor PENDING billing + receptionist invoice generation")
@RestController
@RequestMapping("/api/v1/billings")
@RequiredArgsConstructor
@Slf4j
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

    @Operation(
            summary = "List billings (paginated)",
            description = "Filter: PENDING or COMPLETED. page/size supported.")
    @GetMapping()
    public ResponseEntity<?> getBillings(@RequestParam(required = false) BillingStatus status, @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {

        if (page < 0) {
            return ResponseEntity.badRequest().body("Page index must not be less than zero.");
        }

        if (size <= 0) {
            return ResponseEntity.badRequest().body("Page size must be greater than zero.");
        }

        if (size > 100) {
            return ResponseEntity.badRequest().body("Page size must not exceed 100.");
        }

        log.info("Received request to fetch billing details page={}, size={}", page, size);

        return ResponseEntity.ok(ResponseEntity.ok(billingService.getBillings(status, page, size)));
    }

    @Operation(summary = "Get billings by patient id (paginated)")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getBillingsByPatientId(@PathVariable UUID patientId, @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {

        if (page < 0) {
            return ResponseEntity.badRequest().body("Page index must not be less than zero.");
        }

        if (size <= 0) {
            return ResponseEntity.badRequest().body("Page size must be greater than zero.");
        }

        if (size > 100) {
            return ResponseEntity.badRequest().body("Page size must not exceed 100.");
        }

        log.info("Received request to fetch billing details by patient id={}, page={}, size={}", patientId, page, size);

        return ResponseEntity.ok(billingService.getBillingsByPatientId(patientId, page, size));
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
