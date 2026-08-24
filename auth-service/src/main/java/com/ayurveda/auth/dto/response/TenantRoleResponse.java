package com.ayurveda.auth.dto.response;

import java.util.List;
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
public class TenantRoleResponse {

    private UUID id;
    private UUID tenantId;
    private String roleCode;
    private String roleName;
    private String description;
    private Boolean systemRole;
    private Boolean active;
    private List<String> pageCodes;
    /** Users assigned to this role (Figma card footer). */
    private Long userCount;

}
