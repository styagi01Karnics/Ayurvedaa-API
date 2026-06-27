package com.ayurveda.appointment.service;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateMedicalAssessmentRequest;
import com.ayurveda.appointment.dto.response.MedicalAssessmentResponse;

public interface MedicalAssessmentService {

    ApiResponse<MedicalAssessmentResponse> saveMedicalAssessment(
            CreateMedicalAssessmentRequest request);

}