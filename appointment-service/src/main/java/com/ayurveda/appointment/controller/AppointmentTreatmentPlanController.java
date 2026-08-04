package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentTreatmentPlanRequest;
import com.ayurveda.appointment.dto.response.AppointmentTreatmentPlanResponse;
import com.ayurveda.appointment.service.AppointmentTreatmentPlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Treatment Plan", description = "Appointment treatment plan APIs")
@RestController
@RequestMapping("/api/v1/treatment-plans")
@RequiredArgsConstructor
@Validated
public class AppointmentTreatmentPlanController {

    private final AppointmentTreatmentPlanService
            appointmentTreatmentPlanService;

    @Operation(summary = "Save treatment plan", description = "Creates or updates treatment plan for a patient.")
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentTreatmentPlanResponse>>
            saveTreatmentPlan(
            @Valid @RequestBody CreateAppointmentTreatmentPlanRequest request) {

        ApiResponse<AppointmentTreatmentPlanResponse> response =
                appointmentTreatmentPlanService
                        .saveTreatmentPlan(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get treatment plan by patient ID", description = "Returns treatment plan for the given patient.")
    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<AppointmentTreatmentPlanResponse>>
            getTreatmentPlanByPatientId(
            @PathVariable UUID patientId) {

        ApiResponse<AppointmentTreatmentPlanResponse> response =
                appointmentTreatmentPlanService
                        .getTreatmentPlanByPatientId(patientId);

        return ResponseEntity.ok(response);
    }

}
