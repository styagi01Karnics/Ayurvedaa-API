package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.dto.request.CreateFollowUpRequest;
import com.ayurveda.appointment.dto.request.UpdateFollowUpStatusRequest;
import com.ayurveda.appointment.dto.response.FollowUpResponse;
import com.ayurveda.common.ApiResponse;

public interface FollowUpService {

    /** Creates a follow-up record (does not create an appointment booking). */
    ApiResponse<FollowUpResponse> createFollowUp(CreateFollowUpRequest request);

    /** Returns all non-deleted follow-ups (All Follow Ups tab). */
    ApiResponse<List<FollowUpResponse>> getAllFollowUps();

    /** Returns non-deleted follow-ups for a patient. */
    ApiResponse<List<FollowUpResponse>> getFollowUpsByPatientId(UUID patientId);

    /** Changes follow-up status (UPCOMING, MISSED, COMPLETED, CANCELLED). */
    ApiResponse<FollowUpResponse> updateFollowUpStatus(
            UUID followUpId, UpdateFollowUpStatusRequest request);

    /** Cancels a follow-up (sets status to CANCELLED). */
    ApiResponse<FollowUpResponse> cancelFollowUp(UUID followUpId);

}
