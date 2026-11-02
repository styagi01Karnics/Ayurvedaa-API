package com.ayurveda.appointment.controller;

import com.ayurveda.appointment.dto.request.CreateDoshaRequest;
import com.ayurveda.appointment.dto.response.DoshaResponse;
import com.ayurveda.appointment.service.DoshaMasterService;
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

@Tag(name = "Dosha Master", description = "Dosha master data APIs")
@RestController
@RequestMapping("/api/v1/doshas")
@RequiredArgsConstructor
@Validated
public class DoshaMasterController {

    private final DoshaMasterService doshaMasterService;

    @Operation(summary = "Create dosha", description = "Creates a new dosha master record.")
    @PostMapping
    public ResponseEntity<ApiResponse<DoshaResponse>> createDosha(
            @Valid @RequestBody CreateDoshaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doshaMasterService.createDosha(request));
    }

    @Operation(summary = "Get dosha by ID", description = "Returns a non-deleted dosha by its ID.")
    @GetMapping("/{doshaId}")
    public ResponseEntity<ApiResponse<DoshaResponse>> getDoshaById(@PathVariable UUID doshaId) {
        return ResponseEntity.ok(doshaMasterService.getDoshaById(doshaId));
    }

    @Operation(summary = "List all doshas", description = "Returns all non-deleted doshas ordered by name.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DoshaResponse>>> getAllDoshas() {
        return ResponseEntity.ok(doshaMasterService.getAllDoshas());
    }

    @Operation(summary = "Update dosha master", description = "Updates a dosha master by its ID.")
    @PutMapping("/{doshaId}")
    public ResponseEntity<ApiResponse<DoshaResponse>> updateDoshaMaster(@PathVariable UUID doshaId,
                                                                        @Valid @RequestBody CreateDoshaRequest request) {

        ApiResponse<DoshaResponse> response =
                doshaMasterService.updateDoshaMaster(doshaId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete dosha master", description = "Deletes a dosha master by its ID.")
    @DeleteMapping("/{doshaId}")
    public ResponseEntity<ApiResponse<Void>> deleteDoshaMaster(@PathVariable UUID doshaId) {

        return ResponseEntity.ok(doshaMasterService.deleteDoshaMaster(doshaId));

    }

}
