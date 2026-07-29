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

    ApiResponse<TherapyResponse> createTherapy(CreateTherapyRequest request);

    ApiResponse<TherapyResponse> getTherapyById(UUID therapyId);

    ApiResponse<List<TherapyResponse>> getAllTherapies(TherapyMasterStatus status);

    ApiResponse<List<TherapyResponse>> getTherapiesByCategory(
            UUID categoryId, TherapyMasterStatus status);

    ApiResponse<TherapyResponse> updateTherapy(UUID therapyId, UpdateTherapyRequest request);

    ApiResponse<TherapyResponse> updateTherapyStatus(
            UUID therapyId, UpdateTherapyStatusRequest request);

    ApiResponse<TherapyResponse> deleteTherapy(UUID therapyId);

}
