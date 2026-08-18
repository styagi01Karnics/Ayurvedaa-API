package com.ayurveda.patient.dto.request;

import com.ayurveda.common.enums.IdProofType;
import com.ayurveda.common.validation.ValidationPatterns;
import com.ayurveda.patient.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePatientRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 150, message = "Full name must be between 3 and 150 characters")
    @Pattern(regexp = ValidationPatterns.FULL_NAME, message = "Full name contains invalid characters")
    private String fullName;

    @NotNull(message = "Gender is required")
    private Gender gender;

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
    @Pattern(regexp = ValidationPatterns.PLACE_NAME_OPTIONAL, message = "State contains invalid characters")
    private String state;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Pattern(regexp = ValidationPatterns.PLACE_NAME_OPTIONAL, message = "City contains invalid characters")
    private String city;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    @Pattern(
            regexp = ValidationPatterns.PERSON_NAME_OPTIONAL,
            message = "Emergency contact name contains invalid characters")
    private String emergencyContactName;

    @Size(max = 50, message = "Emergency relationship must not exceed 50 characters")
    @Pattern(
            regexp = ValidationPatterns.ALPHABETS_AND_SPACES_OPTIONAL,
            message = "Emergency relationship should contain only alphabets")
    private String emergencyRelationship;

    @Pattern(regexp = ValidationPatterns.MOBILE_IN, message = "Enter a valid emergency mobile number")
    private String emergencyPhoneNumber;

    private IdProofType idProofType;

    @Size(max = 50, message = "ID proof number must not exceed 50 characters")
    private String idProofNumber;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    @Pattern(regexp = ValidationPatterns.OCCUPATION, message = "Occupation contains invalid characters")
    private String occupation;

    @Size(max = 255, message = "Insurance details must not exceed 255 characters")
    private String insuranceDetails;
}
