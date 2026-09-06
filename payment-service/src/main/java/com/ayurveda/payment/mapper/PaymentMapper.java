package com.ayurveda.payment.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.ayurveda.payment.dto.response.PaymentResponse;
import com.ayurveda.payment.entity.PaymentTransaction;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(PaymentTransaction payment) {
        return toResponse(payment, null, null);
    }

    public PaymentResponse toResponse(
            PaymentTransaction payment,
            String actionUrl,
            Map<String, String> payuParams) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentCode(payment.getPaymentCode())
                .payuTxnId(payment.getPayuTxnId())
                .amount(payment.getAmount())
                .productInfo(payment.getProductInfo())
                .firstName(payment.getFirstName())
                .email(payment.getEmail())
                .phone(payment.getPhone())
                .invoiceId(payment.getInvoiceId())
                .patientId(payment.getPatientId())
                .gateway(payment.getGateway())
                .status(payment.getStatus())
                .payuStatus(payment.getPayuStatus())
                .mihpayid(payment.getMihpayid())
                .paymentMode(payment.getPaymentMode())
                .errorMessage(payment.getErrorMessage())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .actionUrl(actionUrl)
                .payuParams(payuParams)
                .build();
    }
}
