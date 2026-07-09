package com.ayurveda.appointment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientClientRequest {

    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String preferredLanguage;
    private String mobileNumber;
    private String email;
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

    public static CreatePatientClientRequest from(CreatePatientDetailsRequest request) {

        return CreatePatientClientRequest.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .age(request.getAge())
                .preferredLanguage(request.getPreferredLanguage())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .state(request.getState())
                .city(request.getCity())
                .address(request.getAddress())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyRelationship(request.getEmergencyRelationship())
                .emergencyPhoneNumber(request.getEmergencyPhoneNumber())
                .idProofType(request.getIdProofType())
                .idProofNumber(request.getIdProofNumber())
                .occupation(request.getOccupation())
                .insuranceDetails(request.getInsuranceDetails())
                .build();
    }
}