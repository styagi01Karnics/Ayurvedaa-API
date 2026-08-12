package com.ayurveda.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.appointment.dto.request.CreateTreatmentPlanMasterRequest;
import com.ayurveda.appointment.dto.response.TreatmentPlanMasterResponse;
import com.ayurveda.appointment.service.TreatmentPlanMasterService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Treatment Plan Master", description = "Treatment plan name master data APIs")
@RestController
@RequestMapping("/api/v1/treatment-plan-masters")
@RequiredArgsConstructor
@Validated
public class TreatmentPlanMasterController {

    private final TreatmentPlanMasterService treatmentPlanMasterService;

    @Operation(summary = "Create treatment plan", description = "Creates a new treatment plan master record.")
    @PostMapping
    public ResponseEntity<ApiResponse<TreatmentPlanMasterResponse>> create(
            @Valid @RequestBody CreateTreatmentPlanMasterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(treatmentPlanMasterService.create(request));
    }

    @Operation(
            summary = "List all treatment plans",
            description = "Returns all non-deleted treatment plans (ACTIVE and INACTIVE).")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TreatmentPlanMasterResponse>>> getAll() {
        return ResponseEntity.ok(treatmentPlanMasterService.getAll());
    }

    @Operation(
            summary = "List active treatment plans",
            description = "Returns only ACTIVE non-deleted treatment plans.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TreatmentPlanMasterResponse>>> getActive() {
        return ResponseEntity.ok(treatmentPlanMasterService.getActive());
    }

    @Operation(summary = "Get treatment plan by ID", description = "Returns a non-deleted treatment plan by ID.")
    @GetMapping("/{treatmentPlanId}")
    public ResponseEntity<ApiResponse<TreatmentPlanMasterResponse>> getById(
            @PathVariable UUID treatmentPlanId) {
        return ResponseEntity.ok(treatmentPlanMasterService.getById(treatmentPlanId));
    }

}
