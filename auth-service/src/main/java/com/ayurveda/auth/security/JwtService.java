package com.ayurveda.auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.ayurveda.auth.config.JwtProperties;
import com.ayurveda.auth.entity.AuthUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateToken(AuthUser user, List<String> pageCodes) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());

        List<String> pages = pageCodes != null ? pageCodes : Collections.emptyList();
        UUID tenantRoleId = user.getTenantRole() != null ? user.getTenantRole().getId() : null;

        var builder = Jwts.builder()
                .subject(user.getId().toString())
                .claim("tenantId", user.getTenant().getId().toString())
                .claim("tenantCode", user.getTenant().getTenantCode())
                .claim("schemaName", user.getTenant().getSchemaName())
                .claim("username", user.getEmail())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("pageCodes", pages)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey());

        if (tenantRoleId != null) {
            builder.claim("tenantRoleId", tenantRoleId.toString());
        }

        return builder.compact();
    }

    @SuppressWarnings("unchecked")
    public AuthPrincipal parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<String> pageCodes = claims.get("pageCodes", List.class);
        if (pageCodes == null) {
            pageCodes = Collections.emptyList();
        }

        String tenantRoleIdRaw = claims.get("tenantRoleId", String.class);

        return AuthPrincipal.builder()
                .userId(UUID.fromString(claims.getSubject()))
                .tenantId(UUID.fromString(claims.get("tenantId", String.class)))
                .tenantCode(claims.get("tenantCode", String.class))
                .schemaName(claims.get("schemaName", String.class))
                .email(claims.get("email", String.class))
                .role(claims.get("role", String.class))
                .tenantRoleId(tenantRoleIdRaw != null ? UUID.fromString(tenantRoleIdRaw) : null)
                .pageCodes(pageCodes)
                .build();
    }

    public long getExpirationMs() {
        return jwtProperties.getExpirationMs();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

}
