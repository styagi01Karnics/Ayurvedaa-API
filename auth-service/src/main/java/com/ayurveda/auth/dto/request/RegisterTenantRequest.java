package com.ayurveda.auth.dto.request;

import com.ayurveda.auth.constant.AuthValidation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class RegisterTenantRequest {

    @NotBlank(message = "Tenant code is required")
    @Size(max = 50)
    private String tenantCode;

    @NotBlank(message = "Tenant name is required")
    @Size(max = 150)
    private String name;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String address;

    @NotBlank(message = "Admin full name is required")
    @Size(max = 100)
    private String adminFullName;

    /** Gmail address used as admin username (also stored as email). */
    @NotBlank(message = "Admin username (Gmail) is required")
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.GMAIL, message = AuthValidation.GMAIL_MESSAGE)
    private String adminUsername;

    @NotBlank(message = "Admin password is required")
    @Size(min = 8, max = 100)
    private String adminPassword;

}
