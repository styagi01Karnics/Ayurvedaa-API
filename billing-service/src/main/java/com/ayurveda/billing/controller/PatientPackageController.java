package com.ayurveda.billing.controller;

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

import com.ayurveda.billing.dto.request.CreatePatientPackageRequest;
import com.ayurveda.billing.dto.request.UpdatePatientPackageRequest;
import com.ayurveda.billing.dto.request.UpdatePatientPackageStatusRequest;
import com.ayurveda.billing.dto.response.PatientPackageResponse;
import com.ayurveda.billing.service.PatientPackageService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Patient Packages", description = "Patient billing package APIs")
@RestController
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
@Validated
public class PatientPackageController {

    private final PatientPackageService patientPackageService;

    @Operation(summary = "Create patient package")
    @PostMapping
    public ResponseEntity<ApiResponse<PatientPackageResponse>> createPackage(
            @Valid @RequestBody CreatePatientPackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientPackageService.createPackage(request));
    }

    @Operation(summary = "List all patient packages")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientPackageResponse>>> getAllPackages() {
        return ResponseEntity.ok(patientPackageService.getAllPackages());
    }

    @Operation(summary = "List packages by patient id")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<PatientPackageResponse>>> getPackagesByPatientId(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(patientPackageService.getPackagesByPatientId(patientId));
    }

    @Operation(summary = "Update patient package")
    @PutMapping("/{packageId}")
    public ResponseEntity<ApiResponse<PatientPackageResponse>> updatePackage(
            @PathVariable UUID packageId,
            @Valid @RequestBody UpdatePatientPackageRequest request) {
        return ResponseEntity.ok(patientPackageService.updatePackage(packageId, request));
    }

    @Operation(
            summary = "Change package status",
            description = "Updates status to SCHEDULED, ONGOING, or COMPLETED.")
    @PutMapping("/{packageId}/status")
    public ResponseEntity<ApiResponse<PatientPackageResponse>> updatePackageStatus(
            @PathVariable UUID packageId,
            @Valid @RequestBody UpdatePatientPackageStatusRequest request) {
        return ResponseEntity.ok(patientPackageService.updatePackageStatus(packageId, request));
    }

}
