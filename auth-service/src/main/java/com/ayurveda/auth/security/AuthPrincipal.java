package com.ayurveda.auth.security;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthPrincipal {

    private final UUID userId;
    private final UUID tenantId;
    private final String tenantCode;
    private final String email;
    private final String role;

}
