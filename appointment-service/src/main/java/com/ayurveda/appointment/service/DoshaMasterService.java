package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.dto.request.CreateDoshaRequest;
import com.ayurveda.appointment.dto.response.DoshaResponse;
import com.ayurveda.common.ApiResponse;

public interface DoshaMasterService {

    /** Creates a new dosha master record. */
    ApiResponse<DoshaResponse> createDosha(CreateDoshaRequest request);

    /** Fetches a dosha by ID. */
    ApiResponse<DoshaResponse> getDoshaById(UUID doshaId);

    /** Lists all non-deleted doshas. */
    ApiResponse<List<DoshaResponse>> getAllDoshas();

}
