package com.ayurveda.payment.config;

import lombok.Builder;
import lombok.Getter;

/**
 * Resolved PayU merchant credentials for one hospital (or legacy env fallback).
 */
@Getter
@Builder
public class PayuCredentials {

    public static final String LIVE_PAYMENT_URL = "https://secure.payu.in/_payment";
    public static final String TEST_PAYMENT_URL = "https://test.payu.in/_payment";

    private final String tenantCode;
    private final String merchantKey;
    private final String merchantSalt;
    private final String clientId;
    private final String clientSecret;
    private final String mode;
    private final String paymentUrl;
    private final boolean enabled;
    private final boolean legacyFallback;

    public boolean isConfigured() {
        return merchantKey != null && !merchantKey.isBlank()
                && merchantSalt != null && !merchantSalt.isBlank();
    }

    public String resolvedPaymentUrl() {
        if (paymentUrl != null && !paymentUrl.isBlank()) {
            return paymentUrl.trim();
        }
        if ("LIVE".equalsIgnoreCase(mode)) {
            return LIVE_PAYMENT_URL;
        }
        return TEST_PAYMENT_URL;
    }
}
