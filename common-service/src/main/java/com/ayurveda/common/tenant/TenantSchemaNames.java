package com.ayurveda.common.tenant;

/**
 * Validation helpers for Postgres schema identifiers used in {@code SET search_path}.
 */
public final class TenantSchemaNames {

    private static final String SCHEMA_PATTERN = "^[a-z][a-z0-9_]*$";

    private TenantSchemaNames() {
    }

    public static boolean isValidIdentifier(String schemaName) {
        return schemaName != null
                && schemaName.length() <= 63
                && schemaName.matches(SCHEMA_PATTERN);
    }

    /**
     * Hospital clinical schemas are {@code hosp_*}. Platform auth uses {@code public}.
     */
    public static boolean isHospitalSchema(String schemaName) {
        return isValidIdentifier(schemaName)
                && schemaName.startsWith("hosp_")
                && !"public".equalsIgnoreCase(schemaName);
    }

}
