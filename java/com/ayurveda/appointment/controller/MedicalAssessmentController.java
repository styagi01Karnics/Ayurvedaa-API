package com.ayurveda.appointment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateMedicalAssessmentRequest;
import com.ayurveda.appointment.dto.response.MedicalAssessmentResponse;
import com.ayurveda.appointment.service.MedicalAssessmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medical-assessment")
@RequiredArgsConstructor
@Validated
public class MedicalAssessmentController {

    private final MedicalAssessmentService medicalAssessmentService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<MedicalAssessmentResponse>>
            saveMedicalAssessment(
            @ModelAttribute @Valid CreateMedicalAssessmentRequest request) {

        ApiResponse<MedicalAssessmentResponse> response =
                medicalAssessmentService.saveMedicalAssessment(request);

        return ResponseEntity.ok(response);
    }

}