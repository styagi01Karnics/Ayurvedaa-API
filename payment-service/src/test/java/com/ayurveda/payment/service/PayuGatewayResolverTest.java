package com.ayurveda.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.payment.config.PayuCredentials;
import com.ayurveda.payment.config.PayuProperties;
import com.ayurveda.payment.constant.PaymentMessages;

class PayuGatewayResolverTest {

    private PayuProperties properties;
    private PayuGatewayResolver resolver;

    @BeforeEach
    void setUp() {
        properties = new PayuProperties();
        resolver = new PayuGatewayResolver(properties);
    }

    @Test
    void usesApplicationConfig() {
        properties.setMerchantKey("envKey");
        properties.setMerchantSalt("envSalt");
        properties.setMode("LIVE");
        properties.setPaymentUrl("https://secure.payu.in/_payment");
        properties.setClientId("oauth-client");

        PayuCredentials creds = resolver.requireForTenant("GAN-DL");

        assertEquals("envKey", creds.getMerchantKey());
        assertEquals("envSalt", creds.getMerchantSalt());
        assertEquals("LIVE", creds.getMode());
        assertEquals("https://secure.payu.in/_payment", creds.resolvedPaymentUrl());
        assertEquals("oauth-client", creds.getClientId());
        assertTrue(creds.isLegacyFallback());
    }

    @Test
    void throwsWhenNotConfigured() {
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> resolver.requireForTenant("GAN-DL"));
        assertEquals(PaymentMessages.PAYU_NOT_CONFIGURED, ex.getMessage());
    }

    @Test
    void infersTestModeFromUrlWhenModeBlank() {
        properties.setMerchantKey("k");
        properties.setMerchantSalt("s");
        properties.setMode(" ");
        properties.setPaymentUrl("https://test.payu.in/_payment");

        assertEquals("TEST", resolver.requireForTenant("GAN-DL").getMode());
    }
}
