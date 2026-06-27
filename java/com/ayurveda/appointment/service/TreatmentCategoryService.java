package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateTreatmentCategoryRequest;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;

public interface TreatmentCategoryService {

    ApiResponse<TreatmentCategoryResponse> createTreatmentCategory(
            CreateTreatmentCategoryRequest request);

    ApiResponse<TreatmentCategoryResponse> getTreatmentCategoryById(
            UUID categoryId);

    ApiResponse<List<TreatmentCategoryResponse>> getAllTreatmentCategories();

}