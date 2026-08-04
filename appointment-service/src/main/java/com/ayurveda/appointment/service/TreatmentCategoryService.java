package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateTreatmentCategoryRequest;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;

public interface TreatmentCategoryService {

    /** Creates a new treatment category. */
    ApiResponse<TreatmentCategoryResponse> createTreatmentCategory(
            CreateTreatmentCategoryRequest request);

    /** Fetches a treatment category by ID. */
    ApiResponse<TreatmentCategoryResponse> getTreatmentCategoryById(
            UUID categoryId);

    /** Lists all treatment categories. */
    ApiResponse<List<TreatmentCategoryResponse>> getAllTreatmentCategories();

}
