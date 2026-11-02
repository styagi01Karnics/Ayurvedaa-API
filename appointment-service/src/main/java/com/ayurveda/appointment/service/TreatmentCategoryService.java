package com.ayurveda.appointment.service;

import com.ayurveda.appointment.dto.request.CreateTreatmentCategoryRequest;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;
import com.ayurveda.common.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface TreatmentCategoryService {

    /**
     * Creates a new treatment category.
     */
    ApiResponse<TreatmentCategoryResponse> createTreatmentCategory(
            CreateTreatmentCategoryRequest request);

    /**
     * Fetches a treatment category by ID.
     */
    ApiResponse<TreatmentCategoryResponse> getTreatmentCategoryById(
            UUID categoryId);

    /**
     * Lists all treatment categories.
     */
    ApiResponse<List<TreatmentCategoryResponse>> getAllTreatmentCategories();

    /**
     * Updates a treatment category by id.
     */
    ApiResponse<TreatmentCategoryResponse> updateTreatmentCategory(UUID categoryId, CreateTreatmentCategoryRequest request);

    /**
     * Deactivates a treatment category by id.
     */
    ApiResponse<Void> deleteTreatmentCategory(UUID categoryId);

}
