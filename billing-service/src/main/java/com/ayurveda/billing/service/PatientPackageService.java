package com.ayurveda.billing.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.dto.request.CreatePatientPackageRequest;
import com.ayurveda.billing.dto.request.UpdatePatientPackageRequest;
import com.ayurveda.billing.dto.request.UpdatePatientPackageStatusRequest;
import com.ayurveda.billing.dto.response.PatientPackageResponse;
import com.ayurveda.common.ApiResponse;

public interface PatientPackageService {

    /** Creates a package assignment for a patient. */
    ApiResponse<PatientPackageResponse> createPackage(CreatePatientPackageRequest request);

    /** Returns all non-deleted patient packages. */
    ApiResponse<List<PatientPackageResponse>> getAllPackages();

    /** Returns non-deleted packages for a patient. */
    ApiResponse<List<PatientPackageResponse>> getPackagesByPatientId(UUID patientId);

    /** Updates package details (not status). */
    ApiResponse<PatientPackageResponse> updatePackage(
            UUID packageId, UpdatePatientPackageRequest request);

    /** Changes package status by id (SCHEDULED, ONGOING, COMPLETED). */
    ApiResponse<PatientPackageResponse> updatePackageStatus(
            UUID packageId, UpdatePatientPackageStatusRequest request);

}
