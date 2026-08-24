package com.ayurveda.auth.dto.request;

import com.ayurveda.auth.constant.AuthValidation;
import com.ayurveda.auth.enums.UserRole;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RegisterUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    /** Gmail address used as username (also stored as email). */
    @NotBlank(message = "Username (Gmail) is required")
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.GMAIL, message = AuthValidation.GMAIL_MESSAGE)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100)
    private String password;

    @NotNull(message = "Role is required")
    private UserRole role;

    /**
     * Hospital custom role (UI pages). Required for non-ADMIN users.
     * Optional for ADMIN (seeded hospital-admin role is used when null).
     */
    private UUID tenantRoleId;

}
