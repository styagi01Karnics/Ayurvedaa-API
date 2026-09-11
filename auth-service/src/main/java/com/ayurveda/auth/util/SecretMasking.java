package com.ayurveda.auth.util;

/**
 * Masks secrets for API responses. Never log the raw value.
 */
public final class SecretMasking {

    private SecretMasking() {
    }

    public static String mask(String secret) {
        if (secret == null || secret.isBlank()) {
            return null;
        }
        if (secret.length() <= 4) {
            return "****";
        }
        return "****" + secret.substring(secret.length() - 4);
    }
}
