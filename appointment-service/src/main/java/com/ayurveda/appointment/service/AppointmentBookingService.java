package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;

public interface AppointmentBookingService {

    ApiResponse<AppointmentBookingResponse> createAppointment(
            CreateAppointmentBookingRequest request);

    ApiResponse<AppointmentBookingResponse> getAppointmentById(
            UUID bookingId);

}