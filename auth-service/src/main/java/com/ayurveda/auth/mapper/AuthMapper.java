package com.ayurveda.auth.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.TenantRoleResponse;
import com.ayurveda.auth.dto.response.UiPageResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.auth.entity.AuthUser;
import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.entity.TenantRole;
import com.ayurveda.auth.entity.UiPage;

@Component
public class AuthMapper {

    public TenantResponse toTenantResponse(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        return TenantResponse.builder()
                .id(tenant.getId())
                .tenantCode(tenant.getTenantCode())
                .name(tenant.getName())
                .email(tenant.getEmail())
                .phone(tenant.getPhone())
                .address(tenant.getAddress())
                .schemaName(tenant.getSchemaName())
                .platform(tenant.getPlatform())
                .status(tenant.getStatus())
                .provisionMessage(tenant.getProvisionMessage())
                .build();
    }

    public UserResponse toUserResponse(AuthUser user, List<String> pageCodes) {
        if (user == null) {
            return null;
        }
        TenantRole tenantRole = user.getTenantRole();
        return UserResponse.builder()
                .id(user.getId())
                .tenantId(user.getTenant().getId())
                .tenantCode(user.getTenant().getTenantCode())
                .schemaName(user.getTenant().getSchemaName())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .tenantRoleId(tenantRole != null ? tenantRole.getId() : null)
                .tenantRoleCode(tenantRole != null ? tenantRole.getRoleCode() : null)
                .tenantRoleName(tenantRole != null ? tenantRole.getRoleName() : null)
                .pageCodes(pageCodes != null ? pageCodes : List.of())
                .status(user.getStatus())
                .build();
    }

    public UserResponse toUserResponse(AuthUser user) {
        return toUserResponse(user, List.of());
    }

    public UiPageResponse toUiPageResponse(UiPage page) {
        return UiPageResponse.builder()
                .id(page.getId())
                .pageCode(page.getPageCode())
                .pageName(page.getPageName())
                .description(page.getDescription())
                .module(page.getModule())
                .sortOrder(page.getSortOrder())
                .build();
    }

    public TenantRoleResponse toTenantRoleResponse(
            TenantRole role, List<String> pageCodes, Long userCount) {
        return TenantRoleResponse.builder()
                .id(role.getId())
                .tenantId(role.getTenant().getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .systemRole(role.getSystemRole())
                .active(role.getActive())
                .pageCodes(pageCodes != null ? pageCodes : List.of())
                .userCount(userCount != null ? userCount : 0L)
                .build();
    }

    public TenantRoleResponse toTenantRoleResponse(TenantRole role, List<String> pageCodes) {
        return toTenantRoleResponse(role, pageCodes, 0L);
    }

}
