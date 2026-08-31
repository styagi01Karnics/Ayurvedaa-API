package com.ayurveda.auth.dto.request;

import com.ayurveda.auth.constant.AuthValidation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
 *   <li>Hospital users — tenantCode (e.g. GAN-DL) + Gmail + password</li>
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

    /** Gmail login identity (field name kept for FE compatibility). */
    @NotBlank(message = "Username (Gmail) is required")
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.GMAIL, message = AuthValidation.GMAIL_MESSAGE)
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    @Size(max = 100)
    private String password;

}
