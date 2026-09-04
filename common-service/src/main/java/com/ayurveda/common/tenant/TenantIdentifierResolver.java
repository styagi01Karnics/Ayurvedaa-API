package com.ayurveda.common.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Resolves the hospital schema for Hibernate SCHEMA multi-tenancy.
 * Falls back to {@code public} only when no hospital is bound (startup / health).
 */
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    public static final String DEFAULT_SCHEMA = "public";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String schema = TenantContext.getSchemaName();
        return TenantSchemaNames.isHospitalSchema(schema) ? schema : DEFAULT_SCHEMA;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
