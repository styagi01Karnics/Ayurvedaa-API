package com.ayurveda.payment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.payment.dto.request.CreatePaymentLinkRequest;
import com.ayurveda.payment.dto.request.SendPaymentLinkEmailRequest;
import com.ayurveda.payment.dto.response.PaymentLinkResponse;
import com.ayurveda.payment.service.PaymentLinkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payments / PayU")
@RestController
@RequestMapping("/api/v1/payments/links")
@RequiredArgsConstructor
@Validated
public class PaymentLinkController {

    private final PaymentLinkService paymentLinkService;

    @Operation(
            summary = "Create a payment link / POS QR payload for an invoice balance",
            description = """
                    Available to any authenticated hospital user (JWT with hospital schema) — not Super Admin only.
                    Use after invoice generate (full amount) or after a partial cash payment (remaining amount).
                    - Online / room: set sendEmail=true (patient gets payUrl).
                    - At hospital QR: set upiQr=true — qrPayload becomes upi://pay?...&am=AMOUNT (scan opens UPI with exact amount).
                      Requires PayU Dynamic QR (DBQR) on the merchant. payUrl remains web fallback.
                    Amount must be <= invoice leftAmount.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentLinkResponse>> create(
            @Valid @RequestBody CreatePaymentLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentLinkService.create(request));
    }

    @Operation(
            summary = "Get the open payment link for an invoice",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<PaymentLinkResponse>> getOpenByInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(paymentLinkService.getOpenByInvoice(invoiceId));
    }

    @Operation(
            summary = "Get a payment link by id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentLinkResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentLinkService.getById(id));
    }

    @Operation(
            summary = "Email a payment link to the patient",
            description = """
                    Any authenticated hospital user can share the PayU link (hospital SMTP mailbox).
                    Optional body.email overrides the address stored on the link.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{id}/email")
    public ResponseEntity<ApiResponse<PaymentLinkResponse>> email(
            @PathVariable UUID id,
            @RequestBody(required = false) SendPaymentLinkEmailRequest request) {
        String override = request != null ? request.getEmail() : null;
        return ResponseEntity.ok(paymentLinkService.email(id, override));
    }
}
