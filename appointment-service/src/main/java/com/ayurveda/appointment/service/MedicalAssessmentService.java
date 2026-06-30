package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.ayurveda.appointment.dto.request.CreateMedicalAssessmentRequest;
import com.ayurveda.appointment.dto.response.MedicalAssessmentResponse;
import com.ayurveda.common.ApiResponse;

public interface MedicalAssessmentService {

    ApiResponse<MedicalAssessmentResponse> saveMedicalAssessment(
            CreateMedicalAssessmentRequest request);

    ApiResponse<MedicalAssessmentResponse> saveMedicalAssessment(
            CreateMedicalAssessmentRequest request,
            List<MultipartFile> pastMedicalReports,
            List<MultipartFile> prescriptions,
            List<MultipartFile> labReports);

    ApiResponse<MedicalAssessmentResponse> getMedicalAssessmentByBookingId(UUID bookingId);

}
