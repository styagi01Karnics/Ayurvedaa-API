package com.ayurveda.payment.controller;

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

import com.ayurveda.common.ApiResponse;
import com.ayurveda.payment.dto.request.InitiatePaymentRequest;
import com.ayurveda.payment.dto.request.RefundPaymentRequest;
import com.ayurveda.payment.dto.response.PaymentResponse;
import com.ayurveda.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payments / PayU", description = "Initiate PayU hosted checkout and query payment status")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Initiate PayU payment",
            description = """
                    Creates a hospital payment and returns PayU form fields.
                    Frontend should POST `payuParams` to `actionUrl` (PayU hosted checkout).
                    After pay, PayU calls /api/v1/payments/payu/success or /failure.
                    """)
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiate(
            @Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiate(request));
    }

    @Operation(summary = "List payments", description = "Optional filter by invoiceId.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> list(
            @RequestParam(required = false) UUID invoiceId) {
        return ResponseEntity.ok(paymentService.list(invoiceId));
    }

    @Operation(summary = "Get payment by PayU txnid")
    @GetMapping("/by-txn/{txnId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByTxnId(@PathVariable String txnId) {
        return ResponseEntity.ok(paymentService.getByTxnId(txnId));
    }

    @Operation(summary = "Get payment by id")
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getById(paymentId));
    }

    @Operation(
            summary = "Refund a successful PayU payment (Online / QR)",
            description = """
                    Calls PayU cancel_refund_transaction (full or partial).
                    Any authenticated hospital user may refund.
                    On acceptance: updates payment refundedAmount/status, publishes Kafka so billing
                    recalculates invoice left amount, and emails the patient.
                    Cash refunds use billing POST /api/v1/invoices/{invoiceId}/refunds.
                    """)
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(
            @PathVariable UUID paymentId,
            @RequestBody(required = false) @Valid RefundPaymentRequest request) {
        return ResponseEntity.ok(paymentService.refund(
                paymentId, request != null ? request : new RefundPaymentRequest()));
    }
}
