package com.ayurveda.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.auth.dto.request.ForgotPasswordRequest;
import com.ayurveda.auth.dto.request.LoginRequest;
import com.ayurveda.auth.dto.request.RegisterUserRequest;
import com.ayurveda.auth.dto.request.ResetPasswordRequest;
import com.ayurveda.auth.dto.request.SignUpRequest;
import com.ayurveda.auth.dto.response.AuthTokenResponse;
import com.ayurveda.auth.dto.response.ForgotPasswordResponse;
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

    @Operation(summary = "Login with username/email and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Sign up under an existing tenant (returns JWT)")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> signUp(
            @Valid @RequestBody SignUpRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request));
    }

    @Operation(summary = "Forgot password - generate reset token")
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

    @Operation(summary = "List users for current tenant", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> users() {
        return ResponseEntity.ok(authService.getUsersForCurrentTenant());
    }

    @Operation(summary = "Get current tenant", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> currentTenant() {
        return ResponseEntity.ok(authService.getCurrentTenant());
    }

}
