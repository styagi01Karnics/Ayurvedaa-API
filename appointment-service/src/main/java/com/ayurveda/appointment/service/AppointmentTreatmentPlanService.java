package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentTreatmentPlanRequest;
import com.ayurveda.appointment.dto.response.AppointmentTreatmentPlanResponse;

public interface AppointmentTreatmentPlanService {

    /** Creates or updates treatment plan for a patient. */
    ApiResponse<AppointmentTreatmentPlanResponse> saveTreatmentPlan(
            CreateAppointmentTreatmentPlanRequest request);

    /** Fetches treatment plan by patient ID. */
    ApiResponse<AppointmentTreatmentPlanResponse> getTreatmentPlanByPatientId(
            UUID patientId);

}
