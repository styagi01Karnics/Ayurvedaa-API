package com.ayurveda.medicine.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.medicine.dto.request.CreateMedicineCategoryRequest;
import com.ayurveda.medicine.dto.request.UpdateMedicineCategoryRequest;
import com.ayurveda.medicine.dto.response.MedicineCategoryResponse;

public interface MedicineCategoryService {

    List<String> getActiveCategoryCodes();

    ApiResponse<List<MedicineCategoryResponse>> getCategories();

    ApiResponse<MedicineCategoryResponse> createCategory(CreateMedicineCategoryRequest request);

    ApiResponse<MedicineCategoryResponse> updateCategory(UUID categoryId, UpdateMedicineCategoryRequest request);

    ApiResponse<Void> deleteCategory(UUID categoryId);

    /** Throws BadRequestException unless the code exists in the master and is active. */
    void assertActiveCode(String code);

}
