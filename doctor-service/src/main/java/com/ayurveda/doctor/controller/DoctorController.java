package com.ayurveda.doctor.controller;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.doctor.dto.request.CreateDoctorRequest;
import com.ayurveda.doctor.dto.request.UpdateDoctorStatusRequest;
import com.ayurveda.doctor.dto.response.DoctorResponse;
import com.ayurveda.doctor.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Doctor", description = "Doctor master APIs")
@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Validated
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary = "Add doctor")
    @PostMapping
    public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(
            @Valid @RequestBody CreateDoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctor(request));
    }

    @Operation(summary = "List active doctors only")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getActiveDoctors() {
        return ResponseEntity.ok(doctorService.getActiveDoctors());
    }

    @Operation(summary = "Get doctor by ID")
    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
    }

    @Operation(summary = "List doctors")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @Operation(
            summary = "Change doctor status",
            description = "Updates status to ACTIVE or INACTIVE. Does not change deleted flag.")
    @PatchMapping("/{doctorId}/status")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctorStatus(
            @PathVariable UUID doctorId,
            @Valid @RequestBody UpdateDoctorStatusRequest request) {
        return ResponseEntity.ok(doctorService.updateDoctorStatus(doctorId, request));
    }

    @Operation(summary = "Delete doctor")
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorService.deleteDoctor(doctorId));
    }

}
