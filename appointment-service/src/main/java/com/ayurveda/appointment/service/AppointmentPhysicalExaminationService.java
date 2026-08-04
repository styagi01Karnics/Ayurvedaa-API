package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentPhysicalExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentPhysicalExaminationResponse;

public interface AppointmentPhysicalExaminationService {

    /** Creates or updates physical examination for a patient. */
    ApiResponse<AppointmentPhysicalExaminationResponse> savePhysicalExamination(
            CreateAppointmentPhysicalExaminationRequest request);

    /** Fetches physical examination by patient ID. */
    ApiResponse<AppointmentPhysicalExaminationResponse> getPhysicalExaminationByPatientId(
            UUID patientId);

}
