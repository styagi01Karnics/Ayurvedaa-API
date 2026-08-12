package com.ayurveda.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateTreatmentCategoryRequest;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;
import com.ayurveda.appointment.service.TreatmentCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Treatment Category", description = "Treatment category master APIs")
@RestController
@RequestMapping("/api/v1/treatment-categories")
@RequiredArgsConstructor
@Validated
public class TreatmentCategoryController {

    private final TreatmentCategoryService treatmentCategoryService;

    @Operation(summary = "Create treatment category", description = "Creates a new treatment category with a generated category code.")
    @PostMapping
    public ResponseEntity<ApiResponse<TreatmentCategoryResponse>> createTreatmentCategory(
            @Valid @RequestBody CreateTreatmentCategoryRequest request) {

        ApiResponse<TreatmentCategoryResponse> response =
                treatmentCategoryService.createTreatmentCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get treatment category by ID", description = "Returns a treatment category by its ID.")
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<TreatmentCategoryResponse>> getTreatmentCategoryById(
            @PathVariable UUID categoryId) {

        ApiResponse<TreatmentCategoryResponse> response =
                treatmentCategoryService.getTreatmentCategoryById(categoryId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all treatment categories", description = "Returns all treatment categories.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TreatmentCategoryResponse>>> getAllTreatmentCategories() {

        ApiResponse<List<TreatmentCategoryResponse>> response =
                treatmentCategoryService.getAllTreatmentCategories();

        return ResponseEntity.ok(response);
    }

}
