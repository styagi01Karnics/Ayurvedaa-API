package com.ayurveda.notification.mail;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ayurveda.common.crypto.SecretEncryption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Reads hospital SMTP from {@code public.tenant_mail_settings}
 * (saved via PUT /api/v1/platform/hospitals/{hospitalId}/mail).
 * Passwords are AES-GCM encrypted at rest; decrypted here for SMTP.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TenantMailSettingsLookup {

    private final JdbcTemplate jdbcTemplate;
    private final SecretEncryption secretEncryption;

    public Optional<HospitalSmtpAccount> findByTenantCode(String tenantCode) {
        if (!StringUtils.hasText(tenantCode)) {
            return Optional.empty();
        }
        try {
            return jdbcTemplate.query(
                    """
                            SELECT from_email, smtp_password, provider
                            FROM public.tenant_mail_settings
                            WHERE lower(tenant_code) = lower(?)
                              AND COALESCE(is_deleted, false) = false
                              AND enabled = true
                            """,
                    (rs, rowNum) -> new HospitalSmtpAccount(
                            rs.getString("from_email"),
                            secretEncryption.decrypt(rs.getString("smtp_password")),
                            rs.getString("provider")),
                    tenantCode.trim())
                    .stream()
                    .findFirst();
        } catch (Exception ex) {
            log.warn("Hospital mail settings not readable for tenant={}: {}", tenantCode, ex.getMessage());
            return Optional.empty();
        }
    }

    public record HospitalSmtpAccount(String fromEmail, String smtpPassword, String provider) {
        public boolean ready() {
            return StringUtils.hasText(fromEmail) && StringUtils.hasText(smtpPassword);
        }
    }
}
