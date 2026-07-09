package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentMedicalHistoryRequest;
import com.ayurveda.appointment.dto.response.AppointmentMedicalHistoryResponse;
import com.ayurveda.appointment.entity.AppointmentMedicalHistory;

@Component
public class AppointmentMedicalHistoryMapper {

    public AppointmentMedicalHistory toEntity(
            CreateAppointmentMedicalHistoryRequest request) {

        if (request == null) {
            return null;
        }

        return AppointmentMedicalHistory.builder()
                .patientId(request.getPatientId())
                .pastMedicalConditions(request.getPastMedicalConditions())
                .pastSurgeries(request.getPastSurgeries())
                .currentMedications(request.getCurrentMedications())
                .allergies(request.getAllergies())
                .familyHistory(request.getFamilyHistory())
                .build();
    }

    public AppointmentMedicalHistoryResponse toResponse(
            AppointmentMedicalHistory entity) {

        if (entity == null) {
            return null;
        }

        return AppointmentMedicalHistoryResponse.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .pastMedicalConditions(entity.getPastMedicalConditions())
                .pastSurgeries(entity.getPastSurgeries())
                .currentMedications(entity.getCurrentMedications())
                .allergies(entity.getAllergies())
                .familyHistory(entity.getFamilyHistory())
                .build();
    }

    public void updateEntity(
            AppointmentMedicalHistory entity,
            CreateAppointmentMedicalHistoryRequest request) {

        entity.setPastMedicalConditions(request.getPastMedicalConditions());
        entity.setPastSurgeries(request.getPastSurgeries());
        entity.setCurrentMedications(request.getCurrentMedications());
        entity.setAllergies(request.getAllergies());
        entity.setFamilyHistory(request.getFamilyHistory());
    }

}