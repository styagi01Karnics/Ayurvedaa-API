package com.ayurveda.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentTherapyRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;
import com.ayurveda.appointment.dto.response.TherapistTodayScheduleResponse;
import com.ayurveda.appointment.service.AppointmentTherapyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Appointment Therapy", description = "Appointment therapy assignment APIs")
@RestController
@RequestMapping("/api/v1/appointment-therapies")
@RequiredArgsConstructor
@Validated
public class AppointmentTherapyController {

    private final AppointmentTherapyService appointmentTherapyService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentTherapyResponse>>
            createAppointmentTherapy(
            @Valid @RequestBody CreateAppointmentTherapyRequest request) {

        ApiResponse<AppointmentTherapyResponse> response =
                appointmentTherapyService
                        .createAppointmentTherapy(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get therapist's today scheduled slots",
            description = "For a therapist: today's therapy time slots ordered by time. Cancelled excluded.")
    @GetMapping("/therapist/{therapistId}/today")
    public ResponseEntity<ApiResponse<TherapistTodayScheduleResponse>> getTherapistTodaySchedule(
            @PathVariable UUID therapistId) {

        return ResponseEntity.ok(
                appointmentTherapyService.getTherapistTodaySchedule(therapistId));
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<List<AppointmentTherapyResponse>>> getAppointmentTherapyByPatientId(
            @PathVariable UUID patientId) {

        ApiResponse<List<AppointmentTherapyResponse>> response =
                appointmentTherapyService.getAppointmentTherapyByPatientId(patientId);

        return ResponseEntity.ok(response);
    }

}
