package com.ayurveda.auth.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.auth.dto.request.ChangePasswordRequest;
import com.ayurveda.auth.dto.request.ForgotPasswordRequest;
import com.ayurveda.auth.dto.request.LoginRequest;
import com.ayurveda.auth.dto.request.RegisterUserRequest;
import com.ayurveda.auth.dto.request.ResetPasswordRequest;
import com.ayurveda.auth.dto.request.UpdateProfileRequest;
import com.ayurveda.auth.dto.request.UpdateUserRequest;
import com.ayurveda.auth.dto.request.UpdateUserStatusRequest;
import com.ayurveda.auth.dto.response.AuthTokenResponse;
import com.ayurveda.auth.dto.response.ForgotPasswordResponse;
import com.ayurveda.auth.dto.response.PagedResponse;
import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.TokenValidationResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.common.ApiResponse;

public interface AuthService {

    ApiResponse<AuthTokenResponse> login(LoginRequest request);

    ApiResponse<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request);

    ApiResponse<Void> resetPassword(ResetPasswordRequest request);

    ApiResponse<UserResponse> registerUser(RegisterUserRequest request);

    ApiResponse<UserResponse> updateUser(UUID userId, UpdateUserRequest request);

    ApiResponse<UserResponse> updateUserStatus(UUID userId, UpdateUserStatusRequest request);

    ApiResponse<Void> deleteUser(UUID userId);

    ApiResponse<UserResponse> getUserById(UUID userId);

    ApiResponse<UserResponse> getCurrentUser();

    ApiResponse<UserResponse> updateProfile(UpdateProfileRequest request);

    ApiResponse<Void> changePassword(ChangePasswordRequest request);

    ApiResponse<List<UserResponse>> getUsersForCurrentTenant();

    ApiResponse<PagedResponse<UserResponse>> getUsersForCurrentTenantPaged(int page, int size);

    ApiResponse<TenantResponse> getCurrentTenant();

    ApiResponse<TokenValidationResponse> validateToken(String authorizationHeader);

}
