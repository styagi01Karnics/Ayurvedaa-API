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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;
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
                    - Online / room: set sendEmail=true (patient gets payUrl). Status becomes SHARED.
                    - At hospital QR: set upiQr=true — qrPayload becomes upi://pay?...&am=AMOUNT (scan opens UPI with exact amount).
                      Requires PayU Dynamic QR (DBQR) on the merchant. payUrl remains web fallback.
                    Link stays active for 15 minutes, then EXPIRED. Amount must be <= invoice leftAmount.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentLinkResponse>> create(
            @Valid @RequestBody CreatePaymentLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentLinkService.create(request));
    }

    @Operation(
            summary = "List payment links sent to patients",
            description = """
                    Hospital JWT only. Newest first.
                    Status filter (optional): SHARED | PAID | EXPIRED | SUPERSEDED
                    - SHARED = link shared / waiting for payment (includes legacy OPEN)
                    - PAID = patient paid
                    - EXPIRED = unpaid after 15 minutes
                    Also filter by patientId, invoiceId, or search (name / email / phone / invoice number).
                    """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PaymentLinkResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID invoiceId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(paymentLinkService.list(page, size, status, patientId, invoiceId, search));
    }

    @Operation(
            summary = "Get the active (SHARED) payment link for an invoice",
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
                    Sets status SHARED and sharedAt. Link remains valid for 15 minutes from create.
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
