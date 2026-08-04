package com.ayurveda.doctor.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.doctor.dto.request.CreateDoctorRequest;
import com.ayurveda.doctor.dto.request.UpdateDoctorStatusRequest;
import com.ayurveda.doctor.dto.response.DoctorResponse;

import java.util.List;
import java.util.UUID;

public interface DoctorService {

    /** Creates a new doctor master record. */
    ApiResponse<DoctorResponse> createDoctor(CreateDoctorRequest request);

    /** Fetches a doctor by ID. */
    ApiResponse<DoctorResponse> getDoctorById(UUID doctorId);

    /** Lists all non-deleted doctors. */
    ApiResponse<List<DoctorResponse>> getAllDoctors();

    /** Lists active non-deleted doctors. */
    ApiResponse<List<DoctorResponse>> getActiveDoctors();

    /** Updates doctor status (ACTIVE / INACTIVE). */
    ApiResponse<DoctorResponse> updateDoctorStatus(UUID doctorId, UpdateDoctorStatusRequest request);

    /** Soft-deletes a doctor. */
    ApiResponse<Void> deleteDoctor(UUID doctorId);

}
