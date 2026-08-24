package com.ayurveda.auth.security;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthPrincipal {

    private final UUID userId;
    private final UUID tenantId;
    private final String tenantCode;
    private final String schemaName;
    private final String email;
    private final String role;
    private final UUID tenantRoleId;
    @Builder.Default
    private final List<String> pageCodes = Collections.emptyList();

}
