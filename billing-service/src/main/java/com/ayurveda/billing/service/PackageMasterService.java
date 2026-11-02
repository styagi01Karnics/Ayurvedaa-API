package com.ayurveda.billing.service;

import com.ayurveda.billing.dto.request.CreatePackageMasterRequest;
import com.ayurveda.billing.dto.response.PackageMasterResponse;
import com.ayurveda.common.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface PackageMasterService {

    /** Creates a new package master record. */
    ApiResponse<PackageMasterResponse> create(CreatePackageMasterRequest request);

    /** Fetches a package master by ID. */
    ApiResponse<PackageMasterResponse> getById(UUID packageMasterId);

    /** Lists all non-deleted package masters. */
    ApiResponse<List<PackageMasterResponse>> getAll();

    /** Lists only active, non-deleted package masters. */
    ApiResponse<List<PackageMasterResponse>> getActive();

    /**
     * Updates a package master record by id.
     */
    ApiResponse<PackageMasterResponse> updatePackageMaster(UUID packageMasterId, CreatePackageMasterRequest request);

    /**
     * Deactivates a package master record by id.
     */
    ApiResponse<Void> deletePackageMaster(UUID packageMasterId);

}
