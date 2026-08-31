package com.ayurveda.auth.dto.response;

import java.util.List;
import java.util.UUID;

import com.ayurveda.auth.enums.UserRole;
import com.ayurveda.auth.enums.UserStatus;

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
public class UserResponse {

    private UUID id;
    private UUID tenantId;
    private String tenantCode;
    private String schemaName;
    private String email;
    private String fullName;
    private String mobileNumber;
    private UserRole role;
    private UUID tenantRoleId;
    private String tenantRoleCode;
    private String tenantRoleName;
    private List<String> pageCodes;
    private UserStatus status;

}
