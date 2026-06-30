package com.ayurveda.patient.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PatientResponse {

    private UUID id;
    private String patientCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String preferredLanguage;
    private String email;
    private String mobileNumber;
    private String state;
    private String city;
    private String address;
    private String emergencyContactName;
    private String emergencyRelationship;
    private String emergencyPhoneNumber;
    private String idProofType;
    private String idProofNumber;
    private String occupation;
    private String insuranceDetails;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
