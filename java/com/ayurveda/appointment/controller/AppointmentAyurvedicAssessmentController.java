package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentAyurvedicAssessmentRequest;
import com.ayurveda.appointment.dto.response.AppointmentAyurvedicAssessmentResponse;
import com.ayurveda.appointment.service.AppointmentAyurvedicAssessmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<AppointmentAyurvedicAssessmentResponse>>
            getAyurvedicAssessmentByBookingId(
            @PathVariable UUID bookingId) {

        ApiResponse<AppointmentAyurvedicAssessmentResponse> response =
                appointmentAyurvedicAssessmentService
                        .getAyurvedicAssessmentByBookingId(bookingId);

        return ResponseEntity.ok(response);
    }

}