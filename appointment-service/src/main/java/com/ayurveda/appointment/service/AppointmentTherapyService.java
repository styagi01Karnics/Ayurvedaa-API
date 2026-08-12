package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentTherapyRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;
import com.ayurveda.appointment.dto.response.TherapistTodayScheduleResponse;

public interface AppointmentTherapyService {

    /** Creates therapy assignment details for a patient appointment. */
    ApiResponse<AppointmentTherapyResponse> createAppointmentTherapy(
            CreateAppointmentTherapyRequest request);

    /** Fetches therapy assignments for a patient. */
    ApiResponse<List<AppointmentTherapyResponse>> getAppointmentTherapyByPatientId(UUID patientId);

    /** Returns today's therapy schedule slots for a therapist. */
    ApiResponse<TherapistTodayScheduleResponse> getTherapistTodaySchedule(UUID therapistId);

}
