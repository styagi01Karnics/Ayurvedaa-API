package com.ayurveda.auth.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.auth.dto.request.BootstrapSuperAdminRequest;
import com.ayurveda.auth.dto.request.CreateHospitalAdminRequest;
import com.ayurveda.auth.dto.request.OnboardHospitalRequest;
import com.ayurveda.auth.dto.request.UpdateHospitalStatusRequest;
import com.ayurveda.auth.dto.response.HospitalOnboardResponse;
import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.common.ApiResponse;

public interface PlatformService {

    ApiResponse<UserResponse> bootstrapSuperAdmin(BootstrapSuperAdminRequest request);

    /** Clinic + first hospital admin (Figma signup form). */
    ApiResponse<HospitalOnboardResponse> onboardHospital(OnboardHospitalRequest request);

    ApiResponse<List<TenantResponse>> listHospitals();

    ApiResponse<TenantResponse> getHospital(UUID hospitalId);

    ApiResponse<UserResponse> createHospitalAdmin(UUID hospitalId, CreateHospitalAdminRequest request);

    ApiResponse<List<UserResponse>> listHospitalAdmins(UUID hospitalId);

    ApiResponse<TenantResponse> updateHospitalStatus(UUID hospitalId, UpdateHospitalStatusRequest request);

    ApiResponse<TenantResponse> retryHospitalProvision(UUID hospitalId);

}
