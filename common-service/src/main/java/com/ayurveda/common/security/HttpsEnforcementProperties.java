package com.ayurveda.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "ayurveda.security")
public class HttpsEnforcementProperties {

    /**
     * When true, reject non-HTTPS requests (except actuator health).
     * Behind a reverse proxy, set {@code server.forward-headers-strategy=framework}
     * and terminate TLS at the proxy ({@code X-Forwarded-Proto: https}).
     */
    private boolean requireHttps = false;
}
