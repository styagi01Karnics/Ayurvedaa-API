package com.ayurveda.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.auth.dto.response.PublicTenantLocationResponse;
import com.ayurveda.auth.service.PublicTenantService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Public", description = "Unauthenticated public endpoints")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
@SecurityRequirements
public class PublicTenantController {

    private final PublicTenantService publicTenantService;

    @Operation(summary = "List active hospital tenant codes with state and city (no auth)")
    @GetMapping("/tenants")
    public ResponseEntity<ApiResponse<List<PublicTenantLocationResponse>>> listTenantLocations() {
        return ResponseEntity.ok(publicTenantService.listTenantLocations());
    }

}
