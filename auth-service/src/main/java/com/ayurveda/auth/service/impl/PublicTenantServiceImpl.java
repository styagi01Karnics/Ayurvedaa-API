package com.ayurveda.auth.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.auth.constant.AuthMessages;
import com.ayurveda.auth.dto.response.PublicTenantLocationResponse;
import com.ayurveda.auth.enums.TenantStatus;
import com.ayurveda.auth.mapper.AuthMapper;
import com.ayurveda.auth.repository.TenantRepository;
import com.ayurveda.auth.service.PublicTenantService;
import com.ayurveda.common.ApiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicTenantServiceImpl implements PublicTenantService {

    private final TenantRepository tenantRepository;
    private final AuthMapper authMapper;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PublicTenantLocationResponse>> listTenantLocations() {
        List<PublicTenantLocationResponse> locations = tenantRepository
                .findAllByPlatformFalseAndStatusAndDeletedFalseOrderByCreatedAtDesc(TenantStatus.ACTIVE)
                .stream()
                .map(authMapper::toPublicTenantLocationResponse)
                .toList();
        return ApiResponse.success(AuthMessages.PUBLIC_TENANT_LOCATIONS_FETCHED, locations);
    }

}
