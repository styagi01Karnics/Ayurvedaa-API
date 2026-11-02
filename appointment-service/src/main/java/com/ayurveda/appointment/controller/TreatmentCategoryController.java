package com.ayurveda.appointment.controller;

import com.ayurveda.appointment.dto.request.CreateTreatmentCategoryRequest;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;
import com.ayurveda.appointment.service.TreatmentCategoryService;
import com.ayurveda.common.ApiResponse;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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

    @Operation(summary = "Update treatment category", description = "Updates a treatment category by its ID.")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<TreatmentCategoryResponse>> updateTreatmentCategory(@PathVariable UUID categoryId,
                                                                                          @Valid @RequestBody CreateTreatmentCategoryRequest request) {

        ApiResponse<TreatmentCategoryResponse> response =
                treatmentCategoryService.updateTreatmentCategory(categoryId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete treatment category", description = "Deletes a treatment category by its ID.")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteTreatmentCategory(@PathVariable UUID categoryId) {

        return ResponseEntity.ok(treatmentCategoryService.deleteTreatmentCategory(categoryId));

    }


}
