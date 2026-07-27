package com.ayurveda.appointment.dto.response;

import java.time.LocalDate;
import java.util.UUID;

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
public class PatientSummaryResponse {

    private UUID id;
    /** e.g. #PT458652 */
    private String patientDisplayId;
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

}
