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
import com.ayurveda.billing.dto.request.RefundInvoiceRequest;
import com.ayurveda.billing.dto.response.InvoiceListResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.billing.service.InvoiceService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;

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
                    Each Make Payment call creates its own invoice ({tenantCode}-INV-#####).
                    """)
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.createInvoice(request));
    }

    @Operation(
            summary = "List invoices (paginated)",
            description = "Search by patient ID / code and filter by payment status (UNPAID, ONGOING, COMPLETED). page/size supported.")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceListResponse>>> getInvoices(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(invoiceService.getInvoices(patientId, status, page, size));
    }

    @Operation(
            summary = "List invoices by patient id (paginated)",
            description = "Fetches invoices for the given patient UUID. Optional status filter: UNPAID, ONGOING, COMPLETED.")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceListResponse>>> getInvoicesByPatientId(
            @PathVariable UUID patientId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(invoiceService.getInvoicesByPatientId(patientId, status, page, size));
    }

    @Operation(summary = "Get invoice by id")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    @Operation(
            summary = "Record cash or part payment",
            description = """
                    Adds an in-hand cash payment toward the invoice.
                    Cash (paymentMethod=CASH; omit amount or send full left) marks the invoice COMPLETED.
                    A smaller cash amount leaves status ONGOING — then use payment-service for Online/QR balance.
                    ONLINE and QR are not accepted here; create a PayU payment link (email or shop qrPayload).
                    PayU success is applied automatically from payment-service (paymentMethod=PAYU).
                    """)
    @PostMapping("/{invoiceId}/payments")
    public ResponseEntity<ApiResponse<InvoiceResponse>> recordPartPayment(
            @PathVariable UUID invoiceId,
            @Valid @RequestBody PartPaymentRequest request) {

        return ResponseEntity.ok(invoiceService.recordPartPayment(invoiceId, request));
    }

    @Operation(
            summary = "Record cash refund",
            description = """
                    Returns cash in-hand to the patient and reduces invoice paidAmount.
                    Omit amount to refund the full paid balance. Recalculates leftAmount and status
                    (COMPLETED → PARTIAL / UNPAID). Optional patientEmail sends a refund confirmation
                    via the hospital mailbox. For Online/QR PayU refunds use payment-service
                    POST /api/v1/payments/{paymentId}/refund.
                    """)
    @PostMapping("/{invoiceId}/refunds")
    public ResponseEntity<ApiResponse<InvoiceResponse>> recordCashRefund(
            @PathVariable UUID invoiceId,
            @Valid @RequestBody RefundInvoiceRequest request) {

        return ResponseEntity.ok(invoiceService.recordCashRefund(invoiceId, request));
    }

    @Operation(summary = "Soft delete invoice")
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(invoiceService.deleteInvoice(invoiceId));
    }

}
