package com.ayurveda.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayurveda.auth.constant.AuthMessages;
import com.ayurveda.auth.dto.response.PublicTenantLocationResponse;
import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.enums.TenantStatus;
import com.ayurveda.auth.mapper.AuthMapper;
import com.ayurveda.auth.repository.TenantRepository;
import com.ayurveda.common.ApiResponse;

@ExtendWith(MockitoExtension.class)
class PublicTenantServiceImplTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private PublicTenantServiceImpl publicTenantService;

    @Test
    void listTenantLocationsReturnsOnlyMappedFields() {
        Tenant hospital = Tenant.builder()
                .tenantCode("GAN-DL")
                .name("Ganesha Ayurveda")
                .state("Delhi")
                .city("New Delhi")
                .platform(false)
                .status(TenantStatus.ACTIVE)
                .build();
        PublicTenantLocationResponse mapped = PublicTenantLocationResponse.builder()
                .tenantCode("GAN-DL")
                .state("Delhi")
                .city("New Delhi")
                .build();

        when(tenantRepository.findAllByPlatformFalseAndStatusAndDeletedFalseOrderByCreatedAtDesc(
                        TenantStatus.ACTIVE))
                .thenReturn(List.of(hospital));
        when(authMapper.toPublicTenantLocationResponse(hospital)).thenReturn(mapped);

        ApiResponse<List<PublicTenantLocationResponse>> response =
                publicTenantService.listTenantLocations();

        assertTrue(response.isSuccess());
        assertEquals(AuthMessages.PUBLIC_TENANT_LOCATIONS_FETCHED, response.getMessage());
        assertEquals(1, response.getData().size());
        assertEquals("GAN-DL", response.getData().get(0).getTenantCode());
        assertEquals("Delhi", response.getData().get(0).getState());
        assertEquals("New Delhi", response.getData().get(0).getCity());
    }
}
