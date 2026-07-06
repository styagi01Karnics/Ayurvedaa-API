package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentLifestyleInformationRequest;
import com.ayurveda.appointment.dto.response.AppointmentLifestyleInformationResponse;
import com.ayurveda.appointment.entity.AppointmentLifestyleInformation;

@Component
public class AppointmentLifestyleInformationMapper {

    public AppointmentLifestyleInformation toEntity(
            CreateAppointmentLifestyleInformationRequest request) {

        if (request == null) {
            return null;
        }

        return AppointmentLifestyleInformation.builder()
                .patientId(request.getPatientId())
                .dietType(request.getDietType())
                .sleepPattern(request.getSleepPattern())
                .exerciseHabits(request.getExerciseHabits())
                .addiction(request.getAddiction())
                .build();
    }

    public AppointmentLifestyleInformationResponse toResponse(
            AppointmentLifestyleInformation entity) {

        if (entity == null) {
            return null;
        }

        return AppointmentLifestyleInformationResponse.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .dietType(entity.getDietType())
                .sleepPattern(entity.getSleepPattern())
                .exerciseHabits(entity.getExerciseHabits())
                .addiction(entity.getAddiction())
                .build();
    }

    public void updateEntity(
            AppointmentLifestyleInformation entity,
            CreateAppointmentLifestyleInformationRequest request) {

        entity.setDietType(request.getDietType());
        entity.setSleepPattern(request.getSleepPattern());
        entity.setExerciseHabits(request.getExerciseHabits());
        entity.setAddiction(request.getAddiction());
    }

}