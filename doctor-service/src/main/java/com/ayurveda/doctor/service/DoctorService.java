package com.ayurveda.doctor.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.doctor.dto.request.CreateDoctorRequest;
import com.ayurveda.doctor.dto.request.UpdateDoctorStatusRequest;
import com.ayurveda.doctor.dto.response.DoctorResponse;

import java.util.List;
import java.util.UUID;

public interface DoctorService {

    ApiResponse<DoctorResponse> createDoctor(CreateDoctorRequest request);

    ApiResponse<DoctorResponse> getDoctorById(UUID doctorId);

    ApiResponse<List<DoctorResponse>> getAllDoctors();

    ApiResponse<List<DoctorResponse>> getActiveDoctors();

    ApiResponse<DoctorResponse> updateDoctorStatus(UUID doctorId, UpdateDoctorStatusRequest request);

    ApiResponse<Void> deleteDoctor(UUID doctorId);

}
