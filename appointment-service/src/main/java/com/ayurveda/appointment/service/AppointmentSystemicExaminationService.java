package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentSystemicExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentSystemicExaminationResponse;

public interface AppointmentSystemicExaminationService {

    /** Creates or updates systemic examination for a patient. */
    ApiResponse<AppointmentSystemicExaminationResponse> saveSystemicExamination(
            CreateAppointmentSystemicExaminationRequest request);

    /** Fetches systemic examination by patient ID. */
    ApiResponse<AppointmentSystemicExaminationResponse> getSystemicExaminationByPatientId(
            UUID patientId);

}
