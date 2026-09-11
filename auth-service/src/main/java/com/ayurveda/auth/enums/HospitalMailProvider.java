package com.ayurveda.auth.enums;

public enum HospitalMailProvider {
    GMAIL,
    MICROSOFT;

    public static HospitalMailProvider fromEmail(String email) {
        if (email == null || email.isBlank()) {
            return MICROSOFT;
        }
        String lower = email.trim().toLowerCase();
        int at = lower.lastIndexOf('@');
        String domain = at >= 0 ? lower.substring(at + 1) : "";
        if ("gmail.com".equals(domain) || "googlemail.com".equals(domain)) {
            return GMAIL;
        }
        return MICROSOFT;
    }
}
