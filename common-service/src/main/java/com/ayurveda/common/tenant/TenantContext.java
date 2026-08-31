package com.ayurveda.common.tenant;

import java.util.UUID;

/**
 * Request-scoped tenant identity for hospital schema routing.
 * Cleared in a filter {@code finally} block after each request.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_CODE = new ThreadLocal<>();
    private static final ThreadLocal<String> SCHEMA_NAME = new ThreadLocal<>();
    private static final ThreadLocal<UUID> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();
    private static final ThreadLocal<String> AUTHORIZATION_HEADER = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(
            UUID tenantId,
            String tenantCode,
            String schemaName,
            UUID userId,
            String role,
            String authorizationHeader) {
        TENANT_ID.set(tenantId);
        TENANT_CODE.set(tenantCode);
        SCHEMA_NAME.set(schemaName);
        USER_ID.set(userId);
        ROLE.set(role);
        AUTHORIZATION_HEADER.set(authorizationHeader);
    }

    public static void set(UUID tenantId, String tenantCode, String schemaName) {
        set(tenantId, tenantCode, schemaName, null, null, null);
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

    public static UUID getUserId() {
        return USER_ID.get();
    }

    public static String getRole() {
        return ROLE.get();
    }

    public static String getAuthorizationHeader() {
        return AUTHORIZATION_HEADER.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        TENANT_CODE.remove();
        SCHEMA_NAME.remove();
        USER_ID.remove();
        ROLE.remove();
        AUTHORIZATION_HEADER.remove();
    }

}
