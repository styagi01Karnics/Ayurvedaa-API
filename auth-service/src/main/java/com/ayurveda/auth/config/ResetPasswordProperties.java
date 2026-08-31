package com.ayurveda.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.reset-password")
public class ResetPasswordProperties {

    /**
     * When true (local/dev), forgot-password response includes {@code resetToken}.
     * Keep false in production-like environments so the token is only delivered by email.
     */
    private boolean exposeToken = false;

    /** Minutes until the reset token expires. */
    private int expiryMinutes = 30;

    /**
     * Frontend reset-password page base URL used in the email link.
     * Token is appended as {@code ?token=…}.
     */
    private String resetLinkBaseUrl = "http://localhost:3000/reset-password";

}
