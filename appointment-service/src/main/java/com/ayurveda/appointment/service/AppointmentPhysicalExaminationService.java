package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentPhysicalExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentPhysicalExaminationResponse;

public interface AppointmentPhysicalExaminationService {

    ApiResponse<AppointmentPhysicalExaminationResponse> savePhysicalExamination(
            CreateAppointmentPhysicalExaminationRequest request);

    ApiResponse<AppointmentPhysicalExaminationResponse> getPhysicalExaminationByBookingId(
            UUID bookingId);

}