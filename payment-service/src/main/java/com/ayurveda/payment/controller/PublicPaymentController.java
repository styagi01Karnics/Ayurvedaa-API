package com.ayurveda.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.payment.dto.response.PaymentLinkResponse;
import com.ayurveda.payment.dto.response.PaymentResponse;
import com.ayurveda.payment.service.PaymentLinkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payments / PayU")
@RestController
@RequestMapping("/api/v1/payments/public")
@RequiredArgsConstructor
@Validated
public class PublicPaymentController {

    private final PaymentLinkService paymentLinkService;

    @Operation(summary = "Get payment-link details (no login)")
    @GetMapping("/{token:.+}")
    public ResponseEntity<ApiResponse<PaymentLinkResponse>> get(
            @PathVariable String token,
            @RequestParam(required = false) String payPageBaseUrl) {
        paymentLinkService.bindTenantFromToken(token);
        try {
            return ResponseEntity.ok(paymentLinkService.getPublic(token, payPageBaseUrl));
        } finally {
            TenantContext.clear();
        }
    }

    @Operation(summary = "Start PayU checkout from a payment link (no login)")
    @PostMapping("/{token:.+}/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiate(
            @PathVariable String token,
            @RequestParam(required = false) String frontendSuccessUrl,
            @RequestParam(required = false) String frontendFailureUrl) {
        paymentLinkService.bindTenantFromToken(token);
        try {
            return ResponseEntity.ok(paymentLinkService.initiatePublic(token, frontendSuccessUrl, frontendFailureUrl));
        } finally {
            TenantContext.clear();
        }
    }
}
