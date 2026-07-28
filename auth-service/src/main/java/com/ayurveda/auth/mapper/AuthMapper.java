package com.ayurveda.auth.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.auth.entity.AuthUser;
import com.ayurveda.auth.entity.Tenant;

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
                .status(tenant.getStatus())
                .build();
    }

    public UserResponse toUserResponse(AuthUser user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .tenantId(user.getTenant().getId())
                .tenantCode(user.getTenant().getTenantCode())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

}
