package com.ayurveda.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.service.TherapyService;

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

    @PostMapping
    public ResponseEntity<ApiResponse<TherapyResponse>> createTherapy(
            @Valid @RequestBody CreateTherapyRequest request) {

        ApiResponse<TherapyResponse> response =
                therapyService.createTherapy(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{therapyId}")
    public ResponseEntity<ApiResponse<TherapyResponse>> getTherapyById(
            @PathVariable UUID therapyId) {

        ApiResponse<TherapyResponse> response =
                therapyService.getTherapyById(therapyId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TherapyResponse>>> getAllTherapies() {

        ApiResponse<List<TherapyResponse>> response =
                therapyService.getAllTherapies();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<TherapyResponse>>> getTherapiesByCategory(
            @PathVariable UUID categoryId) {

        ApiResponse<List<TherapyResponse>> response =
                therapyService.getTherapiesByCategory(categoryId);

        return ResponseEntity.ok(response);
    }

}