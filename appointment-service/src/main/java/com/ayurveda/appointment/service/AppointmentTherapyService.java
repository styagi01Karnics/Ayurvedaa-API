package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentTherapyRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;

public interface AppointmentTherapyService {

    ApiResponse<AppointmentTherapyResponse> createAppointmentTherapy(
            CreateAppointmentTherapyRequest request);

    ApiResponse<AppointmentTherapyResponse> getAppointmentTherapyByPatientId(
            UUID patientId);

}