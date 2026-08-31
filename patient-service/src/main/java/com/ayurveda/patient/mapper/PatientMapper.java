package com.ayurveda.patient.mapper;

import com.ayurveda.patient.dto.response.PatientResponse;
import com.ayurveda.patient.entity.Patient;

public final class PatientMapper {

    private PatientMapper() {
    }

    public static PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .patientCode(patient.getPatientCode())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .fullName(patient.getFirstName() + " " + patient.getLastName())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .age(patient.getAge())
                .preferredLanguage(patient.getPreferredLanguage())
                .email(patient.getEmail())
                .mobileNumber(patient.getMobileNumber())
                .state(patient.getState())
                .city(patient.getCity())
                .address(patient.getAddress())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyRelationship(patient.getEmergencyRelationship())
                .emergencyPhoneNumber(patient.getEmergencyPhoneNumber())
                .idProofType(patient.getIdProofType())
                .idProofNumber(patient.getIdProofNumber())
                .occupation(patient.getOccupation())
                .insuranceDetails(patient.getInsuranceDetails())
                .status(patient.getStatus())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }

}
