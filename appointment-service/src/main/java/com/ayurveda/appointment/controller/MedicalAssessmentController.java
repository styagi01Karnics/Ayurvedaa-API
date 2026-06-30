package com.ayurveda.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ayurveda.appointment.dto.request.CreateMedicalAssessmentRequest;
import com.ayurveda.appointment.dto.response.MedicalAssessmentResponse;
import com.ayurveda.appointment.service.MedicalAssessmentService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Medical Assessment", description = "Combined medical assessment APIs")
@RestController
@RequestMapping("/api/v1/medical-assessment")
@RequiredArgsConstructor
@Validated
public class MedicalAssessmentController {

    private final MedicalAssessmentService medicalAssessmentService;

    @Operation(summary = "Save medical assessment (JSON)", description = "Saves all 6 assessment sections in one request")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MedicalAssessmentResponse>> saveMedicalAssessment(
            @Valid @RequestBody CreateMedicalAssessmentRequest request) {

        ApiResponse<MedicalAssessmentResponse> response =
                medicalAssessmentService.saveMedicalAssessment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Save medical assessment with documents", description = "JSON data part plus optional file uploads")
    @PostMapping(value = "/with-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MedicalAssessmentResponse>> saveMedicalAssessmentWithDocuments(
            @RequestPart("data") @Valid CreateMedicalAssessmentRequest request,
            @RequestPart(value = "pastMedicalReports", required = false) List<MultipartFile> pastMedicalReports,
            @RequestPart(value = "prescriptions", required = false) List<MultipartFile> prescriptions,
            @RequestPart(value = "labReports", required = false) List<MultipartFile> labReports) {

        ApiResponse<MedicalAssessmentResponse> response = medicalAssessmentService.saveMedicalAssessment(
                request, pastMedicalReports, prescriptions, labReports);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get medical assessment by booking ID")
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<MedicalAssessmentResponse>> getMedicalAssessmentByBookingId(
            @PathVariable UUID bookingId) {

        ApiResponse<MedicalAssessmentResponse> response =
                medicalAssessmentService.getMedicalAssessmentByBookingId(bookingId);

        return ResponseEntity.ok(response);
    }

}
