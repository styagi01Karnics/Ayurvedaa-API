package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentMedicalHistoryRequest;
import com.ayurveda.appointment.dto.response.AppointmentMedicalHistoryResponse;

public interface AppointmentMedicalHistoryService {

    ApiResponse<AppointmentMedicalHistoryResponse> saveMedicalHistory(
            CreateAppointmentMedicalHistoryRequest request);

    ApiResponse<AppointmentMedicalHistoryResponse> getMedicalHistoryByPatientId(
            UUID patientId);

}