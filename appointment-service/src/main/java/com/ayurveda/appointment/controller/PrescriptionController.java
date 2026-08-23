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

import com.ayurveda.appointment.dto.request.CreatePrescriptionRequest;
import com.ayurveda.appointment.dto.request.UpdatePrescriptionRequest;
import com.ayurveda.appointment.dto.response.PrescriptionResponse;
import com.ayurveda.appointment.service.PrescriptionService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Prescription", description = "Generate and fetch patient prescriptions")
@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
@Validated
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @Operation(
            summary = "Generate prescription",
            description = """
                    Creates a prescription for a patient (Generate Prescription).
                    Supports multiple medicines, therapy suggestions, and optional next follow-up.
                    """)
    @PostMapping
    public ResponseEntity<ApiResponse<PrescriptionResponse>> createPrescription(
            @Valid @RequestBody CreatePrescriptionRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.createPrescription(request));
    }

    @Operation(
            summary = "Update prescription",
            description = """
                    Updates an existing prescription by id.
                    Replaces medicines and therapy suggestions with the request lists.
                    Patient id cannot be changed.
                    """)
    @PutMapping("/{prescriptionId}")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> updatePrescription(
            @PathVariable UUID prescriptionId,
            @Valid @RequestBody UpdatePrescriptionRequest request) {

        return ResponseEntity.ok(
                prescriptionService.updatePrescription(prescriptionId, request));
    }

    @Operation(
            summary = "Get prescriptions by patient id",
            description = "Returns all non-deleted prescriptions for the patient, newest first.")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getPrescriptionsByPatientId(
            @PathVariable UUID patientId) {

        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientId(patientId));
    }

    @Operation(
            summary = "Get prescription by id",
            description = "Returns one prescription with medicines, therapy suggestions, and next follow-up.")
    @GetMapping("/{prescriptionId}")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescriptionById(
            @PathVariable UUID prescriptionId) {

        return ResponseEntity.ok(prescriptionService.getPrescriptionById(prescriptionId));
    }

}
