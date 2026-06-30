package com.ayurveda.appointment.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
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
public class CreatePatientDetailsRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    private String fullName;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Age is required")
    private Integer age;

    private String preferredLanguage;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be 10 to 15 digits")
    private String mobileNumber;

    @Email(message = "Email must be valid")
    private String email;

    private String state;

    private String city;

    @NotBlank(message = "Permanent address is required")
    private String permanentAddress;

    private String emergencyContactName;

    private String emergencyRelationship;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Emergency phone number must be 10 to 15 digits")
    private String emergencyPhoneNumber;

    private String idProofType;

    private String idProofNumber;

    private String occupation;

    private String insuranceDetails;

}
