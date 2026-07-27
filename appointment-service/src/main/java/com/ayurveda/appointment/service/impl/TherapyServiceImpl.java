package com.ayurveda.appointment.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.client.TherapistServiceClient;
import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.response.AssignedTherapistResponse;
import com.ayurveda.appointment.dto.response.TherapistSummaryResponse;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.enums.TherapyMasterStatus;
import com.ayurveda.appointment.mapper.TherapyMapper;
import com.ayurveda.appointment.repository.TherapyRepository;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.service.TherapyService;
import com.ayurveda.appointment.util.TherapyCodeGenerator;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
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
    private final TherapistServiceClient therapistServiceClient;
    private final TherapyMapper therapyMapper;
    private final TherapyCodeGenerator therapyCodeGenerator;

    @Override
    public ApiResponse<TherapyResponse> createTherapy(CreateTherapyRequest request) {
        log.info("Creating therapy : {}", request.getName());

        TreatmentCategoryMaster category = treatmentCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Treatment category not found with id : " + request.getCategoryId()));

        TherapistSummaryResponse therapist = fetchTherapist(request.getAssignedTherapistId());

        String therapyName = request.getName().trim();
        if (therapyRepository.existsByTherapyNameAndDeletedFalse(therapyName)) {
            throw new DuplicateResourceException(
                    "Therapy already exists with name : " + therapyName);
        }

        TherapyMasterStatus status =
                request.getStatus() != null ? request.getStatus() : TherapyMasterStatus.ACTIVE;

        TherapyMaster therapy = therapyMapper.toEntity(request, status);
        therapy.setTherapyCode(therapyCodeGenerator.generateTherapyCode());

        TherapyMaster savedTherapy = therapyRepository.save(therapy);

        log.info("Therapy created successfully with id : {}", savedTherapy.getId());

        return ApiResponse.success(
                "Therapy created successfully",
                therapyMapper.toResponse(
                        savedTherapy,
                        null,
                        category.getCategoryName(),
                        therapist.getTherapistName()));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TherapyResponse> getTherapyById(UUID therapyId) {
        log.info("Fetching therapy : {}", therapyId);

        TherapyMaster therapy = therapyRepository.findByIdAndDeletedFalse(therapyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Therapy not found with id : " + therapyId));

        return ApiResponse.success(toEnrichedResponse(therapy, null));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapyResponse>> getAllTherapies() {
        log.info("Fetching all therapies");

        AtomicInteger serial = new AtomicInteger(1);
        List<TherapyResponse> response = therapyRepository.findAllByDeletedFalse().stream()
                .map(therapy -> toEnrichedResponse(therapy, serial.getAndIncrement()))
                .toList();

        return ApiResponse.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapyResponse>> getTherapiesByCategory(UUID categoryId) {
        log.info("Fetching therapies for category : {}", categoryId);

        treatmentCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Treatment category not found with id : " + categoryId));

        AtomicInteger serial = new AtomicInteger(1);
        List<TherapyResponse> response =
                therapyRepository.findByCategoryIdAndDeletedFalse(categoryId).stream()
                        .map(therapy -> toEnrichedResponse(therapy, serial.getAndIncrement()))
                        .toList();

        return ApiResponse.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AssignedTherapistResponse>> getAssignedTherapistsByTherapyIds(
            List<UUID> therapyIds) {

        if (therapyIds == null || therapyIds.isEmpty()) {
            throw new BadRequestException("At least one therapy id is required.");
        }

        List<UUID> distinctTherapyIds = therapyIds.stream().distinct().toList();
        log.info("Fetching assigned therapists for therapies: {}", distinctTherapyIds);

        List<TherapyMaster> therapies =
                therapyRepository.findByIdInAndDeletedFalse(distinctTherapyIds);

        if (therapies.isEmpty()) {
            throw new ResourceNotFoundException("No therapies found for the given ids.");
        }

        Map<UUID, AssignedTherapistResponse> therapistsById = new LinkedHashMap<>();

        for (TherapyMaster therapy : therapies) {
            UUID therapistId = therapy.getAssignedTherapistId();
            if (therapistId == null) {
                continue;
            }

            AssignedTherapistResponse existing = therapistsById.get(therapistId);
            if (existing == null) {
                TherapistSummaryResponse therapist = fetchTherapist(therapistId);
                existing = AssignedTherapistResponse.builder()
                        .therapistId(therapist.getId())
                        .therapistName(therapist.getTherapistName())
                        .therapistCode(therapist.getTherapistCode())
                        .mobileNumber(therapist.getMobileNumber())
                        .therapyIds(new ArrayList<>())
                        .therapyNames(new ArrayList<>())
                        .build();
                therapistsById.put(therapistId, existing);
            }

            existing.getTherapyIds().add(therapy.getId());
            existing.getTherapyNames().add(therapy.getTherapyName());
        }

        return ApiResponse.success(
                "Assigned therapists fetched successfully.",
                new ArrayList<>(therapistsById.values()));
    }

    @Override
    public ApiResponse<Void> deleteTherapy(UUID therapyId) {
        TherapyMaster therapy = therapyRepository.findByIdAndDeletedFalse(therapyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Therapy not found with id : " + therapyId));

        therapy.setDeleted(true);
        therapy.setActive(false);
        therapy.setStatus(TherapyMasterStatus.INACTIVE);
        therapyRepository.save(therapy);

        return ApiResponse.success("Therapy deleted successfully.", null);
    }

    private TherapyResponse toEnrichedResponse(TherapyMaster therapy, Integer serialNo) {
        String categoryName = treatmentCategoryRepository.findById(therapy.getCategoryId())
                .map(TreatmentCategoryMaster::getCategoryName)
                .orElse(null);

        String therapistName = null;
        if (therapy.getAssignedTherapistId() != null) {
            try {
                TherapistSummaryResponse therapist = fetchTherapist(therapy.getAssignedTherapistId());
                therapistName = therapist != null ? therapist.getTherapistName() : null;
            } catch (Exception ex) {
                log.warn("Unable to fetch therapist {}: {}",
                        therapy.getAssignedTherapistId(), ex.getMessage());
            }
        }

        return therapyMapper.toResponse(therapy, serialNo, categoryName, therapistName);
    }

    private TherapistSummaryResponse fetchTherapist(UUID therapistId) {
        ApiResponse<TherapistSummaryResponse> response =
                therapistServiceClient.getTherapistById(therapistId);

        if (response == null || !response.isSuccess() || response.getData() == null) {
            throw new ResourceNotFoundException("Therapist not found with id : " + therapistId);
        }
        return response.getData();
    }

}
