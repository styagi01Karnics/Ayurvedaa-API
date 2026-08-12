package com.ayurveda.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.appointment.dto.request.CreateTreatmentRequest;
import com.ayurveda.appointment.dto.request.UpdateTreatmentRequest;
import com.ayurveda.appointment.dto.request.UpdateTreatmentStatusRequest;
import com.ayurveda.appointment.dto.response.TreatmentResponse;
import com.ayurveda.appointment.service.TreatmentService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Treatment", description = "Patient treatment plan APIs")
@RestController
@RequestMapping("/api/v1/treatments")
@RequiredArgsConstructor
@Validated
public class TreatmentController {

    private final TreatmentService treatmentService;

    @Operation(summary = "Create treatment", description = "Creates a treatment plan for a patient.")
    @PostMapping
    public ResponseEntity<ApiResponse<TreatmentResponse>> createTreatment(
            @Valid @RequestBody CreateTreatmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(treatmentService.createTreatment(request));
    }

    @Operation(summary = "List all treatments", description = "Returns all non-deleted treatments.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TreatmentResponse>>> getAllTreatments() {
        return ResponseEntity.ok(treatmentService.getAllTreatments());
    }

    @Operation(
            summary = "List treatments by patient",
            description = "Returns non-deleted treatments for the given patient id.")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<TreatmentResponse>>> getTreatmentsByPatientId(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(treatmentService.getTreatmentsByPatientId(patientId));
    }

    @Operation(summary = "Update treatment", description = "Updates treatment plan details.")
    @PutMapping("/{treatmentId}")
    public ResponseEntity<ApiResponse<TreatmentResponse>> updateTreatment(
            @PathVariable UUID treatmentId,
            @Valid @RequestBody UpdateTreatmentRequest request) {
        return ResponseEntity.ok(treatmentService.updateTreatment(treatmentId, request));
    }

    @Operation(
            summary = "Change treatment status",
            description = "Updates status to SCHEDULED, ONGOING, or COMPLETED.")
    @PutMapping("/{treatmentId}/status")
    public ResponseEntity<ApiResponse<TreatmentResponse>> updateTreatmentStatus(
            @PathVariable UUID treatmentId,
            @Valid @RequestBody UpdateTreatmentStatusRequest request) {
        return ResponseEntity.ok(treatmentService.updateTreatmentStatus(treatmentId, request));
    }

}
