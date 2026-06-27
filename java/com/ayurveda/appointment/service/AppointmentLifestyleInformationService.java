package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentLifestyleInformationRequest;
import com.ayurveda.appointment.dto.response.AppointmentLifestyleInformationResponse;

public interface AppointmentLifestyleInformationService {

    ApiResponse<AppointmentLifestyleInformationResponse> saveLifestyleInformation(
            CreateAppointmentLifestyleInformationRequest request);

    ApiResponse<AppointmentLifestyleInformationResponse> getLifestyleInformationByBookingId(
            UUID bookingId);

}