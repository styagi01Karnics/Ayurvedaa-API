package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentAyurvedicAssessmentRequest;
import com.ayurveda.appointment.dto.response.AppointmentAyurvedicAssessmentResponse;
import com.ayurveda.appointment.dto.response.DoshaResponse;
import com.ayurveda.appointment.entity.AppointmentAyurvedicAssessment;

@Component
public class AppointmentAyurvedicAssessmentMapper {

    public AppointmentAyurvedicAssessment toEntity(
            CreateAppointmentAyurvedicAssessmentRequest request) {

        if (request == null) {
            return null;
        }

        return AppointmentAyurvedicAssessment.builder()
                .patientId(request.getPatientId())
                .doshaId(request.getDoshaId())
                .bodyConstitution(request.getBodyConstitution())
                .currentImbalances(request.getCurrentImbalances())
                .build();
    }

    public AppointmentAyurvedicAssessmentResponse toResponse(
            AppointmentAyurvedicAssessment entity,
            DoshaResponse dosha) {

        if (entity == null) {
            return null;
        }

        return AppointmentAyurvedicAssessmentResponse.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .doshaId(entity.getDoshaId())
                .dosha(dosha)
                .bodyConstitution(entity.getBodyConstitution())
                .currentImbalances(entity.getCurrentImbalances())
                .build();
    }

    public void updateEntity(
            AppointmentAyurvedicAssessment entity,
            CreateAppointmentAyurvedicAssessmentRequest request) {

        entity.setDoshaId(request.getDoshaId());
        entity.setBodyConstitution(request.getBodyConstitution());
        entity.setCurrentImbalances(request.getCurrentImbalances());
    }

}
