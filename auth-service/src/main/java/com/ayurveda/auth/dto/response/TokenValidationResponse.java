package com.ayurveda.auth.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {

    private boolean valid;
    private UUID userId;
    private UUID tenantId;
    private String tenantCode;
    private String email;
    private String role;

}
