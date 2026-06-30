package com.ayurveda.patient.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePatientRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    private String fullName;

    @NotBlank(message = "Gender is required")
    @Size(max = 20, message = "Gender must not exceed 20 characters")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Age is required")
    private Integer age;

    @Size(max = 50, message = "Preferred language must not exceed 50 characters")
    private String preferredLanguage;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be 10 to 15 digits")
    private String mobileNumber;

    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "Permanent address is required")
    private String address;

    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    private String emergencyContactName;

    @Size(max = 50, message = "Emergency relationship must not exceed 50 characters")
    private String emergencyRelationship;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Emergency phone number must be 10 to 15 digits")
    private String emergencyPhoneNumber;

    @Size(max = 30, message = "ID proof type must not exceed 30 characters")
    private String idProofType;

    @Size(max = 50, message = "ID proof number must not exceed 50 characters")
    private String idProofNumber;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @Size(max = 255, message = "Insurance details must not exceed 255 characters")
    private String insuranceDetails;

}
