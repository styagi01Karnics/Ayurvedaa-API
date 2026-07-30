package com.ayurveda.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.request.UpdateTherapyRequest;
import com.ayurveda.appointment.dto.request.UpdateTherapyStatusRequest;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.enums.TherapyMasterStatus;
import com.ayurveda.appointment.service.TherapyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Therapy Master", description = "Therapy master data APIs")
@RestController
@RequestMapping("/api/v1/therapies")
@RequiredArgsConstructor
@Validated
public class TherapyController {

    private final TherapyService therapyService;

    @Operation(summary = "Add therapy")
    @PostMapping
    public ResponseEntity<ApiResponse<TherapyResponse>> createTherapy(
            @Valid @RequestBody CreateTherapyRequest request) {

        ApiResponse<TherapyResponse> response =
                therapyService.createTherapy(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "List therapies",
            description = "Returns all non-deleted therapies (ACTIVE and INACTIVE). Optional status filter.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TherapyResponse>>> getAllTherapies(
            @RequestParam(required = false) TherapyMasterStatus status) {

        ApiResponse<List<TherapyResponse>> response =
                therapyService.getAllTherapies(status);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List therapies by category",
            description = "Returns non-deleted therapies for the category (ACTIVE and INACTIVE). Optional status filter.")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<TherapyResponse>>> getTherapiesByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(required = false) TherapyMasterStatus status) {

        ApiResponse<List<TherapyResponse>> response =
                therapyService.getTherapiesByCategory(categoryId, status);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get therapy by ID")
    @GetMapping("/{therapyId}")
    public ResponseEntity<ApiResponse<TherapyResponse>> getTherapyById(
            @PathVariable UUID therapyId) {

        ApiResponse<TherapyResponse> response =
                therapyService.getTherapyById(therapyId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update therapy")
    @PutMapping("/{therapyId}")
    public ResponseEntity<ApiResponse<TherapyResponse>> updateTherapy(
            @PathVariable UUID therapyId,
            @Valid @RequestBody UpdateTherapyRequest request) {
        return ResponseEntity.ok(therapyService.updateTherapy(therapyId, request));
    }

    @Operation(
            summary = "Change therapy status",
            description = "Updates status to ACTIVE or INACTIVE. Does not change deleted flag.")
    @PatchMapping("/{therapyId}/status")
    public ResponseEntity<ApiResponse<TherapyResponse>> updateTherapyStatus(
            @PathVariable UUID therapyId,
            @Valid @RequestBody UpdateTherapyStatusRequest request) {
        return ResponseEntity.ok(therapyService.updateTherapyStatus(therapyId, request));
    }

    @Operation(summary = "Delete therapy")
    @DeleteMapping("/{therapyId}")
    public ResponseEntity<ApiResponse<TherapyResponse>> deleteTherapy(@PathVariable UUID therapyId) {
        return ResponseEntity.ok(therapyService.deleteTherapy(therapyId));
    }

}
