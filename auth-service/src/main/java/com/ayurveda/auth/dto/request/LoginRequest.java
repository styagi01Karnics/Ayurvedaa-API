package com.ayurveda.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Login:
 * <ul>
 *   <li>Platform SUPER_ADMIN — Gmail + password only (no tenantCode)</li>
 *   <li>Hospital users — tenantCode (e.g. GAN-DL) + Gmail or user code + password</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** Required for hospital users (e.g. GAN-DL). Omit for platform Super Admin. */
    @Size(max = 50)
    private String tenantCode;

    /** Gmail (any tenant) or, with tenantCode, the hospital user code. Field name kept for FE compatibility. */
    @NotBlank(message = "Username (Gmail or user code) is required")
    @Size(max = 150)
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    @Size(max = 100)
    private String password;

}
