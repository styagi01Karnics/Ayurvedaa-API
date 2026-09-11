package com.ayurveda.auth.service;

import java.util.List;

import com.ayurveda.auth.dto.response.PublicTenantLocationResponse;
import com.ayurveda.common.ApiResponse;

public interface PublicTenantService {

    /** Active hospital tenants only (no auth). Minimal location fields. */
    ApiResponse<List<PublicTenantLocationResponse>> listTenantLocations();

}
