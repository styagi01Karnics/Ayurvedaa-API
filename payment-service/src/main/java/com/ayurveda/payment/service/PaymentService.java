package com.ayurveda.payment.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.payment.dto.request.InitiatePaymentRequest;
import com.ayurveda.payment.dto.request.RefundPaymentRequest;
import com.ayurveda.payment.dto.response.PaymentResponse;

public interface PaymentService {

    ApiResponse<PaymentResponse> initiate(InitiatePaymentRequest request);

    /**
     * Creates a payment row and asks PayU for a Dynamic UPI QR ({@code upi://pay?...&am=...}).
     */
    ApiResponse<UpiQrInitResult> initiateUpiQr(InitiatePaymentRequest request, String clientIp);

    ApiResponse<List<PaymentResponse>> list(UUID invoiceId);

    ApiResponse<PaymentResponse> getById(UUID paymentId);

    ApiResponse<PaymentResponse> getByTxnId(String txnId);

    ApiResponse<PaymentResponse> refund(UUID paymentId, RefundPaymentRequest request);

    String handlePayuCallback(Map<String, String> params, boolean successEndpoint);
}
