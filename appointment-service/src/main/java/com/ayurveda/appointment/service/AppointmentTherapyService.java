package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentTherapyRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;
import com.ayurveda.appointment.dto.response.TherapistTodayScheduleResponse;

public interface AppointmentTherapyService {

    ApiResponse<AppointmentTherapyResponse> createAppointmentTherapy(
            CreateAppointmentTherapyRequest request);

    ApiResponse<List<AppointmentTherapyResponse>> getAppointmentTherapyByPatientId(UUID patientId);

    ApiResponse<TherapistTodayScheduleResponse> getTherapistTodaySchedule(UUID therapistId);

}