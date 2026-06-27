package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateDoctorRequest;
import com.ayurveda.appointment.dto.response.DoctorResponse;

public interface DoctorService {

    ApiResponse<DoctorResponse> createDoctor(CreateDoctorRequest request);

    ApiResponse<DoctorResponse> getDoctorById(UUID doctorId);

    ApiResponse<List<DoctorResponse>> getAllDoctors();

}