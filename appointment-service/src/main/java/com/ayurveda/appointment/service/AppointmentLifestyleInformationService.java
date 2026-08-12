package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentLifestyleInformationRequest;
import com.ayurveda.appointment.dto.response.AppointmentLifestyleInformationResponse;

public interface AppointmentLifestyleInformationService {

    /** Creates or updates lifestyle information for a patient. */
    ApiResponse<AppointmentLifestyleInformationResponse> saveLifestyleInformation(
            CreateAppointmentLifestyleInformationRequest request);

    /** Fetches lifestyle information by patient ID. */
    ApiResponse<AppointmentLifestyleInformationResponse> getLifestyleInformationByPatientId(
            UUID patientId);

}
