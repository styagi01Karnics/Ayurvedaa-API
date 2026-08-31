package com.ayurveda.common.tenant;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Same property names as auth-service ({@code auth.jwt.secret}).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtTenantProperties {

    /**
     * HMAC secret used to verify Bearer JWTs issued by auth-service.
     */
    private String secret;

}
