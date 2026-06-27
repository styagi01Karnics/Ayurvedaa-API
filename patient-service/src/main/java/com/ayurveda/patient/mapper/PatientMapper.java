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
                .email(patient.getEmail())
                .mobileNumber(patient.getMobileNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .active(patient.getActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }

}
