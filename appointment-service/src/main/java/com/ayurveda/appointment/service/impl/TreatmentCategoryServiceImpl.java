package com.ayurveda.appointment.service.impl;

import com.ayurveda.appointment.dto.request.CreateTreatmentCategoryRequest;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;
import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.enums.TreatmentCategoryStatus;
import com.ayurveda.appointment.mapper.TreatmentCategoryMapper;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.service.TreatmentCategoryService;
import com.ayurveda.appointment.util.TreatmentCategoryCodeGenerator;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TreatmentCategoryServiceImpl implements TreatmentCategoryService {

    private final TreatmentCategoryRepository treatmentCategoryRepository;
    private final TreatmentCategoryMapper treatmentCategoryMapper;
    private final TreatmentCategoryCodeGenerator treatmentCategoryCodeGenerator;

    @Override
    public ApiResponse<TreatmentCategoryResponse> createTreatmentCategory(
            CreateTreatmentCategoryRequest request) {

        log.info("Creating treatment category: {}", request.getCategoryName());

        if (treatmentCategoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new DuplicateResourceException(
                    "Treatment category already exists with name: "
                            + request.getCategoryName());
        }

        TreatmentCategoryMaster category =
                treatmentCategoryMapper.toEntity(request);

        category.setCategoryCode(
                treatmentCategoryCodeGenerator.generateCategoryCode());

        TreatmentCategoryMaster savedCategory =
                treatmentCategoryRepository.save(category);


        log.info("Treatment category created successfully with id: {}",
                savedCategory.getId());

        TreatmentCategoryResponse response =
                treatmentCategoryMapper.toResponse(savedCategory);

        return ApiResponse.success(
                "Treatment category created successfully",
                response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TreatmentCategoryResponse> getTreatmentCategoryById(
            UUID categoryId) {

        log.info("Fetching treatment category with id: {}", categoryId);

        TreatmentCategoryMaster category =
                treatmentCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Treatment category not found with id: "
                                        + categoryId));

        TreatmentCategoryResponse response =
                treatmentCategoryMapper.toResponse(category);

        log.info("Treatment category fetched successfully with id: {}", categoryId);

        return ApiResponse.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TreatmentCategoryResponse>> getAllTreatmentCategories() {

        log.info("Fetching all treatment categories");

        List<TreatmentCategoryResponse> response =
                treatmentCategoryRepository.findAll()
                        .stream()
                        .map(treatmentCategoryMapper::toResponse)
                        .toList();

        log.info("Fetched {} treatment categories successfully", response.size());

        return ApiResponse.success(response);
    }
log.info("Updating treatment category by id: {}", categoryId);

        return treatmentCategoryRepository.findById(categoryId)
            .map(existingCategory -> {
        existingCategory.setCategoryName(request.getCategoryName());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setStatus(request.getStatus());
        existingCategory.setUpdatedAt(java.time.LocalDateTime.now());
        return treatmentCategoryRepository.save(existingCategory);
    })
            .map(savedCategory -> {
        log.info("Treatment category updated successfully with id: {}", savedCategory.getId());
        TreatmentCategoryResponse response = treatmentCategoryMapper.toResponse(savedCategory);
        return ApiResponse.success("Treatment category updated successfully with id: " + savedCategory.getId(), response);
    })
            .orElseThrow(() -> new ResourceNotFoundException("Treatment category not found with id: " + categoryId));
    @Override
    public ApiResponse<TreatmentCategoryResponse> updateTreatmentCategory(UUID categoryId, CreateTreatmentCategoryRequest request) {

    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteTreatmentCategory(UUID categoryId) {

        log.info("Deleting treatment category by id: {}", categoryId);

        return treatmentCategoryRepository.findById(categoryId)
                .map(existingCategory -> {
                    existingCategory.setStatus(TreatmentCategoryStatus.INACTIVE);
                    existingCategory.setDeleted(Boolean.TRUE);
                    treatmentCategoryRepository.save(existingCategory);
                    log.info("Treatment category deactivated successfully with id: {}", categoryId);
                    return ApiResponse.<Void>success("Treatment category deactivated successfully with id: " + categoryId, null);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Treatment category not found with id: " + categoryId));
    }
}