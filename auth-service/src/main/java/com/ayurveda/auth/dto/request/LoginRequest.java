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
 *   <li>Platform SUPER_ADMIN — Gmail username + password only (no tenant)</li>
 *   <li>Hospital users — tenantId or tenantCode + Gmail username + password</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** Required for hospital users. Omit for platform Super Admin. */
    private java.util.UUID tenantId;

    /** Alternative to tenantId for hospital users (e.g. GAN). */
    @Size(max = 50)
    private String tenantCode;

    /** Gmail address used as username. */
    @NotBlank(message = "Username (Gmail) is required")
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.GMAIL, message = AuthValidation.GMAIL_MESSAGE)
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    @Size(max = 100)
    private String password;

}
