package com.ayurveda.auth.dto.request;

import com.ayurveda.auth.constant.AuthValidation;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Super Admin hospital profile edit — same Figma clinic/contact fields as onboard,
 * excluding password / confirmPassword. Does not change {@code tenantCode} or schema.
 * All fields optional; only non-blank values are applied.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHospitalRequest {

    // ----- Clinic Information -----

    @Size(max = 150)
    private String clinicName;

    @Size(max = 100)
    private String clinicType;

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

    /** Optional logo URL after upload; stored on tenant. */
    @Size(max = 500)
    private String logoUrl;

    // ----- Contact Information -----

    @Size(max = 100)
    private String fullName;

    @Size(max = 20)
    private String mobileNumber;

    /** Contact email on tenant (does not change auth_users login). */
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.GMAIL, message = AuthValidation.GMAIL_MESSAGE)
    private String email;

    /** Optional contact photo URL; stored on tenant. */
    @Size(max = 500)
    private String photoUrl;

}
