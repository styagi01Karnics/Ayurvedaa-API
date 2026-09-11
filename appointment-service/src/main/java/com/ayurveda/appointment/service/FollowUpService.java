package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.appointment.dto.request.CreateFollowUpRequest;
import com.ayurveda.appointment.dto.request.UpdateFollowUpStatusRequest;
import com.ayurveda.appointment.dto.response.FollowUpResponse;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;

public interface FollowUpService {

    ApiResponse<FollowUpResponse> createFollowUp(CreateFollowUpRequest request);

    ApiResponse<PagedResponse<FollowUpResponse>> getAllFollowUps(int page, int size);

    ApiResponse<PagedResponse<FollowUpResponse>> getFollowUpsByPatientId(
            UUID patientId, int page, int size);

    ApiResponse<FollowUpResponse> updateFollowUpStatus(
            UUID followUpId, UpdateFollowUpStatusRequest request);

    ApiResponse<FollowUpResponse> cancelFollowUp(UUID followUpId);

}
