package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentTreatmentPlanRequest;
import com.ayurveda.appointment.dto.response.AppointmentTreatmentPlanResponse;
import com.ayurveda.appointment.entity.AppointmentTreatmentPlan;

@Component
public class AppointmentTreatmentPlanMapper {

    public AppointmentTreatmentPlan toEntity(
            CreateAppointmentTreatmentPlanRequest request) {

        if (request == null) {
            return null;
        }

        return AppointmentTreatmentPlan.builder()
                .patientId(request.getPatientId())
                .investigationAndPlanSuggested(
                        request.getInvestigationAndPlanSuggested())
                .planTaken(request.getPlanTaken())
                .build();
    }

    public AppointmentTreatmentPlanResponse toResponse(
            AppointmentTreatmentPlan entity) {

        if (entity == null) {
            return null;
        }

        return AppointmentTreatmentPlanResponse.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .investigationAndPlanSuggested(
                        entity.getInvestigationAndPlanSuggested())
                .planTaken(entity.getPlanTaken())
                .build();
    }

    public void updateEntity(
            AppointmentTreatmentPlan entity,
            CreateAppointmentTreatmentPlanRequest request) {

        entity.setInvestigationAndPlanSuggested(
                request.getInvestigationAndPlanSuggested());

        entity.setPlanTaken(
                request.getPlanTaken());
    }

}