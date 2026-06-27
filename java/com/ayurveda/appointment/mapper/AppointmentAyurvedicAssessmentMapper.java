package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentAyurvedicAssessmentRequest;
import com.ayurveda.appointment.dto.response.AppointmentAyurvedicAssessmentResponse;
import com.ayurveda.appointment.entity.AppointmentAyurvedicAssessment;

@Component
public class AppointmentAyurvedicAssessmentMapper {

    public AppointmentAyurvedicAssessment toEntity(
            CreateAppointmentAyurvedicAssessmentRequest request) {

        if (request == null) {
            return null;
        }

        return AppointmentAyurvedicAssessment.builder()
                .bookingId(request.getBookingId())
                .doshaType(request.getDoshaType())
                .bodyConstitution(request.getBodyConstitution())
                .currentImbalances(request.getCurrentImbalances())
                .build();
    }

    public AppointmentAyurvedicAssessmentResponse toResponse(
            AppointmentAyurvedicAssessment entity) {

        if (entity == null) {
            return null;
        }

        return AppointmentAyurvedicAssessmentResponse.builder()
                .id(entity.getId())
                .bookingId(entity.getBookingId())
                .doshaType(entity.getDoshaType())
                .bodyConstitution(entity.getBodyConstitution())
                .currentImbalances(entity.getCurrentImbalances())
                .build();
    }

    public void updateEntity(
            AppointmentAyurvedicAssessment entity,
            CreateAppointmentAyurvedicAssessmentRequest request) {

        entity.setDoshaType(request.getDoshaType());
        entity.setBodyConstitution(request.getBodyConstitution());
        entity.setCurrentImbalances(request.getCurrentImbalances());
    }

}