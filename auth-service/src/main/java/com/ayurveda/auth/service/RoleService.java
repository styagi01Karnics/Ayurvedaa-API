package com.ayurveda.auth.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.auth.dto.request.CreateTenantRoleRequest;
import com.ayurveda.auth.dto.request.UpdateTenantRoleRequest;
import com.ayurveda.auth.dto.response.TenantRoleResponse;
import com.ayurveda.auth.dto.response.UiPageResponse;
import com.ayurveda.common.ApiResponse;

public interface RoleService {

    ApiResponse<List<UiPageResponse>> listUiPages();

    ApiResponse<TenantRoleResponse> createRole(CreateTenantRoleRequest request);

    ApiResponse<List<TenantRoleResponse>> listRoles();

    ApiResponse<TenantRoleResponse> getRole(UUID roleId);

    ApiResponse<TenantRoleResponse> updateRole(UUID roleId, UpdateTenantRoleRequest request);

    ApiResponse<Void> deleteRole(UUID roleId);

}
