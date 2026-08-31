package com.ayurveda.auth.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ayurveda.auth.constant.AuthMessages;
import com.ayurveda.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates Postgres schemas for hospital tenants and runs {@link HospitalSchemaMigrator}.
 * Provision applies the full clinical table baseline (masters, appointments, billing, ops)
 * from {@code classpath:db/hospital-schema/*.sql}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaProvisioningService {

    private static final String SCHEMA_PREFIX = "hosp_";

    private final JdbcTemplate jdbcTemplate;
    private final HospitalSchemaMigrator hospitalSchemaMigrator;

    public String buildSchemaName(String tenantCode) {
        String normalized = tenantCode == null ? "" : tenantCode.trim().toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        if (normalized.isBlank()) {
            throw new BadRequestException(AuthMessages.INVALID_TENANT_CODE_FOR_SCHEMA);
        }
        String schema = SCHEMA_PREFIX + normalized;
        if (schema.length() > 63) {
            schema = schema.substring(0, 63);
        }
        if (!schema.matches("^[a-z][a-z0-9_]*$")) {
            throw new BadRequestException(AuthMessages.INVALID_SCHEMA_NAME + schema);
        }
        return schema;
    }

    /**
     * Creates the schema (if missing) and applies classpath hospital-schema baseline scripts.
     *
     * @return provision summary message suitable for {@code tenants.provisionMessage}
     */
    public String provisionSchema(String schemaName) {
        createSchema(schemaName);
        String migrationSummary = hospitalSchemaMigrator.migrate(schemaName);
        return "Schema " + schemaName + " created. " + migrationSummary;
    }

    public void createSchema(String schemaName) {
        validateSchemaName(schemaName);
        log.info("Creating Postgres schema {}", schemaName);
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
    }

    public void validateSchemaName(String schemaName) {
        if (schemaName == null || !schemaName.matches("^[a-z][a-z0-9_]*$") || schemaName.length() > 63) {
            throw new BadRequestException(AuthMessages.INVALID_SCHEMA_NAME + schemaName);
        }
        if ("public".equalsIgnoreCase(schemaName) || "pg_catalog".equalsIgnoreCase(schemaName)
                || "information_schema".equalsIgnoreCase(schemaName)) {
            throw new BadRequestException(AuthMessages.RESERVED_SCHEMA_NAME + schemaName);
        }
    }

}
