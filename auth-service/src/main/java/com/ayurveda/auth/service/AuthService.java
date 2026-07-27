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

    ApiResponse<TenantResponse> registerTenant(RegisterTenantRequest request);

    ApiResponse<AuthTokenResponse> login(LoginRequest request);

    ApiResponse<AuthTokenResponse> signUp(SignUpRequest request);

    ApiResponse<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request);

    ApiResponse<Void> resetPassword(ResetPasswordRequest request);

    ApiResponse<UserResponse> registerUser(RegisterUserRequest request);

    ApiResponse<UserResponse> getCurrentUser();

    ApiResponse<List<UserResponse>> getUsersForCurrentTenant();

    ApiResponse<TenantResponse> getCurrentTenant();

    ApiResponse<TokenValidationResponse> validateToken(String authorizationHeader);

}
