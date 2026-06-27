package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentTreatmentPlanRequest;
import com.ayurveda.appointment.dto.response.AppointmentTreatmentPlanResponse;

public interface AppointmentTreatmentPlanService {

    ApiResponse<AppointmentTreatmentPlanResponse> saveTreatmentPlan(
            CreateAppointmentTreatmentPlanRequest request);

    ApiResponse<AppointmentTreatmentPlanResponse> getTreatmentPlanByBookingId(
            UUID bookingId);

}