package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.request.UpdateTherapyRequest;
import com.ayurveda.appointment.dto.request.UpdateTherapyStatusRequest;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.enums.TherapyMasterStatus;
import com.ayurveda.appointment.mapper.TherapyMapper;
import com.ayurveda.appointment.repository.TherapyRepository;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.service.TherapyService;
import com.ayurveda.appointment.util.AppMessages;
import com.ayurveda.appointment.util.TherapyCodeGenerator;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TherapyServiceImpl implements TherapyService {

    private final TherapyRepository therapyRepository;
    private final TreatmentCategoryRepository treatmentCategoryRepository;
    private final TherapyMapper therapyMapper;
    private final TherapyCodeGenerator therapyCodeGenerator;

    @Override
    public ApiResponse<TherapyResponse> createTherapy(CreateTherapyRequest request) {
        log.info("Creating therapy : {}", request.getName());

        TreatmentCategoryMaster category = treatmentCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.TREATMENT_CATEGORY_NOT_FOUND_WITH_ID + request.getCategoryId()));

        String therapyName = request.getName().trim();
        if (therapyRepository.existsByTherapyNameAndDeletedFalse(therapyName)) {
            throw new DuplicateResourceException(
                    AppMessages.THERAPY_ALREADY_EXISTS_WITH_NAME + therapyName);
        }

        TherapyMasterStatus status =
                request.getStatus() != null ? request.getStatus() : TherapyMasterStatus.ACTIVE;

        TherapyMaster therapy = therapyMapper.toEntity(request, status);
        therapy.setTherapyCode(therapyCodeGenerator.generateTherapyCode());

        TherapyMaster savedTherapy = therapyRepository.save(therapy);

        log.info("Therapy created successfully with id : {}", savedTherapy.getId());

        return ApiResponse.success(
                AppMessages.THERAPY_CREATED,
                therapyMapper.toResponse(savedTherapy, category.getCategoryName()));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TherapyResponse> getTherapyById(UUID therapyId) {
        log.info("Fetching therapy : {}", therapyId);

        TherapyMaster therapy = therapyRepository.findByIdAndDeletedFalse(therapyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.THERAPY_NOT_FOUND_WITH_ID + therapyId));

        return ApiResponse.success(toEnrichedResponse(therapy));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapyResponse>> getAllTherapies(TherapyMasterStatus status) {
        log.info("Fetching all therapies with status filter : {}", status);

        List<TherapyMaster> therapies = status == null
                ? therapyRepository.findAllByDeletedFalse()
                : therapyRepository.findAllByDeletedFalseAndStatus(status);

        List<TherapyResponse> response = therapies.stream()
                .map(this::toEnrichedResponse)
                .toList();

        return ApiResponse.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapyResponse>> getTherapiesByCategory(
            UUID categoryId, TherapyMasterStatus status) {
        log.info("Fetching therapies for category : {} with status filter : {}", categoryId, status);

        treatmentCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.TREATMENT_CATEGORY_NOT_FOUND_WITH_ID + categoryId));

        List<TherapyMaster> therapies = status == null
                ? therapyRepository.findByCategoryIdAndDeletedFalse(categoryId)
                : therapyRepository.findByCategoryIdAndDeletedFalseAndStatus(categoryId, status);

        List<TherapyResponse> response = therapies.stream()
                .map(this::toEnrichedResponse)
                .toList();

        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<TherapyResponse> updateTherapy(UUID therapyId, UpdateTherapyRequest request) {
        log.info("Updating therapy : {}", therapyId);

        TherapyMaster therapy = therapyRepository.findByIdAndDeletedFalse(therapyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.THERAPY_NOT_FOUND_WITH_ID + therapyId));

        TreatmentCategoryMaster category = treatmentCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.TREATMENT_CATEGORY_NOT_FOUND_WITH_ID + request.getCategoryId()));

        String therapyName = request.getName().trim();
        Optional<TherapyMaster> existingByName =
                therapyRepository.findByTherapyName(therapyName);
        if (existingByName.isPresent()
                && !existingByName.get().getId().equals(therapyId)
                && !Boolean.TRUE.equals(existingByName.get().getDeleted())) {
            throw new DuplicateResourceException(
                    AppMessages.THERAPY_ALREADY_EXISTS_WITH_NAME + therapyName);
        }

        therapy.setTherapyName(therapyName);
        therapy.setCategoryId(request.getCategoryId());
        therapy.setDurationMinutes(request.getDurationMinutes());
        therapy.setPrice(request.getPrice());
        therapy.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            therapy.setStatus(request.getStatus());
        }

        TherapyMaster savedTherapy = therapyRepository.save(therapy);

        return ApiResponse.success(
                AppMessages.THERAPY_UPDATED,
                therapyMapper.toResponse(savedTherapy, category.getCategoryName()));
    }

    @Override
    public ApiResponse<TherapyResponse> updateTherapyStatus(
            UUID therapyId, UpdateTherapyStatusRequest request) {

        TherapyMaster therapy = therapyRepository.findByIdAndDeletedFalse(therapyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.THERAPY_NOT_FOUND_WITH_ID + therapyId));

        therapy.setStatus(request.getStatus());
        TherapyMaster savedTherapy = therapyRepository.save(therapy);

        log.info("Therapy {} status updated to {} (deleted unchanged)", therapyId, request.getStatus());

        return ApiResponse.success(AppMessages.THERAPY_STATUS_UPDATED, toEnrichedResponse(savedTherapy));
    }

    @Override
    public ApiResponse<TherapyResponse> deleteTherapy(UUID therapyId) {
        TherapyMaster therapy = therapyRepository.findByIdAndDeletedFalse(therapyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.THERAPY_NOT_FOUND_WITH_ID + therapyId));

        therapy.setDeleted(true);
        TherapyMaster savedTherapy = therapyRepository.save(therapy);

        return ApiResponse.success(AppMessages.THERAPY_DELETED, toEnrichedResponse(savedTherapy));
    }

    private TherapyResponse toEnrichedResponse(TherapyMaster therapy) {
        String categoryName = treatmentCategoryRepository.findById(therapy.getCategoryId())
                .map(TreatmentCategoryMaster::getCategoryName)
                .orElse(null);

        return therapyMapper.toResponse(therapy, categoryName);
    }

}
