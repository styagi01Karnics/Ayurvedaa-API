package com.ayurveda.common.util;

import com.ayurveda.common.tenant.TenantContext;

/**
 * Builds tenant-scoped business codes: {@code {tenantCode}-{TYPE}-{#####}}.
 * Requires {@link TenantContext#getTenantCode()} from the JWT filter.
 */
public final class BusinessCodeGenerator {

    private BusinessCodeGenerator() {
    }

    /**
     * Next code for {@code type} given existing codes in the current hospital schema.
     * Parses trailing digits of codes matching the tenant/type prefix; starts at 1.
     */
    public static String next(String type, Iterable<String> existingCodes) {
        String tenantCode = requireTenantCode();
        String prefix = prefix(tenantCode, type);
        int max = 0;
        if (existingCodes != null) {
            for (String code : existingCodes) {
                if (code == null || !code.startsWith(prefix)) {
                    continue;
                }
                int seq = parseTrailingSequence(code);
                if (seq > max) {
                    max = seq;
                }
            }
        }
        return format(tenantCode, type, max + 1);
    }

    /** Prefix {@code {tenantCode}-{TYPE}-} using the current {@link TenantContext}. */
    public static String prefix(String type) {
        return prefix(requireTenantCode(), type);
    }

    public static String prefix(String tenantCode, String type) {
        return tenantCode + "-" + type + "-";
    }

    public static String format(String tenantCode, String type, int sequence) {
        return tenantCode + "-" + type + "-" + String.format("%05d", sequence);
    }

    public static int parseTrailingSequence(String code) {
        if (code == null || code.isBlank()) {
            return 0;
        }
        int dash = code.lastIndexOf('-');
        if (dash < 0 || dash == code.length() - 1) {
            return 0;
        }
        try {
            return Integer.parseInt(code.substring(dash + 1).trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static String requireTenantCode() {
        String tenantCode = TenantContext.getTenantCode();
        if (tenantCode == null || tenantCode.isBlank()) {
            throw new IllegalStateException(
                    "tenantCode is not available in TenantContext; ensure the JWT includes tenantCode");
        }
        return tenantCode.trim();
    }

}
