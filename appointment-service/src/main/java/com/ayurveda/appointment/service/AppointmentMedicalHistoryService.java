package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentMedicalHistoryRequest;
import com.ayurveda.appointment.dto.response.AppointmentMedicalHistoryResponse;

public interface AppointmentMedicalHistoryService {

    /** Creates or updates medical history for a patient. */
    ApiResponse<AppointmentMedicalHistoryResponse> saveMedicalHistory(
            CreateAppointmentMedicalHistoryRequest request);

    /** Fetches medical history by patient ID. */
    ApiResponse<AppointmentMedicalHistoryResponse> getMedicalHistoryByPatientId(
            UUID patientId);

}
