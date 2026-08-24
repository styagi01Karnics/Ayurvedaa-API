package com.ayurveda.auth.dto.response;

import java.util.UUID;

import com.ayurveda.auth.enums.TenantStatus;

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
public class TenantResponse {

    private UUID id;
    private String tenantCode;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String schemaName;
    private Boolean platform;
    private TenantStatus status;
    private String provisionMessage;

}
