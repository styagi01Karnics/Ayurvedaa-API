package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.response.TherapyResponse;

public interface TherapyService {

    ApiResponse<TherapyResponse> createTherapy(
            CreateTherapyRequest request);

    ApiResponse<TherapyResponse> getTherapyById(
            UUID therapyId);

    ApiResponse<List<TherapyResponse>> getAllTherapies();

    ApiResponse<List<TherapyResponse>> getTherapiesByCategory(
            UUID categoryId);

}