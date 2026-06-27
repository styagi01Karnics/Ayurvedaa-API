package com.ayurveda.appointment.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.enums.Gender;
import com.ayurveda.appointment.enums.IdProofType;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentBookingRequest {

    // ========================
    // Basic Information
    // ========================

    @NotBlank
    @Size(max = 150)
    private String fullName;

    @NotNull
    private Gender gender;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private Integer age;

    private String preferredLanguage;

    @NotNull
    private LocalDate registrationDate;

    @NotNull
    private UUID assignedDoctorId;

    @NotEmpty
    private List<String> consultationTypes;

    // ========================
    // Contact Information
    // ========================

    @NotBlank
    private String mobileNumber;

    private String email;

    private String state;

    private String city;

    private String permanentAddress;

    // ========================
    // Emergency Contact
    // ========================

    private String emergencyContactName;

    private String emergencyRelationship;

    private String emergencyPhoneNumber;

    // ========================
    // Identification
    // ========================

    private IdProofType idProofType;

    private String idProofNumber;

    private String occupation;

    private String insuranceDetails;

}