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
 * Figma hospital signup: Clinic Information + Contact Information.
 * <p>
 * All form fields (except {@code password} / {@code confirmPassword}) are persisted on
 * {@code tenants}. Auth extracts only what login needs into {@code auth_users}:
 * {@code fullName}, {@code mobileNumber}, {@code email} (canonical login identity),
 * and {@code password} → passwordHash.
 * <p>
 * Figma may label a separate "User ID" field; the API accepts only {@code email} (Gmail)
 * as the single login identity.
 * <p>
 * {@code tenantCode} is auto-generated as BRAND-STATE (e.g. GAN-DL) from clinic name + state;
 * collisions get a numeric suffix (GAN-DL-2, GAN-DL-3, …).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardHospitalRequest {

    // ----- Clinic Information -----

    @NotBlank(message = "Clinic name is required")
    @Size(max = 150)
    private String clinicName;

    @Size(max = 100)
    private String clinicType;

    @NotBlank(message = "State is required")
    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String city;

    @Size(max = 20)
    private String pinCode;

    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @Size(max = 100)
    private String registrationNumberGst;

    /** Optional logo URL after upload via file-upload-service. */
    @Size(max = 500)
    private String logoUrl;

    // ----- Contact Information (hospital admin) -----

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @Size(max = 20)
    private String mobileNumber;

    /**
     * Single login identity (Gmail). Also stored as tenant contact email.
     * UI "User ID" / "Email" both map to this field.
     */
    @NotBlank(message = "Email (Gmail) is required")
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.GMAIL, message = AuthValidation.GMAIL_MESSAGE)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Size(min = 8, max = 100)
    private String confirmPassword;

    /** Optional primary-contact photo URL after file-upload-service; stored on tenant. */
    @Size(max = 500)
    private String photoUrl;

}
