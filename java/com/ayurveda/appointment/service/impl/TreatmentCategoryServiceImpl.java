package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateTreatmentCategoryRequest;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;
import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.exception.DuplicateResourceException;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.TreatmentCategoryMapper;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.service.TreatmentCategoryService;
import com.ayurveda.appointment.util.TreatmentCategoryCodeGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        return ApiResponse.success(response);
    }
}