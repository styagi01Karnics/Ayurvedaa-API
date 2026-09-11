package com.ayurveda.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import com.ayurveda.common.crypto.SecretEncryption;
import com.ayurveda.common.tenant.JwtTenantProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Encrypts hospital mailbox SMTP passwords using the same {@code auth.jwt.secret}
 * already shared by auth and notification (no separate encryption env var).
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(JwtTenantProperties.class)
public class SecretEncryptionAutoConfiguration {

    private static final String LOCAL_FALLBACK_KEY =
            "AyurvedaaAuthJwtSecretKeyMustBeAtLeast256BitsLong!!";

    @Bean
    @ConditionalOnMissingBean
    public SecretEncryption secretEncryption(JwtTenantProperties jwtTenantProperties) {
        String key = jwtTenantProperties.getSecret();
        if (!StringUtils.hasText(key)) {
            key = LOCAL_FALLBACK_KEY;
            log.warn("auth.jwt.secret / JWT_SECRET is not set; using local fallback for secret encryption.");
        }
        return new SecretEncryption(key.trim());
    }
}
