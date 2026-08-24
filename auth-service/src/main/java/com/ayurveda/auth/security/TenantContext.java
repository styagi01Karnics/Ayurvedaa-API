package com.ayurveda.auth.security;

import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_CODE = new ThreadLocal<>();
    private static final ThreadLocal<String> SCHEMA_NAME = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(UUID tenantId, String tenantCode) {
        set(tenantId, tenantCode, null);
    }

    public static void set(UUID tenantId, String tenantCode, String schemaName) {
        TENANT_ID.set(tenantId);
        TENANT_CODE.set(tenantCode);
        SCHEMA_NAME.set(schemaName);
    }

    public static UUID getTenantId() {
        return TENANT_ID.get();
    }

    public static String getTenantCode() {
        return TENANT_CODE.get();
    }

    public static String getSchemaName() {
        return SCHEMA_NAME.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        TENANT_CODE.remove();
        SCHEMA_NAME.remove();
    }

}
