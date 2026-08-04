package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.request.UpdateTherapyRequest;
import com.ayurveda.appointment.dto.request.UpdateTherapyStatusRequest;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.enums.TherapyMasterStatus;

public interface TherapyService {

    /** Creates a new therapy master record. */
    ApiResponse<TherapyResponse> createTherapy(CreateTherapyRequest request);

    /** Fetches a therapy by ID. */
    ApiResponse<TherapyResponse> getTherapyById(UUID therapyId);

    /** Lists all non-deleted therapies, optionally filtered by status. */
    ApiResponse<List<TherapyResponse>> getAllTherapies(TherapyMasterStatus status);

    /** Lists therapies for a category, optionally filtered by status. */
    ApiResponse<List<TherapyResponse>> getTherapiesByCategory(
            UUID categoryId, TherapyMasterStatus status);

    /** Updates an existing therapy. */
    ApiResponse<TherapyResponse> updateTherapy(UUID therapyId, UpdateTherapyRequest request);

    /** Updates therapy status (ACTIVE / INACTIVE). */
    ApiResponse<TherapyResponse> updateTherapyStatus(
            UUID therapyId, UpdateTherapyStatusRequest request);

    /** Soft-deletes a therapy. */
    ApiResponse<TherapyResponse> deleteTherapy(UUID therapyId);

}
