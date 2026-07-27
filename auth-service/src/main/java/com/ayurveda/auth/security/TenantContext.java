package com.ayurveda.auth.security;

import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_CODE = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(UUID tenantId, String tenantCode) {
        TENANT_ID.set(tenantId);
        TENANT_CODE.set(tenantCode);
    }

    public static UUID getTenantId() {
        return TENANT_ID.get();
    }

    public static String getTenantCode() {
        return TENANT_CODE.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        TENANT_CODE.remove();
    }

}
