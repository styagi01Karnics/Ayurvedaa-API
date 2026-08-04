package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentLifestyleInformationRequest;
import com.ayurveda.appointment.dto.response.AppointmentLifestyleInformationResponse;
import com.ayurveda.appointment.service.AppointmentLifestyleInformationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Lifestyle Information", description = "Appointment lifestyle information APIs")
@RestController
@RequestMapping("/api/v1/lifestyle-information")
@RequiredArgsConstructor
@Validated
public class AppointmentLifestyleInformationController {

    private final AppointmentLifestyleInformationService
            appointmentLifestyleInformationService;

    @Operation(summary = "Save lifestyle information", description = "Creates or updates lifestyle information for a patient.")
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentLifestyleInformationResponse>>
            saveLifestyleInformation(
            @Valid @RequestBody CreateAppointmentLifestyleInformationRequest request) {

        ApiResponse<AppointmentLifestyleInformationResponse> response =
                appointmentLifestyleInformationService
                        .saveLifestyleInformation(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Get lifestyle information by patient ID", description = "Returns lifestyle information for the given patient.")
    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<AppointmentLifestyleInformationResponse>>
            getLifestyleInformationByPatientId(
            @PathVariable UUID patientId) {

        ApiResponse<AppointmentLifestyleInformationResponse> response =
                appointmentLifestyleInformationService
                        .getLifestyleInformationByPatientId(patientId);

        return ResponseEntity.ok(response);
    }

}
