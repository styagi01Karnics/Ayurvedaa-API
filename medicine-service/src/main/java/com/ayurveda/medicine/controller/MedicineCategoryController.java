package com.ayurveda.medicine.controller;

import java.util.List;
import java.util.UUID;

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

import com.ayurveda.common.ApiResponse;
import com.ayurveda.medicine.dto.request.CreateMedicineCategoryRequest;
import com.ayurveda.medicine.dto.request.UpdateMedicineCategoryRequest;
import com.ayurveda.medicine.dto.response.MedicineCategoryResponse;
import com.ayurveda.medicine.service.MedicineCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Medicine Categories", description = "Medicine category master APIs")
@RestController
@RequestMapping("/api/v1/medicines/categories")
@RequiredArgsConstructor
@Validated
public class MedicineCategoryController {

    private final MedicineCategoryService medicineCategoryService;

    @Operation(summary = "List medicine categories (master)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicineCategoryResponse>>> getCategories() {
        return ResponseEntity.ok(medicineCategoryService.getCategories());
    }

    @Operation(summary = "Create medicine category")
    @PostMapping
    public ResponseEntity<ApiResponse<MedicineCategoryResponse>> createCategory(
            @Valid @RequestBody CreateMedicineCategoryRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicineCategoryService.createCategory(request));
    }

    @Operation(summary = "Update medicine category name or active flag")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<MedicineCategoryResponse>> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateMedicineCategoryRequest request) {

        return ResponseEntity.ok(medicineCategoryService.updateCategory(categoryId, request));
    }

    @Operation(summary = "Delete medicine category (blocked while medicines use it)")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(medicineCategoryService.deleteCategory(categoryId));
    }

}
