package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentTherapyRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;
import com.ayurveda.appointment.dto.response.TherapistSummaryResponse;
import com.ayurveda.appointment.entity.AppointmentTherapy;

@Component
public class AppointmentTherapyMapper {

    public AppointmentTherapy toEntity(CreateAppointmentTherapyRequest request) {
        if (request == null) {
            return null;
        }

        return AppointmentTherapy.builder()
                .patientId(request.getPatientId())
                .treatmentCategoryId(request.getTreatmentCategoryId())
                .assignedTherapistId(request.getAssignedTherapistId())
                .scheduleDate(request.getScheduleDate())
                .scheduleTime(request.getScheduleTime())
                .sessionDuration(request.getSessionDuration())
                .sessionFrequency(request.getSessionFrequency())
                .therapyInstructions(request.getTherapyInstructions())
                .remarks(request.getRemarks())
                .build();
    }

    public AppointmentTherapyResponse toResponse(AppointmentTherapy entity, TherapistSummaryResponse therapist) {
        if (entity == null) {
            return null;
        }

        return AppointmentTherapyResponse.builder()
                .therapyId(entity.getId())
                .assignedTherapist(therapist)
                .scheduleDate(entity.getScheduleDate())
                .scheduleTime(entity.getScheduleTime())
                .sessionDuration(entity.getSessionDuration())
                .sessionFrequency(entity.getSessionFrequency())
                .therapyInstructions(entity.getTherapyInstructions())
                .remarks(entity.getRemarks())
                .therapyStatus(entity.getTherapyStatus())
                .build();
    }

}
