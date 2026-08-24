package com.ayurveda.auth.dto.request;

import java.util.UUID;

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
 * Forgot password:
 * <ul>
 *   <li>Super Admin — Gmail only (no tenant)</li>
 *   <li>Hospital users — tenantId or tenantCode + Gmail</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    private UUID tenantId;

    @Size(max = 50)
    private String tenantCode;

    @NotBlank(message = "Username (Gmail) is required")
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.GMAIL, message = AuthValidation.GMAIL_MESSAGE)
    private String usernameOrEmail;

}
