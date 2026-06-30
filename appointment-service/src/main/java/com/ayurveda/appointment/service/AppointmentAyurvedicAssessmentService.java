package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentAyurvedicAssessmentRequest;
import com.ayurveda.appointment.dto.response.AppointmentAyurvedicAssessmentResponse;

public interface AppointmentAyurvedicAssessmentService {

    ApiResponse<AppointmentAyurvedicAssessmentResponse> saveAyurvedicAssessment(
            CreateAppointmentAyurvedicAssessmentRequest request);

    ApiResponse<AppointmentAyurvedicAssessmentResponse> getAyurvedicAssessmentByBookingId(
            UUID bookingId);

}