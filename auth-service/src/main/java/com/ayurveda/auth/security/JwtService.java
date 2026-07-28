package com.ayurveda.auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
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

    public String generateToken(AuthUser user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("tenantId", user.getTenant().getId().toString())
                .claim("tenantCode", user.getTenant().getTenantCode())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    public AuthPrincipal parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return AuthPrincipal.builder()
                .userId(UUID.fromString(claims.getSubject()))
                .tenantId(UUID.fromString(claims.get("tenantId", String.class)))
                .tenantCode(claims.get("tenantCode", String.class))
                .email(claims.get("email", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

    public long getExpirationMs() {
        return jwtProperties.getExpirationMs();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

}
