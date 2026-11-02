package com.ayurveda.appointment.service;

import com.ayurveda.appointment.dto.request.CreateDoshaRequest;
import com.ayurveda.appointment.dto.response.DoshaResponse;
import com.ayurveda.common.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface DoshaMasterService {

    /** Creates a new dosha master record. */
    ApiResponse<DoshaResponse> createDosha(CreateDoshaRequest request);

    /** Fetches a dosha by ID. */
    ApiResponse<DoshaResponse> getDoshaById(UUID doshaId);

    /** Lists all non-deleted doshas. */
    ApiResponse<List<DoshaResponse>> getAllDoshas();

    /**
     * Updates a dosha master record by id.
     */
    ApiResponse<DoshaResponse> updateDoshaMaster(UUID doshaId, CreateDoshaRequest request);

    /**
     * Deactivates a dosha master record by id.
     */
    ApiResponse<Void> deleteDoshaMaster(UUID doshaId);

}
