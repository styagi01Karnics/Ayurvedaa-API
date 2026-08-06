package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateTreatmentPlanMasterRequest;
import com.ayurveda.appointment.dto.response.TreatmentPlanMasterResponse;
import com.ayurveda.appointment.entity.TreatmentPlanMaster;
import com.ayurveda.appointment.enums.TreatmentPlanMasterStatus;
import com.ayurveda.appointment.mapper.TreatmentPlanMasterMapper;
import com.ayurveda.appointment.repository.TreatmentPlanMasterRepository;
import com.ayurveda.appointment.service.TreatmentPlanMasterService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TreatmentPlanMasterServiceImpl implements TreatmentPlanMasterService {

    private final TreatmentPlanMasterRepository treatmentPlanMasterRepository;
    private final TreatmentPlanMasterMapper treatmentPlanMasterMapper;

    @Override
    public ApiResponse<TreatmentPlanMasterResponse> create(CreateTreatmentPlanMasterRequest request) {
        log.info("Creating treatment plan master: {}", request.getName());

        if (treatmentPlanMasterRepository.existsByNameIgnoreCaseAndDeletedFalse(request.getName())) {
            throw new DuplicateResourceException(
                    Constants.TREATMENT_PLAN_MASTER_ALREADY_EXISTS_WITH_NAME + request.getName());
        }

        TreatmentPlanMaster saved =
                treatmentPlanMasterRepository.save(treatmentPlanMasterMapper.toEntity(request));

        log.info("Treatment plan master created successfully with id: {}", saved.getId());
        return ApiResponse.success(
                Constants.TREATMENT_PLAN_MASTER_CREATED, treatmentPlanMasterMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TreatmentPlanMasterResponse> getById(UUID treatmentPlanId) {
        log.info("Fetching treatment plan master with id: {}", treatmentPlanId);

        TreatmentPlanMaster entity = treatmentPlanMasterRepository
                .findByIdAndDeletedFalse(treatmentPlanId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.TREATMENT_PLAN_MASTER_NOT_FOUND));

        return ApiResponse.success(
                Constants.TREATMENT_PLAN_MASTER_FETCHED, treatmentPlanMasterMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TreatmentPlanMasterResponse>> getAll() {
        log.info("Fetching all treatment plan masters");

        List<TreatmentPlanMasterResponse> plans = treatmentPlanMasterRepository
                .findAllByDeletedFalseOrderByNameAsc()
                .stream()
                .map(treatmentPlanMasterMapper::toResponse)
                .toList();

        log.info("Fetched {} treatment plan masters successfully", plans.size());
        return ApiResponse.success(Constants.TREATMENT_PLAN_MASTERS_FETCHED, plans);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TreatmentPlanMasterResponse>> getActive() {
        log.info("Fetching active treatment plan masters");

        List<TreatmentPlanMasterResponse> plans = treatmentPlanMasterRepository
                .findAllByStatusAndDeletedFalseOrderByNameAsc(TreatmentPlanMasterStatus.ACTIVE)
                .stream()
                .map(treatmentPlanMasterMapper::toResponse)
                .toList();

        log.info("Fetched {} active treatment plan masters successfully", plans.size());
        return ApiResponse.success(Constants.TREATMENT_PLAN_MASTERS_FETCHED, plans);
    }

}
