package com.ayurveda.auth.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.auth.dto.request.CreateTenantRoleRequest;
import com.ayurveda.auth.dto.request.UpdateTenantRoleRequest;
import com.ayurveda.auth.dto.response.TenantRoleResponse;
import com.ayurveda.auth.dto.response.UiPageResponse;
import com.ayurveda.auth.service.RoleService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Roles", description = "Hospital admin Role Management (create roles + UI page access)")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN') or hasAuthority('PAGE_SETTINGS')")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "List UI pages available for role assignment")
    @GetMapping("/ui-pages")
    public ResponseEntity<ApiResponse<List<UiPageResponse>>> listUiPages() {
        return ResponseEntity.ok(roleService.listUiPages());
    }

    @Operation(summary = "Create custom hospital role with UI page access")
    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<TenantRoleResponse>> createRole(
            @Valid @RequestBody CreateTenantRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(request));
    }

    @Operation(summary = "List roles for current hospital")
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<TenantRoleResponse>>> listRoles() {
        return ResponseEntity.ok(roleService.listRoles());
    }

    @Operation(summary = "Get role by id")
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<ApiResponse<TenantRoleResponse>> getRole(@PathVariable UUID roleId) {
        return ResponseEntity.ok(roleService.getRole(roleId));
    }

    @Operation(summary = "Update role name/description and UI pages")
    @PutMapping("/roles/{roleId}")
    public ResponseEntity<ApiResponse<TenantRoleResponse>> updateRole(
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateTenantRoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(roleId, request));
    }

    @Operation(summary = "Soft-delete a custom role")
    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable UUID roleId) {
        return ResponseEntity.ok(roleService.deleteRole(roleId));
    }

}
