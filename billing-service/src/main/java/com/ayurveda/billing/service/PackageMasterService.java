package com.ayurveda.billing.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.dto.request.CreatePackageMasterRequest;
import com.ayurveda.billing.dto.response.PackageMasterResponse;
import com.ayurveda.common.ApiResponse;

public interface PackageMasterService {

    /** Creates a new package master record. */
    ApiResponse<PackageMasterResponse> create(CreatePackageMasterRequest request);

    /** Fetches a package master by ID. */
    ApiResponse<PackageMasterResponse> getById(UUID packageMasterId);

    /** Lists all non-deleted package masters. */
    ApiResponse<List<PackageMasterResponse>> getAll();

    /** Lists only active, non-deleted package masters. */
    ApiResponse<List<PackageMasterResponse>> getActive();

}
