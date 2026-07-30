package com.ayurveda.therapist.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.therapist.dto.request.CreateTherapistRequest;
import com.ayurveda.therapist.dto.request.UpdateTherapistRequest;
import com.ayurveda.therapist.dto.request.UpdateTherapistStatusRequest;
import com.ayurveda.therapist.dto.response.TherapistResponse;

import java.util.List;
import java.util.UUID;

public interface TherapistService {

    ApiResponse<TherapistResponse> createTherapist(CreateTherapistRequest request);

    ApiResponse<TherapistResponse> updateTherapist(UUID therapistId, UpdateTherapistRequest request);

    ApiResponse<TherapistResponse> getTherapistById(UUID therapistId);

    ApiResponse<List<TherapistResponse>> getAllTherapists();

    ApiResponse<List<TherapistResponse>> getTherapistsByTherapyIds(List<UUID> therapyIds);

    ApiResponse<TherapistResponse> updateTherapistStatus(
            UUID therapistId, UpdateTherapistStatusRequest request);

    ApiResponse<Void> deleteTherapist(UUID therapistId);

}
