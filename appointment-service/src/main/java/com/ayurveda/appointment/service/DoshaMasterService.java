package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.dto.request.CreateDoshaRequest;
import com.ayurveda.appointment.dto.response.DoshaResponse;
import com.ayurveda.common.ApiResponse;

public interface DoshaMasterService {

    ApiResponse<DoshaResponse> createDosha(CreateDoshaRequest request);

    ApiResponse<DoshaResponse> getDoshaById(UUID doshaId);

    ApiResponse<List<DoshaResponse>> getAllDoshas();

}
