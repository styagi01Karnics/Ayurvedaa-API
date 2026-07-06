package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentAyurvedicAssessmentRequest;
import com.ayurveda.appointment.dto.response.AppointmentAyurvedicAssessmentResponse;
import com.ayurveda.appointment.service.AppointmentAyurvedicAssessmentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Ayurvedic Assessment", description = "Appointment ayurvedic assessment APIs")
@RestController
@RequestMapping("/api/v1/ayurvedic-assessments")
@RequiredArgsConstructor
@Validated
public class AppointmentAyurvedicAssessmentController {

    private final AppointmentAyurvedicAssessmentService
            appointmentAyurvedicAssessmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentAyurvedicAssessmentResponse>>
            saveAyurvedicAssessment(
            @Valid @RequestBody CreateAppointmentAyurvedicAssessmentRequest request) {

        ApiResponse<AppointmentAyurvedicAssessmentResponse> response =
                appointmentAyurvedicAssessmentService
                        .saveAyurvedicAssessment(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<AppointmentAyurvedicAssessmentResponse>>
            getAyurvedicAssessmentByPatientId(
            @PathVariable UUID patientId) {

        ApiResponse<AppointmentAyurvedicAssessmentResponse> response =
                appointmentAyurvedicAssessmentService
                        .getAyurvedicAssessmentByPatientId(patientId);

        return ResponseEntity.ok(response);
    }

}