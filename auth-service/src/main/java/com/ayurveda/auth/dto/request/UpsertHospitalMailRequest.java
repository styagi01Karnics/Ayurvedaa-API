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
 * Hospital mailbox used as From for patient emails. Not the admin login.
 * Gmail or Microsoft / Outlook. Password is SMTP / App Password;
 * stored AES-GCM encrypted with a random salt using JWT_SECRET (not BCrypt).
 * <p>
 * First-time setup requires {@code password}. Later updates may omit {@code password}
 * to keep the stored secret.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertHospitalMailRequest {

    @NotBlank(message = "Hospital sending email is required")
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.EMAIL, message = AuthValidation.EMAIL_MESSAGE)
    private String email;

    /** Required when creating mailbox; optional on update to keep existing secret. */
    @Size(min = 8, max = 255)
    private String password;

}
