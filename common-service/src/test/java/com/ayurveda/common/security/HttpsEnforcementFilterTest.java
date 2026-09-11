package com.ayurveda.common.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class HttpsEnforcementFilterTest {

    @Test
    void acceptsSecureRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        assertTrue(HttpsEnforcementFilter.isHttps(request));
    }

    @Test
    void acceptsForwardedHttpsProto() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(false);
        request.addHeader("X-Forwarded-Proto", "https");
        assertTrue(HttpsEnforcementFilter.isHttps(request));
    }

    @Test
    void acceptsForwardedHttpsInCommaList() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-Proto", "http, https");
        assertTrue(HttpsEnforcementFilter.isHttps(request));
    }

    @Test
    void rejectsPlainHttp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(false);
        assertFalse(HttpsEnforcementFilter.isHttps(request));
    }
}
