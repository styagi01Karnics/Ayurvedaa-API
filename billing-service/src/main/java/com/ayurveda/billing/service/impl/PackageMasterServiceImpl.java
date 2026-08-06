package com.ayurveda.billing.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.billing.constant.BillingMessages;
import com.ayurveda.billing.dto.request.CreatePackageMasterRequest;
import com.ayurveda.billing.dto.response.PackageMasterResponse;
import com.ayurveda.billing.entity.PackageMaster;
import com.ayurveda.billing.enums.PackageMasterStatus;
import com.ayurveda.billing.mapper.PackageMasterMapper;
import com.ayurveda.billing.repository.PackageMasterRepository;
import com.ayurveda.billing.service.PackageMasterService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PackageMasterServiceImpl implements PackageMasterService {

    private final PackageMasterRepository packageMasterRepository;
    private final PackageMasterMapper packageMasterMapper;

    @Override
    public ApiResponse<PackageMasterResponse> create(CreatePackageMasterRequest request) {
        log.info("Creating package master: {}", request.getName());

        if (packageMasterRepository.existsByNameIgnoreCaseAndDeletedFalse(request.getName())) {
            throw new DuplicateResourceException(
                    BillingMessages.PACKAGE_MASTER_ALREADY_EXISTS_WITH_NAME + request.getName());
        }

        PackageMaster saved = packageMasterRepository.save(packageMasterMapper.toEntity(request));

        log.info("Package master created successfully with id: {}", saved.getId());
        return ApiResponse.success(
                BillingMessages.PACKAGE_MASTER_CREATED, packageMasterMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PackageMasterResponse> getById(UUID packageMasterId) {
        log.info("Fetching package master with id: {}", packageMasterId);

        PackageMaster entity = packageMasterRepository.findByIdAndDeletedFalse(packageMasterId)
                .orElseThrow(() -> new ResourceNotFoundException(BillingMessages.PACKAGE_MASTER_NOT_FOUND));

        return ApiResponse.success(
                BillingMessages.PACKAGE_MASTER_FETCHED, packageMasterMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PackageMasterResponse>> getAll() {
        log.info("Fetching all package masters");

        List<PackageMasterResponse> packages = packageMasterRepository
                .findAllByDeletedFalseOrderByNameAsc()
                .stream()
                .map(packageMasterMapper::toResponse)
                .toList();

        log.info("Fetched {} package masters successfully", packages.size());
        return ApiResponse.success(BillingMessages.PACKAGE_MASTERS_FETCHED, packages);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PackageMasterResponse>> getActive() {
        log.info("Fetching active package masters");

        List<PackageMasterResponse> packages = packageMasterRepository
                .findAllByStatusAndDeletedFalseOrderByNameAsc(PackageMasterStatus.ACTIVE)
                .stream()
                .map(packageMasterMapper::toResponse)
                .toList();

        log.info("Fetched {} active package masters successfully", packages.size());
        return ApiResponse.success(BillingMessages.PACKAGE_MASTERS_FETCHED, packages);
    }

}
