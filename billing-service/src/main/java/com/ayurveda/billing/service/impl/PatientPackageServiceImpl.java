package com.ayurveda.billing.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.billing.constant.BillingMessages;
import com.ayurveda.billing.dto.request.CreatePatientPackageRequest;
import com.ayurveda.billing.dto.request.UpdatePatientPackageRequest;
import com.ayurveda.billing.dto.request.UpdatePatientPackageStatusRequest;
import com.ayurveda.billing.dto.response.PatientPackageResponse;
import com.ayurveda.billing.entity.PackageMaster;
import com.ayurveda.billing.entity.PatientPackage;
import com.ayurveda.billing.enums.PackageStatus;
import com.ayurveda.billing.repository.PackageMasterRepository;
import com.ayurveda.billing.repository.PatientPackageRepository;
import com.ayurveda.billing.service.PatientPackageService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatientPackageServiceImpl implements PatientPackageService {

    private final PatientPackageRepository patientPackageRepository;
    private final PackageMasterRepository packageMasterRepository;

    @Override
    public ApiResponse<PatientPackageResponse> createPackage(CreatePatientPackageRequest request) {
        log.info("Creating patient package for patient: {}", request.getPatientId());

        PackageMaster packageMaster = fetchPackageMaster(request.getPackageMasterId());

        PackageStatus status = request.getStatus() != null
                ? request.getStatus()
                : PackageStatus.SCHEDULED;

        PatientPackage patientPackage = PatientPackage.builder()
                .patientId(request.getPatientId())
                .packageMasterId(packageMaster.getId())
                .validity(request.getValidity())
                .status(status)
                .discountApplied(request.getDiscountApplied())
                .build();

        PatientPackage saved = patientPackageRepository.save(patientPackage);
        log.info("Patient package created successfully. Package ID: {}", saved.getId());

        return ApiResponse.success(
                BillingMessages.PATIENT_PACKAGE_CREATED, toResponse(saved, packageMaster));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PatientPackageResponse>> getAllPackages() {
        log.info("Fetching all patient packages");

        List<PatientPackageResponse> responses = patientPackageRepository
                .findAllByDeletedFalseOrderByValidityDesc()
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("Fetched {} patient packages successfully", responses.size());
        return ApiResponse.success(BillingMessages.PATIENT_PACKAGES_FETCHED, responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PatientPackageResponse>> getPackagesByPatientId(UUID patientId) {
        log.info("Fetching patient packages for patient: {}", patientId);

        List<PatientPackageResponse> responses = patientPackageRepository
                .findAllByPatientIdAndDeletedFalseOrderByValidityDesc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("Fetched {} patient packages for patient: {}", responses.size(), patientId);
        return ApiResponse.success(BillingMessages.PATIENT_PACKAGES_FETCHED, responses);
    }

    @Override
    public ApiResponse<PatientPackageResponse> updatePackage(
            UUID packageId, UpdatePatientPackageRequest request) {

        log.info("Updating patient package: {}", packageId);

        PatientPackage patientPackage = findActivePackage(packageId);
        PackageMaster packageMaster = fetchPackageMaster(request.getPackageMasterId());

        patientPackage.setPackageMasterId(packageMaster.getId());
        patientPackage.setValidity(request.getValidity());
        patientPackage.setDiscountApplied(request.getDiscountApplied());

        PatientPackage saved = patientPackageRepository.save(patientPackage);
        log.info("Patient package updated successfully. Package ID: {}", packageId);

        return ApiResponse.success(
                BillingMessages.PATIENT_PACKAGE_UPDATED, toResponse(saved, packageMaster));
    }

    @Override
    public ApiResponse<PatientPackageResponse> updatePackageStatus(
            UUID packageId, UpdatePatientPackageStatusRequest request) {

        log.info("Updating patient package status. Package ID: {}, Status: {}",
                packageId, request.getStatus());

        PatientPackage patientPackage = findActivePackage(packageId);
        patientPackage.setStatus(request.getStatus());
        PatientPackage saved = patientPackageRepository.save(patientPackage);

        log.info("Patient package status updated successfully. Package ID: {}", packageId);
        return ApiResponse.success(BillingMessages.PATIENT_PACKAGE_STATUS_UPDATED, toResponse(saved));
    }

    private PatientPackage findActivePackage(UUID packageId) {
        return patientPackageRepository.findByIdAndDeletedFalse(packageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BillingMessages.PATIENT_PACKAGE_NOT_FOUND_WITH_ID + packageId));
    }

    private PackageMaster fetchPackageMaster(UUID packageMasterId) {
        return packageMasterRepository.findByIdAndDeletedFalse(packageMasterId)
                .orElseThrow(() -> new ResourceNotFoundException(BillingMessages.PACKAGE_MASTER_NOT_FOUND));
    }

    private PatientPackageResponse toResponse(PatientPackage patientPackage) {
        PackageMaster packageMaster = null;
        try {
            packageMaster = fetchPackageMaster(patientPackage.getPackageMasterId());
        } catch (Exception ex) {
            log.warn("Package master not found for patient package {}: {}",
                    patientPackage.getId(), patientPackage.getPackageMasterId());
        }
        return toResponse(patientPackage, packageMaster);
    }

    private PatientPackageResponse toResponse(PatientPackage patientPackage, PackageMaster packageMaster) {
        return PatientPackageResponse.builder()
                .id(patientPackage.getId())
                .patientId(patientPackage.getPatientId())
                .packageMasterId(patientPackage.getPackageMasterId())
                .packageName(packageMaster != null ? packageMaster.getName() : null)
                .packagePrice(packageMaster != null ? packageMaster.getPackagePrice() : null)
                .validity(patientPackage.getValidity())
                .status(patientPackage.getStatus())
                .discountApplied(patientPackage.getDiscountApplied())
                .build();
    }

}
