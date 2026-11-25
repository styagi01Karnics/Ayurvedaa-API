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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTenantRoleRequest {

    @NotBlank(message = "Role name is required")
    @Size(max = 100)
    private String roleName;

    @Size(max = 255)
    private String description;

    @NotEmpty(message = "At least one UI page is required")
    private List<@NotBlank String> pageCodes;

    private Boolean active;

}
