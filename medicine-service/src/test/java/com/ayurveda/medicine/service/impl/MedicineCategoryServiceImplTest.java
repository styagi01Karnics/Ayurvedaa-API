package com.ayurveda.medicine.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.medicine.dto.request.CreateMedicineCategoryRequest;
import com.ayurveda.medicine.entity.MedicineCategoryMaster;
import com.ayurveda.medicine.repository.MedicineCategoryMasterRepository;
import com.ayurveda.medicine.repository.MedicineRepository;

@ExtendWith(MockitoExtension.class)
class MedicineCategoryServiceImplTest {

    @Mock
    private MedicineCategoryMasterRepository categoryRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineCategoryServiceImpl categoryService;

    @Test
    void createCategoryNormalizesCodeToUppercase() {
        CreateMedicineCategoryRequest request = CreateMedicineCategoryRequest.builder()
                .code(" drops ")
                .name("Eye Drops")
                .build();
        when(categoryRepository.existsByCodeAndDeletedFalse("DROPS")).thenReturn(false);
        when(categoryRepository.save(any(MedicineCategoryMaster.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = categoryService.createCategory(request);

        assertEquals("DROPS", response.getData().getCode());
        assertEquals("Eye Drops", response.getData().getName());
    }

    @Test
    void createCategoryRejectsDuplicateCode() {
        CreateMedicineCategoryRequest request = CreateMedicineCategoryRequest.builder()
                .code("SYRUP")
                .name("Syrup")
                .build();
        when(categoryRepository.existsByCodeAndDeletedFalse("SYRUP")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.createCategory(request));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategoryIsBlockedWhenMedicinesUseIt() {
        UUID id = UUID.randomUUID();
        MedicineCategoryMaster category = MedicineCategoryMaster.builder()
                .code("SYRUP")
                .name("Syrup")
                .active(true)
                .build();
        when(categoryRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(category));
        when(medicineRepository.countByCategory("SYRUP")).thenReturn(2L);

        assertThrows(BadRequestException.class, () -> categoryService.deleteCategory(id));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void assertActiveCodeRejectsInactiveCategory() {
        MedicineCategoryMaster inactive = MedicineCategoryMaster.builder()
                .code("OLD")
                .name("Old")
                .active(false)
                .build();
        when(categoryRepository.findByCodeAndDeletedFalse("OLD")).thenReturn(Optional.of(inactive));

        assertThrows(BadRequestException.class, () -> categoryService.assertActiveCode("OLD"));
    }

    @Test
    void assertActiveCodeRejectsUnknownOrNullCode() {
        when(categoryRepository.findByCodeAndDeletedFalse("NOPE")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> categoryService.assertActiveCode("NOPE"));
        assertThrows(BadRequestException.class, () -> categoryService.assertActiveCode(null));
    }
}
