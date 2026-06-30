package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentSystemicExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentSystemicExaminationResponse;
import com.ayurveda.appointment.service.AppointmentSystemicExaminationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Systemic Examination", description = "Appointment systemic examination APIs")
@RestController
@RequestMapping("/api/v1/systemic-examinations")
@RequiredArgsConstructor
@Validated
public class AppointmentSystemicExaminationController {

    private final AppointmentSystemicExaminationService
            appointmentSystemicExaminationService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentSystemicExaminationResponse>>
            saveSystemicExamination(
            @Valid @RequestBody CreateAppointmentSystemicExaminationRequest request) {

        ApiResponse<AppointmentSystemicExaminationResponse> response =
                appointmentSystemicExaminationService
                        .saveSystemicExamination(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<AppointmentSystemicExaminationResponse>>
            getSystemicExaminationByBookingId(
            @PathVariable UUID bookingId) {

        ApiResponse<AppointmentSystemicExaminationResponse> response =
                appointmentSystemicExaminationService
                        .getSystemicExaminationByBookingId(bookingId);

        return ResponseEntity.ok(response);
    }

}