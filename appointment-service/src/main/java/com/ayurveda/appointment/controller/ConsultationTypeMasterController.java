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

import com.ayurveda.appointment.dto.request.CreateConsultationTypeRequest;
import com.ayurveda.appointment.dto.response.ConsultationTypeResponse;
import com.ayurveda.appointment.service.ConsultationTypeMasterService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Consultation Type Master", description = "Consultation type master data APIs")
@RestController
@RequestMapping("/api/v1/consultation-types")
@RequiredArgsConstructor
@Validated
public class ConsultationTypeMasterController {

    private final ConsultationTypeMasterService consultationTypeMasterService;

    @Operation(summary = "Create consultation type", description = "Creates a new consultation type master record.")
    @PostMapping
    public ResponseEntity<ApiResponse<ConsultationTypeResponse>> create(
            @Valid @RequestBody CreateConsultationTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultationTypeMasterService.create(request));
    }

    @Operation(summary = "List all consultation types", description = "Returns all non-deleted consultation types (ACTIVE and INACTIVE).")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ConsultationTypeResponse>>> getAll() {
        return ResponseEntity.ok(consultationTypeMasterService.getAll());
    }

    @Operation(summary = "List active consultation types", description = "Returns only ACTIVE non-deleted consultation types.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ConsultationTypeResponse>>> getActive() {
        return ResponseEntity.ok(consultationTypeMasterService.getActive());
    }

    @Operation(summary = "Get consultation type by ID", description = "Returns a non-deleted consultation type by ID.")
    @GetMapping("/{consultationTypeId}")
    public ResponseEntity<ApiResponse<ConsultationTypeResponse>> getById(
            @PathVariable UUID consultationTypeId) {
        return ResponseEntity.ok(consultationTypeMasterService.getById(consultationTypeId));
    }

}
