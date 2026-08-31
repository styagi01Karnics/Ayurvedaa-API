package com.ayurveda.auth.controller;

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

import com.ayurveda.auth.dto.request.BootstrapSuperAdminRequest;
import com.ayurveda.auth.dto.request.CreateHospitalAdminRequest;
import com.ayurveda.auth.dto.request.OnboardHospitalRequest;
import com.ayurveda.auth.dto.request.UpdateHospitalStatusRequest;
import com.ayurveda.auth.dto.response.HospitalOnboardResponse;
import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.auth.service.PlatformService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Platform", description = "Super Admin hospital onboarding and management")
@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
@Validated
public class PlatformController {

    private final PlatformService platformService;

    @Operation(summary = "Bootstrap first platform SUPER_ADMIN (one-time, public until created)")
    @PostMapping("/bootstrap-super-admin")
    public ResponseEntity<ApiResponse<UserResponse>> bootstrapSuperAdmin(
            @Valid @RequestBody BootstrapSuperAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(platformService.bootstrapSuperAdmin(request));
    }

    @Operation(
            summary = "Onboard hospital + first admin (Figma Clinic + Contact signup)",
            description = """
                    Clinic Information + Contact Information (first hospital admin).
                    tenantCode is auto-generated BRAND-STATE (e.g. GAN-DL) from clinicName + state.
                    Schema becomes hosp_gan_dl. State can be name or code (Delhi/DL, Odisha/OD).
                    email must be Gmail (single login identity; UI "User ID" maps to email).
                    No tenantId in body. All form fields (except password) save to tenants;
                    auth_users gets fullName, mobileNumber, email (username mirrored = email),
                    password→passwordHash.
                    logoUrl / photoUrl are optional URLs after file-upload-service (stored on tenant).
                    """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/hospitals")
    public ResponseEntity<ApiResponse<HospitalOnboardResponse>> onboardHospital(
            @Valid @RequestBody OnboardHospitalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(platformService.onboardHospital(request));
    }

    @Operation(summary = "List all hospitals", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/hospitals")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> listHospitals() {
        return ResponseEntity.ok(platformService.listHospitals());
    }

    @Operation(summary = "Get hospital by id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/hospitals/{hospitalId}")
    public ResponseEntity<ApiResponse<TenantResponse>> getHospital(@PathVariable UUID hospitalId) {
        return ResponseEntity.ok(platformService.getHospital(hospitalId));
    }

    @Operation(
            summary = "Create hospital admin for a hospital",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/hospitals/{hospitalId}/admins")
    public ResponseEntity<ApiResponse<UserResponse>> createHospitalAdmin(
            @PathVariable UUID hospitalId,
            @Valid @RequestBody CreateHospitalAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(platformService.createHospitalAdmin(hospitalId, request));
    }

    @Operation(
            summary = "List hospital admins for a hospital",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/hospitals/{hospitalId}/admins")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listHospitalAdmins(
            @PathVariable UUID hospitalId) {
        return ResponseEntity.ok(platformService.listHospitalAdmins(hospitalId));
    }

    @Operation(
            summary = "Update hospital status (ACTIVE / INACTIVE / SUSPENDED)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/hospitals/{hospitalId}/status")
    public ResponseEntity<ApiResponse<TenantResponse>> updateHospitalStatus(
            @PathVariable UUID hospitalId,
            @Valid @RequestBody UpdateHospitalStatusRequest request) {
        return ResponseEntity.ok(platformService.updateHospitalStatus(hospitalId, request));
    }

    @Operation(
            summary = "Retry schema provision for a FAILED hospital",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/hospitals/{hospitalId}/retry-provision")
    public ResponseEntity<ApiResponse<TenantResponse>> retryHospitalProvision(
            @PathVariable UUID hospitalId) {
        return ResponseEntity.ok(platformService.retryHospitalProvision(hospitalId));
    }

}
