package com.ayurveda.auth.controller;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.auth.dto.request.ChangePasswordRequest;
import com.ayurveda.auth.dto.request.ForgotPasswordRequest;
import com.ayurveda.auth.dto.request.LoginRequest;
import com.ayurveda.auth.dto.request.RegisterUserRequest;
import com.ayurveda.auth.dto.request.ResetPasswordRequest;
import com.ayurveda.auth.dto.request.SignUpRequest;
import com.ayurveda.auth.dto.request.UpdateProfileRequest;
import com.ayurveda.auth.dto.request.UpdateUserRequest;
import com.ayurveda.auth.dto.request.UpdateUserStatusRequest;
import com.ayurveda.auth.dto.response.AuthTokenResponse;
import com.ayurveda.auth.dto.response.ForgotPasswordResponse;
import com.ayurveda.auth.dto.response.PagedResponse;
import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.TokenValidationResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.auth.service.AuthService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "Tenant-scoped authentication APIs")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login — Super Admin: Gmail+password; Hospital: tenantId/tenantCode + Gmail+password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Sign up (disabled — hospital admin must create users)")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> signUp(
            @Valid @RequestBody SignUpRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request));
    }

    @Operation(summary = "Forgot password — Super Admin: Gmail only; Hospital: tenant + Gmail")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @Operation(summary = "Reset password using token")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @Operation(summary = "Validate JWT token (for other services)")
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validate(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        return ResponseEntity.ok(authService.validateToken(authorization));
    }

    @Operation(summary = "Register user under current tenant", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/register-user")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody RegisterUserRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(request));
    }

    @Operation(summary = "Get current authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @Operation(summary = "Update own profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(request));
    }

    @Operation(summary = "Change own password", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }

    @Operation(summary = "List users for current tenant", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> users() {
        return ResponseEntity.ok(authService.getUsersForCurrentTenant());
    }

    @Operation(
            summary = "Paginated users for current tenant",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/paged")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> usersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(authService.getUsersForCurrentTenantPaged(page, size));
    }

    @Operation(summary = "Get user by id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(authService.getUserById(userId));
    }

    @Operation(summary = "Update user", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(authService.updateUser(userId, request));
    }

    @Operation(summary = "Update user status", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(authService.updateUserStatus(userId, request));
    }

    @Operation(summary = "Soft-delete user", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(authService.deleteUser(userId));
    }

    @Operation(summary = "Get current tenant", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> currentTenant() {
        return ResponseEntity.ok(authService.getCurrentTenant());
    }

}
