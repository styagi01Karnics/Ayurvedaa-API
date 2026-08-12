package com.ayurveda.therapist.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.therapist.dto.request.CreateTherapistRequest;
import com.ayurveda.therapist.dto.request.UpdateTherapistRequest;
import com.ayurveda.therapist.dto.request.UpdateTherapistStatusRequest;
import com.ayurveda.therapist.dto.response.TherapistResponse;

import java.util.List;
import java.util.UUID;

public interface TherapistService {

    /** Creates a new therapist master record. */
    ApiResponse<TherapistResponse> createTherapist(CreateTherapistRequest request);

    /** Updates an existing therapist. */
    ApiResponse<TherapistResponse> updateTherapist(UUID therapistId, UpdateTherapistRequest request);

    /** Fetches a therapist by ID. */
    ApiResponse<TherapistResponse> getTherapistById(UUID therapistId);

    /** Lists all non-deleted therapists. */
    ApiResponse<List<TherapistResponse>> getAllTherapists();

    /** Lists therapists assigned to any of the given therapy IDs. */
    ApiResponse<List<TherapistResponse>> getTherapistsByTherapyIds(List<UUID> therapyIds);

    /** Updates therapist status (ACTIVE / INACTIVE). */
    ApiResponse<TherapistResponse> updateTherapistStatus(
            UUID therapistId, UpdateTherapistStatusRequest request);

    /** Soft-deletes a therapist. */
    ApiResponse<Void> deleteTherapist(UUID therapistId);

}
