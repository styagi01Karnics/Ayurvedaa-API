package com.ayurveda.doctor.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;
import com.ayurveda.doctor.dto.request.AssignDoctorUserCodeRequest;
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

    /** Lists non-deleted doctors (paginated). */
    ApiResponse<PagedResponse<DoctorResponse>> getAllDoctors(int page, int size);

    /** Lists active non-deleted doctors (paginated). */
    ApiResponse<PagedResponse<DoctorResponse>> getActiveDoctors(int page, int size);

    /** Lists active non-deleted doctors without a user code, for dropdowns (not paginated). */
    ApiResponse<List<DoctorResponse>> getActiveDoctorsWithoutUserCode();

    /** Updates doctor status (ACTIVE / INACTIVE). */
    ApiResponse<DoctorResponse> updateDoctorStatus(UUID doctorId, UpdateDoctorStatusRequest request);

    /** Soft-deletes a doctor. */
    ApiResponse<Void> deleteDoctor(UUID doctorId);

    /** Updates doctor data by id. */
    ApiResponse<DoctorResponse> updateDoctorData(UUID doctorId, CreateDoctorRequest request);

    /** Links a user code to an existing doctor. Set once; unique among non-deleted doctors. */
    ApiResponse<DoctorResponse> assignUserCode(UUID doctorId, AssignDoctorUserCodeRequest request);

}
