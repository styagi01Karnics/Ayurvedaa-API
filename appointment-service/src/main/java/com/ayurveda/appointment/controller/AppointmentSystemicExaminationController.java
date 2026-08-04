package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentSystemicExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentSystemicExaminationResponse;
import com.ayurveda.appointment.service.AppointmentSystemicExaminationService;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Save systemic examination", description = "Creates or updates systemic examination for a patient.")
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentSystemicExaminationResponse>>
            saveSystemicExamination(
            @Valid @RequestBody CreateAppointmentSystemicExaminationRequest request) {

        ApiResponse<AppointmentSystemicExaminationResponse> response =
                appointmentSystemicExaminationService
                        .saveSystemicExamination(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get systemic examination by patient ID", description = "Returns systemic examination for the given patient.")
    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<AppointmentSystemicExaminationResponse>>
            getSystemicExaminationByPatientId(
            @PathVariable UUID patientId) {

        ApiResponse<AppointmentSystemicExaminationResponse> response =
                appointmentSystemicExaminationService
                        .getSystemicExaminationByPatientId(patientId);

        return ResponseEntity.ok(response);
    }

}
