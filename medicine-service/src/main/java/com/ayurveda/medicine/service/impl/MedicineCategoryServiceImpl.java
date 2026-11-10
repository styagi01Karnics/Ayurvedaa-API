package com.ayurveda.medicine.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.medicine.constant.MedicineMessages;
import com.ayurveda.medicine.dto.request.CreateMedicineCategoryRequest;
import com.ayurveda.medicine.dto.request.UpdateMedicineCategoryRequest;
import com.ayurveda.medicine.dto.response.MedicineCategoryResponse;
import com.ayurveda.medicine.entity.MedicineCategoryMaster;
import com.ayurveda.medicine.repository.MedicineCategoryMasterRepository;
import com.ayurveda.medicine.repository.MedicineRepository;
import com.ayurveda.medicine.service.MedicineCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicineCategoryServiceImpl implements MedicineCategoryService {

    private final MedicineCategoryMasterRepository categoryRepository;
    private final MedicineRepository medicineRepository;

    @Override
    @Transactional(readOnly = true)
    public List<String> getActiveCategoryCodes() {
        return categoryRepository.findByDeletedFalseAndActiveTrueOrderByNameAsc().stream()
                .map(MedicineCategoryMaster::getCode)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<MedicineCategoryResponse>> getCategories() {
        List<MedicineCategoryResponse> categories = categoryRepository.findByDeletedFalseOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();

        return ApiResponse.success(MedicineMessages.MEDICINE_CATEGORIES_FETCHED_SUCCESSFULLY, categories);
    }

    @Override
    public ApiResponse<MedicineCategoryResponse> createCategory(CreateMedicineCategoryRequest request) {
        String code = normalizeCode(request.getCode());

        if (categoryRepository.existsByCodeAndDeletedFalse(code)) {
            throw new BadRequestException(MedicineMessages.MEDICINE_CATEGORY_CODE_ALREADY_EXISTS);
        }

        MedicineCategoryMaster saved = categoryRepository.save(MedicineCategoryMaster.builder()
                .code(code)
                .name(request.getName().trim())
                .active(true)
                .build());

        log.info("Medicine category created. Code: {}", code);

        return ApiResponse.success(MedicineMessages.MEDICINE_CATEGORY_CREATED_SUCCESSFULLY, toResponse(saved));
    }

    @Override
    public ApiResponse<MedicineCategoryResponse> updateCategory(UUID categoryId, UpdateMedicineCategoryRequest request) {
        MedicineCategoryMaster category = findActive(categoryId);

        category.setName(request.getName().trim());
        category.setActive(request.getActive());
        MedicineCategoryMaster saved = categoryRepository.save(category);

        log.info("Medicine category updated. Code: {}", saved.getCode());

        return ApiResponse.success(MedicineMessages.MEDICINE_CATEGORY_UPDATED_SUCCESSFULLY, toResponse(saved));
    }

    @Override
    public ApiResponse<Void> deleteCategory(UUID categoryId) {
        MedicineCategoryMaster category = findActive(categoryId);

        if (medicineRepository.countByCategory(category.getCode()) > 0) {
            throw new BadRequestException(MedicineMessages.MEDICINE_CATEGORY_IN_USE);
        }

        category.setDeleted(true);
        category.setActive(false);
        categoryRepository.save(category);

        log.info("Medicine category deleted. Code: {}", category.getCode());

        return ApiResponse.success(MedicineMessages.MEDICINE_CATEGORY_DELETED_SUCCESSFULLY, null);
    }

    @Override
    @Transactional(readOnly = true)
    public void assertActiveCode(String code) {
        boolean valid = code != null && categoryRepository.findByCodeAndDeletedFalse(code)
                .map(MedicineCategoryMaster::getActive)
                .orElse(false);

        if (!valid) {
            throw new BadRequestException(MedicineMessages.MEDICINE_CATEGORY_INVALID_OR_INACTIVE + code);
        }
    }

    private MedicineCategoryMaster findActive(UUID categoryId) {
        return categoryRepository.findByIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(MedicineMessages.MEDICINE_CATEGORY_NOT_FOUND));
    }

    private static String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private MedicineCategoryResponse toResponse(MedicineCategoryMaster category) {
        return MedicineCategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .active(category.getActive())
                .build();
    }

}
