package com.ayurveda.payment.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PayuPropertiesPostserviceTest {

    @Test
    void defaultsToTestPostservice() {
        PayuProperties props = new PayuProperties();
        props.setMode("TEST");
        props.setPaymentUrl("https://test.payu.in/_payment");
        assertTrue(props.resolvedPostserviceUrl().contains("test.payu.in"));
    }

    @Test
    void liveModeUsesInfoHost() {
        PayuProperties props = new PayuProperties();
        props.setMode("LIVE");
        assertTrue(props.resolvedPostserviceUrl().contains("info.payu.in"));
    }

    @Test
    void explicitOverrideWins() {
        PayuProperties props = new PayuProperties();
        props.setPostserviceUrl("https://example.test/postservice");
        assertTrue(props.resolvedPostserviceUrl().equals("https://example.test/postservice"));
    }
}
