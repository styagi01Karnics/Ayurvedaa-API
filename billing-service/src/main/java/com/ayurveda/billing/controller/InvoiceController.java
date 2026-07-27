package com.ayurveda.billing.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.dto.request.PartPaymentRequest;
import com.ayurveda.billing.dto.response.InvoiceListResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.billing.service.InvoiceService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Billing / Invoices", description = "Invoice generation and part-payment APIs")
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Validated
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(
            summary = "Generate invoice (separate bill)",
            description = """
                    Creates a separate bill from whatever sections are sent.
                    Supported combinations:
                    - Service Type only
                    - Medicine only
                    - Therapy only
                    - Service + Medicine
                    - Service + Therapy
                    - Medicine + Therapy
                    - Service + Medicine + Therapy (all together)
                    Each Make Payment call creates its own invoice (INV-xxxx).
                    """)
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.createInvoice(request));
    }

    @Operation(
            summary = "List invoices",
            description = "Search by patient ID / code and filter by payment status (UNPAID, ONGOING, COMPLETED).")
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceListResponse>>> getInvoices(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) InvoiceStatus status) {

        return ResponseEntity.ok(invoiceService.getInvoices(patientId, status));
    }

    @Operation(summary = "Get invoice by id")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    @Operation(
            summary = "Record part payment",
            description = "Adds a payment toward the invoice. Status becomes ONGOING until fully paid, then COMPLETED.")
    @PostMapping("/{invoiceId}/payments")
    public ResponseEntity<ApiResponse<InvoiceResponse>> recordPartPayment(
            @PathVariable UUID invoiceId,
            @Valid @RequestBody PartPaymentRequest request) {

        return ResponseEntity.ok(invoiceService.recordPartPayment(invoiceId, request));
    }

    @Operation(summary = "Soft delete invoice")
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(invoiceService.deleteInvoice(invoiceId));
    }

}
