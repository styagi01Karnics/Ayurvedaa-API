package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentMedicalHistoryRequest;
import com.ayurveda.appointment.dto.response.AppointmentMedicalHistoryResponse;
import com.ayurveda.appointment.service.AppointmentMedicalHistoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Medical History", description = "Appointment medical history APIs")
@RestController
@RequestMapping("/api/v1/medical-histories")
@RequiredArgsConstructor
@Validated
public class AppointmentMedicalHistoryController {

    private final AppointmentMedicalHistoryService
            appointmentMedicalHistoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentMedicalHistoryResponse>>
            saveMedicalHistory(
            @Valid @RequestBody CreateAppointmentMedicalHistoryRequest request) {

        ApiResponse<AppointmentMedicalHistoryResponse> response =
                appointmentMedicalHistoryService.saveMedicalHistory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<AppointmentMedicalHistoryResponse>>
            getMedicalHistoryByBookingId(
            @PathVariable UUID bookingId) {

        ApiResponse<AppointmentMedicalHistoryResponse> response =
                appointmentMedicalHistoryService
                        .getMedicalHistoryByBookingId(bookingId);

        return ResponseEntity.ok(response);
    }

}