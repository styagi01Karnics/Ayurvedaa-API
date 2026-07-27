package com.ayurveda.therapist.controller;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.therapist.dto.request.CreateTherapistRequest;
import com.ayurveda.therapist.dto.response.TherapistResponse;
import com.ayurveda.therapist.service.TherapistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "Therapist", description = "Therapist master APIs")
@RestController
@RequestMapping("/api/v1/therapists")
@RequiredArgsConstructor
@Validated
public class TherapistController {

    private final TherapistService therapistService;

    @Operation(summary = "Add therapist")
    @PostMapping
    public ResponseEntity<ApiResponse<TherapistResponse>> createTherapist(
            @Valid @RequestBody CreateTherapistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(therapistService.createTherapist(request));
    }

    @Operation(summary = "Get therapist by ID")
    @GetMapping("/{therapistId}")
    public ResponseEntity<ApiResponse<TherapistResponse>> getTherapistById(
            @PathVariable UUID therapistId) {
        return ResponseEntity.ok(therapistService.getTherapistById(therapistId));
    }

    @Operation(summary = "List therapists")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TherapistResponse>>> getAllTherapists() {
        return ResponseEntity.ok(therapistService.getAllTherapists());
    }

    @Operation(summary = "Delete therapist")
    @DeleteMapping("/{therapistId}")
    public ResponseEntity<ApiResponse<Void>> deleteTherapist(@PathVariable UUID therapistId) {
        return ResponseEntity.ok(therapistService.deleteTherapist(therapistId));
    }

}
