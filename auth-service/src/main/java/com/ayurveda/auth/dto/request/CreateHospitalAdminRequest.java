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

/** Super Admin creates one or more hospital admins for a hospital. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHospitalAdminRequest {

    /** Optional when hospital id is in the path. */
    private UUID hospitalId;

    @NotBlank(message = "Admin full name is required")
    @Size(max = 100)
    private String fullName;

    /** Gmail address used as username (also stored as email). */
    @NotBlank(message = "Admin username (Gmail) is required")
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.GMAIL, message = AuthValidation.GMAIL_MESSAGE)
    private String username;

    @NotBlank(message = "Admin password is required")
    @Size(min = 8, max = 100)
    private String password;

}
