package com.ayurveda.auth.service;

import java.util.List;

import com.ayurveda.auth.dto.request.ForgotPasswordRequest;
import com.ayurveda.auth.dto.request.LoginRequest;
import com.ayurveda.auth.dto.request.RegisterTenantRequest;
import com.ayurveda.auth.dto.request.RegisterUserRequest;
import com.ayurveda.auth.dto.request.ResetPasswordRequest;
import com.ayurveda.auth.dto.request.SignUpRequest;
import com.ayurveda.auth.dto.response.AuthTokenResponse;
import com.ayurveda.auth.dto.response.ForgotPasswordResponse;
import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.TokenValidationResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.common.ApiResponse;

public interface AuthService {

    /** Registers a new tenant and its admin user. */
    ApiResponse<TenantResponse> registerTenant(RegisterTenantRequest request);

    /** Authenticates a user and returns a JWT access token. */
    ApiResponse<AuthTokenResponse> login(LoginRequest request);

    /** Self-registers a receptionist under an existing tenant and returns a JWT. */
    ApiResponse<AuthTokenResponse> signUp(SignUpRequest request);

    /** Issues a password-reset token when the account exists. */
    ApiResponse<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request);

    /** Resets the user's password using a valid reset token. */
    ApiResponse<Void> resetPassword(ResetPasswordRequest request);

    /** Registers a user under the current tenant (admin only). */
    ApiResponse<UserResponse> registerUser(RegisterUserRequest request);

    /** Returns the currently authenticated user. */
    ApiResponse<UserResponse> getCurrentUser();

    /** Lists all users for the current tenant (admin only). */
    ApiResponse<List<UserResponse>> getUsersForCurrentTenant();

    /** Returns the tenant of the currently authenticated user. */
    ApiResponse<TenantResponse> getCurrentTenant();

    /** Validates a Bearer JWT and returns principal claims when valid. */
    ApiResponse<TokenValidationResponse> validateToken(String authorizationHeader);

}
