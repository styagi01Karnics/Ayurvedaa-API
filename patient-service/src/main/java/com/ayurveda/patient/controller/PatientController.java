package com.ayurveda.patient.controller;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.patient.dto.request.CreatePatientRequest;
import com.ayurveda.patient.dto.response.PatientResponse;
import com.ayurveda.patient.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/patients")
@Tag(name = "Patient Management", description = "Patient Management APIs")
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Create Patient")
    @PostMapping("/create-patient")
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {

        log.info("Received request to create patient.");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.createPatient(request));
    }

    @Operation(summary = "Get Patient By ID")
    @GetMapping("/get-patient/{patientId}")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(
            @PathVariable UUID patientId) {

        log.info("Received request to fetch patient: {}", patientId);

        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

    @Operation(summary = "Get All Patients")
    @GetMapping("/get-all-patients")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients() {

        log.info("Received request to fetch all patients.");

        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @Operation(summary = "Get Total Patient Count")
    @GetMapping("/get-patient-count")
    public ResponseEntity<ApiResponse<Long>> getTotalPatientCount() {

        log.info("Received request to fetch total patient count.");

        return ResponseEntity.ok(patientService.getTotalPatientCount());
    }

    @Operation(
            summary = "Delete Patient",
            description = "Soft delete patient by patient ID."
    )
    @DeleteMapping("/delete-patient/{patientId}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(
            @PathVariable UUID patientId) {

        log.info("Received request to delete patient. Patient ID: {}", patientId);

        return ResponseEntity.ok(
                patientService.deletePatient(patientId)
        );
    }

}

