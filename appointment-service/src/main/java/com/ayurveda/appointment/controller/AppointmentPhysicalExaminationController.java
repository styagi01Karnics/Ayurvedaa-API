package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentPhysicalExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentPhysicalExaminationResponse;
import com.ayurveda.appointment.service.AppointmentPhysicalExaminationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Physical Examination", description = "Appointment physical examination APIs")
@RestController
@RequestMapping("/api/v1/physical-examinations")
@RequiredArgsConstructor
@Validated
public class AppointmentPhysicalExaminationController {

    private final AppointmentPhysicalExaminationService
            appointmentPhysicalExaminationService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentPhysicalExaminationResponse>>
            savePhysicalExamination(
            @Valid @RequestBody CreateAppointmentPhysicalExaminationRequest request) {

        ApiResponse<AppointmentPhysicalExaminationResponse> response =
                appointmentPhysicalExaminationService
                        .savePhysicalExamination(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<AppointmentPhysicalExaminationResponse>>
            getPhysicalExaminationByPatientId(
            @PathVariable UUID patientId) {

        ApiResponse<AppointmentPhysicalExaminationResponse> response =
                appointmentPhysicalExaminationService
                        .getPhysicalExaminationByPatientId(patientId);

        return ResponseEntity.ok(response);
    }

}