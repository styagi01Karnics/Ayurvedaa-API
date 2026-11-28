package com.ayurveda.auth.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Hospital admin — Add New Role (Figma).
 * pageCodes = modules toggled On.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantRoleRequest {

    /** Optional; auto-generated from roleName when omitted. */
    @Size(max = 50)
    private String roleCode;

    @NotBlank(message = "Role name is required")
    @Size(max = 100)
    private String roleName;

    @Size(max = 255)
    private String description;

    /** UI page codes toggled On (DASHBOARD, PATIENTS, …). */
    @NotEmpty(message = "At least one UI page is required")
    private List<@NotBlank String> pageCodes;

    /** Figma Active toggle; defaults to true. */
    private Boolean active;

}
