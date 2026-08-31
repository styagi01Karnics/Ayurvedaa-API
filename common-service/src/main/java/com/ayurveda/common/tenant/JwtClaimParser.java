package com.ayurveda.common.tenant;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Parses auth-service JWTs (claims: tenantId, tenantCode, schemaName, role, …).
 */
public class JwtClaimParser {

    private final SecretKey signingKey;

    public JwtClaimParser(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("auth.jwt.secret must be configured for tenant routing");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public TenantClaims parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String tenantIdRaw = claims.get("tenantId", String.class);
        return TenantClaims.builder()
                .userId(UUID.fromString(claims.getSubject()))
                .tenantId(tenantIdRaw != null ? UUID.fromString(tenantIdRaw) : null)
                .tenantCode(claims.get("tenantCode", String.class))
                .schemaName(claims.get("schemaName", String.class))
                .email(claims.get("email", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

}
