package com.ayurveda.auth.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ayurveda.auth.security.AuthPrincipal;
import com.ayurveda.common.activity.ActivityActionType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Writes a fail-soft activity_logs row into a newly provisioned hospital schema.
 * Used when Super Admin onboards / retries provision — cannot go through
 * activity-log-service with a public-schema Super Admin JWT (403).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalOnboardActivityLogger {

    private static final String PAGE = "Settings";
    private static final int TARGET_MAX = 150;

    private final JdbcTemplate jdbcTemplate;
    private final SchemaProvisioningService schemaProvisioningService;

    public void recordOnboarded(String schemaName, String hospitalName, String tenantCode, AuthPrincipal principal) {
        String target = truncate(
                "Hospital " + hospitalName + " (" + tenantCode + ") onboarded by Super Admin",
                TARGET_MAX);
        insert(schemaName, target, tenantCode, principal);
    }

    public void recordProvisionCompleted(
            String schemaName, String hospitalName, String tenantCode, AuthPrincipal principal) {
        String target = truncate(
                "Hospital " + hospitalName + " (" + tenantCode + ") provision completed by Super Admin",
                TARGET_MAX);
        insert(schemaName, target, tenantCode, principal);
    }

    private void insert(String schemaName, String target, String afterValue, AuthPrincipal principal) {
        try {
            schemaProvisioningService.validateSchemaName(schemaName);

            LocalDateTime now = LocalDateTime.now();
            Timestamp ts = Timestamp.valueOf(now);
            UUID id = UUID.randomUUID();

            String sql = "INSERT INTO " + schemaName + ".activity_logs ("
                    + "id, created_at, updated_at, is_deleted, "
                    + "page, action, target, before_value, after_value, "
                    + "activity_timestamp, performed_by_user_id, performed_by_user_name, performed_by_role"
                    + ") VALUES (?, ?, ?, FALSE, ?, ?, ?, NULL, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(
                    sql,
                    id,
                    ts,
                    ts,
                    PAGE,
                    ActivityActionType.CREATED.name(),
                    target,
                    afterValue,
                    ts,
                    principal != null ? principal.getUserId() : null,
                    principal != null ? principal.getEmail() : null,
                    principal != null ? principal.getRole() : null);

            log.debug("Recorded hospital onboard activity log in {}.activity_logs id={}", schemaName, id);
        } catch (Exception ex) {
            log.warn(
                    "Failed to write onboard activity log into {}.activity_logs (hospital already created): {}",
                    schemaName,
                    ex.getMessage());
        }
    }

    private static String truncate(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }

}
