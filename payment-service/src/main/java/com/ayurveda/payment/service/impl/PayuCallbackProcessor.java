package com.ayurveda.payment.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.payment.constant.PaymentMessages;
import com.ayurveda.payment.entity.PaymentTransaction;
import com.ayurveda.payment.enums.PaymentStatus;
import com.ayurveda.payment.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayuCallbackProcessor {

    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CallbackResult process(Map<String, String> params, boolean successEndpoint) {
        String txnId = value(params, "txnid");
        PaymentTransaction payment = paymentRepository.findByPayuTxnIdAndDeletedFalse(txnId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PaymentMessages.PAYMENT_NOT_FOUND_WITH_TXN + txnId));

        PaymentStatus mapped = mapPayuStatus(value(params, "status"), successEndpoint);
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            payment.setStatus(mapped);
            payment.setPayuStatus(value(params, "status"));
            payment.setMihpayid(value(params, "mihpayid"));
            payment.setPaymentMode(value(params, "mode"));
            payment.setErrorMessage(firstNonBlank(value(params, "error_Message"), value(params, "error")));
            payment.setRawCallback(toJson(params));
        }

        log.info(
                "PayU callback processed. txnid={}, payuStatus={}, mapped={}",
                txnId,
                payment.getPayuStatus(),
                payment.getStatus());

        return new CallbackResult(mapped, resolveFrontendRedirect(payment, mapped), payment);
    }

    public record CallbackResult(PaymentStatus status, String redirectUrl, PaymentTransaction payment) {
    }

    private static PaymentStatus mapPayuStatus(String payuStatus, boolean successEndpoint) {
        String status = payuStatus == null ? "" : payuStatus.trim().toLowerCase();
        return switch (status) {
            case "success" -> PaymentStatus.SUCCESS;
            case "pending" -> PaymentStatus.PENDING;
            case "failure", "failed" -> PaymentStatus.FAILED;
            case "cancel", "cancelled", "canceled", "bounced" -> PaymentStatus.CANCELLED;
            default -> successEndpoint ? PaymentStatus.PENDING : PaymentStatus.FAILED;
        };
    }

    private static String resolveFrontendRedirect(PaymentTransaction payment, PaymentStatus status) {
        String base = status == PaymentStatus.SUCCESS
                ? payment.getFrontendSuccessUrl()
                : payment.getFrontendFailureUrl();
        if (base == null || base.isBlank()) {
            return null;
        }
        return UriComponentsBuilder.fromUriString(base)
                .queryParam("txnid", payment.getPayuTxnId())
                .queryParam("paymentId", payment.getId())
                .queryParam("status", status.name())
                .build(true)
                .toUriString();
    }

    private String toJson(Map<String, String> params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException ex) {
            return params.toString();
        }
    }

    private static String value(Map<String, String> params, String key) {
        if (params == null || key == null) {
            return "";
        }
        String value = params.get(key);
        if (value != null) {
            return value.trim();
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key) && entry.getValue() != null) {
                return entry.getValue().trim();
            }
        }
        return "";
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
