package com.ayurveda.billing.controller;

import com.ayurveda.billing.dto.request.CreatePackageMasterRequest;
import com.ayurveda.billing.dto.response.PackageMasterResponse;
import com.ayurveda.billing.service.PackageMasterService;
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

@Tag(name = "Package Master", description = "Package name and price master data APIs")
@RestController
@RequestMapping("/api/v1/package-masters")
@RequiredArgsConstructor
@Validated
public class PackageMasterController {

    private final PackageMasterService packageMasterService;

    @Operation(summary = "Create package master", description = "Creates a package with name and price.")
    @PostMapping
    public ResponseEntity<ApiResponse<PackageMasterResponse>> create(
            @Valid @RequestBody CreatePackageMasterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(packageMasterService.create(request));
    }

    @Operation(
            summary = "List all package masters",
            description = "Returns all non-deleted packages (ACTIVE and INACTIVE).")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PackageMasterResponse>>> getAll() {
        return ResponseEntity.ok(packageMasterService.getAll());
    }

    @Operation(
            summary = "List active package masters",
            description = "Returns only ACTIVE non-deleted packages.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PackageMasterResponse>>> getActive() {
        return ResponseEntity.ok(packageMasterService.getActive());
    }

    @Operation(summary = "Get package master by ID")
    @GetMapping("/{packageMasterId}")
    public ResponseEntity<ApiResponse<PackageMasterResponse>> getById(
            @PathVariable UUID packageMasterId) {
        return ResponseEntity.ok(packageMasterService.getById(packageMasterId));
    }

    @Operation(summary = "Update package master", description = "Updates a package master by its ID.")
    @PutMapping("/{packageMasterId}")
    public ResponseEntity<ApiResponse<PackageMasterResponse>> updatePackageMaster(@PathVariable UUID packageMasterId,
                                                                                  @Valid @RequestBody CreatePackageMasterRequest request) {

        ApiResponse<PackageMasterResponse> response =
                packageMasterService.updatePackageMaster(packageMasterId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete package master", description = "Deletes a package master by its ID.")
    @DeleteMapping("/{packageMasterId}")
    public ResponseEntity<ApiResponse<Void>> deletePackageMaster(@PathVariable UUID packageMasterId) {

        return ResponseEntity.ok(packageMasterService.deletePackageMaster(packageMasterId));

    }

}
