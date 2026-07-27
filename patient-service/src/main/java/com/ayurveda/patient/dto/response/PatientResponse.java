package com.ayurveda.patient.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.common.enums.IdProofType;
import com.ayurveda.patient.enums.Gender;

@Getter
@Builder
public class PatientResponse {

    private UUID id;
    /** e.g. #PT458652 */
    private String patientDisplayId;
    /** e.g. GAN2025-0129 */
    private String patientCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private Gender gender;
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
    private IdProofType idProofType;
    private String idProofNumber;
    private String occupation;
    private String insuranceDetails;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
