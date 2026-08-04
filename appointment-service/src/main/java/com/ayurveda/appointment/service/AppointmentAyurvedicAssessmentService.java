package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentAyurvedicAssessmentRequest;
import com.ayurveda.appointment.dto.response.AppointmentAyurvedicAssessmentResponse;

public interface AppointmentAyurvedicAssessmentService {

    /** Creates or updates ayurvedic assessment for a patient. */
    ApiResponse<AppointmentAyurvedicAssessmentResponse> saveAyurvedicAssessment(
            CreateAppointmentAyurvedicAssessmentRequest request);

    /** Fetches ayurvedic assessment by patient ID. */
    ApiResponse<AppointmentAyurvedicAssessmentResponse> getAyurvedicAssessmentByPatientId(
            UUID patientId);

}
