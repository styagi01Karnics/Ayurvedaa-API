package com.ayurveda.payment.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.payment.config.PayuCredentials;
import com.ayurveda.payment.config.PayuProperties;
import com.ayurveda.payment.constant.PaymentMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Single-tenant PayU credentials from {@code payu.*} in application.yml / env.
 * Super Admin per-tenant DB storage is not used for now.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayuGatewayResolver {

    private final PayuProperties payuProperties;

    public PayuCredentials requireForTenant(String tenantCode) {
        return findForTenant(tenantCode)
                .orElseThrow(() -> new BadRequestException(PaymentMessages.PAYU_NOT_CONFIGURED));
    }

    public Optional<PayuCredentials> findForTenant(String tenantCode) {
        if (!payuProperties.isConfigured()) {
            return Optional.empty();
        }
        log.debug("Using application.yml PayU credentials for tenant={}", blankToEmpty(tenantCode));
        return Optional.of(fromConfig(tenantCode));
    }

    private PayuCredentials fromConfig(String tenantCode) {
        String mode = payuProperties.getMode();
        if (mode == null || mode.isBlank()) {
            mode = inferMode(payuProperties.getPaymentUrl());
        }
        return PayuCredentials.builder()
                .tenantCode(tenantCode)
                .merchantKey(payuProperties.getMerchantKey())
                .merchantSalt(payuProperties.getMerchantSalt())
                .clientId(trimToNull(payuProperties.getClientId()))
                .clientSecret(trimToNull(payuProperties.getClientSecret()))
                .mode(mode.trim().toUpperCase())
                .paymentUrl(payuProperties.getPaymentUrl())
                .enabled(true)
                .legacyFallback(true)
                .build();
    }

    private static String inferMode(String paymentUrl) {
        if (paymentUrl != null && paymentUrl.toLowerCase().contains("secure.payu.in")) {
            return "LIVE";
        }
        return "TEST";
    }

    private static String blankToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
