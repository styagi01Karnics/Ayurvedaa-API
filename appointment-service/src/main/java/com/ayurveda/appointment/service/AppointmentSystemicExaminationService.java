package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentSystemicExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentSystemicExaminationResponse;

public interface AppointmentSystemicExaminationService {

    ApiResponse<AppointmentSystemicExaminationResponse> saveSystemicExamination(
            CreateAppointmentSystemicExaminationRequest request);

    ApiResponse<AppointmentSystemicExaminationResponse> getSystemicExaminationByBookingId(
            UUID bookingId);

}