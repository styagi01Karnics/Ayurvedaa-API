package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.ayurveda.appointment.dto.request.CreateMedicalAssessmentRequest;
import com.ayurveda.appointment.dto.response.MedicalAssessmentResponse;
import com.ayurveda.common.ApiResponse;

public interface MedicalAssessmentService {

    /** Saves all medical assessment sections for a patient (JSON only). */
    ApiResponse<MedicalAssessmentResponse> saveMedicalAssessment(
            CreateMedicalAssessmentRequest request);

    /** Saves all medical assessment sections with optional document uploads. */
    ApiResponse<MedicalAssessmentResponse> saveMedicalAssessment(
            CreateMedicalAssessmentRequest request,
            List<MultipartFile> pastMedicalReports,
            List<MultipartFile> prescriptions,
            List<MultipartFile> labReports);

    /** Fetches the complete medical assessment for a patient. */
    ApiResponse<MedicalAssessmentResponse> getMedicalAssessmentByPatientId(UUID patientId);

}
