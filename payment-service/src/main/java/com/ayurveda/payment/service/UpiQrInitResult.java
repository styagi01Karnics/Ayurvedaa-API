package com.ayurveda.payment.service;

import com.ayurveda.payment.dto.response.PaymentResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpiQrInitResult {

    private PaymentResponse payment;
    /** Encode this as the on-screen QR — opens UPI with exact amount. */
    private String qrPayload;
    private String payuPaymentId;
}
