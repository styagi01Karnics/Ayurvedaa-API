package com.ayurveda.appointment.dto.request;

import java.time.LocalDate;

import com.ayurveda.common.validation.ValidationPatterns;
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
    @Size(min = 3, max = 150, message = "Full name must be between 3 and 150 characters")
    @Pattern(
            regexp = ValidationPatterns.FULL_NAME_ALPHABETS_SPACES,
            message = "Full name should contain only alphabets and spaces")
    private String fullName;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = ValidationPatterns.GENDER, message = "Gender must be MALE, FEMALE or OTHER")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Age is required")
    private Integer age;

    @Size(max = 50, message = "Preferred language must not exceed 50 characters")
    @Pattern(
            regexp = ValidationPatterns.ALPHABETS_AND_SPACES_OPTIONAL,
            message = "Preferred language should contain only alphabets")
    private String preferredLanguage;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = ValidationPatterns.MOBILE_IN, message = "Enter a valid 10-digit mobile number")
    private String mobileNumber;

    @Email(message = "Enter a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 100, message = "State must not exceed 100 characters")
    @Pattern(
            regexp = ValidationPatterns.ALPHABETS_AND_SPACES_OPTIONAL,
            message = "State should contain only alphabets")
    private String state;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Pattern(
            regexp = ValidationPatterns.ALPHABETS_AND_SPACES_OPTIONAL,
            message = "City should contain only alphabets")
    private String city;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    @Pattern(
            regexp = ValidationPatterns.ALPHABETS_AND_SPACES_OPTIONAL,
            message = "Emergency contact name should contain only alphabets")
    private String emergencyContactName;

    @Size(max = 50, message = "Emergency relationship must not exceed 50 characters")
    @Pattern(
            regexp = ValidationPatterns.ALPHABETS_AND_SPACES_OPTIONAL,
            message = "Emergency relationship should contain only alphabets")
    private String emergencyRelationship;

    @Pattern(regexp = ValidationPatterns.MOBILE_IN, message = "Enter a valid emergency mobile number")
    private String emergencyPhoneNumber;

    @Pattern(regexp = ValidationPatterns.ID_PROOF_TYPE, message = "Invalid ID proof type")
    private String idProofType;

    @Size(max = 50, message = "ID proof number must not exceed 50 characters")
    private String idProofNumber;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    @Pattern(
            regexp = ValidationPatterns.ALPHABETS_AND_SPACES_OPTIONAL,
            message = "Occupation should contain only alphabets")
    private String occupation;

    @Size(max = 255, message = "Insurance details must not exceed 255 characters")
    private String insuranceDetails;
}
