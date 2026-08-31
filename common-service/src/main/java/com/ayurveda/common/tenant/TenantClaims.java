package com.ayurveda.common.tenant;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TenantClaims {

    private final UUID userId;
    private final UUID tenantId;
    private final String tenantCode;
    private final String schemaName;
    private final String email;
    private final String role;

}
