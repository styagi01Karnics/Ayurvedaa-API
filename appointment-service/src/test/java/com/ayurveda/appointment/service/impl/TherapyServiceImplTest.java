package com.ayurveda.appointment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
import com.ayurveda.appointment.util.TherapyCodeGenerator;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class TherapyServiceImplTest {

    @Mock
    private TherapyRepository therapyRepository;

    @Mock
    private TreatmentCategoryRepository treatmentCategoryRepository;

    @Mock
    private TherapyMapper therapyMapper;

    @Mock
    private TherapyCodeGenerator therapyCodeGenerator;

    @InjectMocks
    private TherapyServiceImpl therapyService;

    private UUID therapyId;
    private UUID categoryId;
    private TreatmentCategoryMaster category;
    private TherapyMaster therapy;
    private TherapyResponse therapyResponse;

    @BeforeEach
    void setUp() {
        therapyId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        category = TreatmentCategoryMaster.builder()
                .categoryName("Massage Therapy")
                .build();
        ReflectionTestUtils.setField(category, "id", categoryId);

        therapy = TherapyMaster.builder()
                .therapyName("Kayakalpa")
                .categoryId(categoryId)
                .status(TherapyMasterStatus.ACTIVE)
                .durationMinutes(45)
                .price(BigDecimal.valueOf(1800))
                .build();
        ReflectionTestUtils.setField(therapy, "id", therapyId);

        therapyResponse = TherapyResponse.builder()
                .id(therapyId)
                .name("Kayakalpa")
                .therapyName("Kayakalpa")
                .categoryId(categoryId)
                .categoryName("Massage Therapy")
                .status(TherapyMasterStatus.ACTIVE)
                .build();
    }

    @Test
    void createTherapy_success() {
        CreateTherapyRequest request = CreateTherapyRequest.builder()
                .name(" Kayakalpa ")
                .categoryId(categoryId)
                .durationMinutes(45)
                .price(BigDecimal.valueOf(1800))
                .build();

        when(treatmentCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(therapyRepository.existsByTherapyNameAndDeletedFalse("Kayakalpa")).thenReturn(false);
        when(therapyMapper.toEntity(request, TherapyMasterStatus.ACTIVE)).thenReturn(therapy);
        when(therapyCodeGenerator.generateTherapyCode()).thenReturn("TH003");
        when(therapyRepository.save(therapy)).thenReturn(therapy);
        when(therapyMapper.toResponse(therapy, "Massage Therapy")).thenReturn(therapyResponse);

        ApiResponse<TherapyResponse> response = therapyService.createTherapy(request);

        assertTrue(response.isSuccess());
        assertEquals("Kayakalpa", response.getData().getName());
    }

    @Test
    void createTherapy_duplicateName_throws() {
        CreateTherapyRequest request = CreateTherapyRequest.builder()
                .name("Kayakalpa")
                .categoryId(categoryId)
                .durationMinutes(45)
                .price(BigDecimal.TEN)
                .build();

        when(treatmentCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(therapyRepository.existsByTherapyNameAndDeletedFalse("Kayakalpa")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> therapyService.createTherapy(request));
    }

    @Test
    void getTherapyById_notFound_throws() {
        when(therapyRepository.findByIdAndDeletedFalse(therapyId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> therapyService.getTherapyById(therapyId));
    }

    @Test
    void getAllTherapies_withStatusFilter() {
        when(therapyRepository.findAllByDeletedFalseAndStatus(TherapyMasterStatus.ACTIVE))
                .thenReturn(List.of(therapy));
        when(treatmentCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(therapyMapper.toResponse(therapy, "Massage Therapy")).thenReturn(therapyResponse);

        ApiResponse<List<TherapyResponse>> response =
                therapyService.getAllTherapies(TherapyMasterStatus.ACTIVE);

        assertEquals(1, response.getData().size());
    }

    @Test
    void getAllTherapies_withoutStatusUsesAll() {
        when(therapyRepository.findAllByDeletedFalse()).thenReturn(List.of(therapy));
        when(treatmentCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(therapyMapper.toResponse(therapy, "Massage Therapy")).thenReturn(therapyResponse);

        assertEquals(1, therapyService.getAllTherapies(null).getData().size());
    }

    @Test
    void updateTherapyStatus_updatesOnlyStatus() {
        UpdateTherapyStatusRequest request = UpdateTherapyStatusRequest.builder()
                .status(TherapyMasterStatus.INACTIVE)
                .build();

        when(therapyRepository.findByIdAndDeletedFalse(therapyId)).thenReturn(Optional.of(therapy));
        when(therapyRepository.save(therapy)).thenReturn(therapy);
        when(treatmentCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(therapyMapper.toResponse(eq(therapy), any())).thenReturn(therapyResponse);

        ApiResponse<TherapyResponse> response = therapyService.updateTherapyStatus(therapyId, request);

        assertTrue(response.isSuccess());
        assertEquals(TherapyMasterStatus.INACTIVE, therapy.getStatus());
    }

    @Test
    void updateTherapy_success() {
        UpdateTherapyRequest request = UpdateTherapyRequest.builder()
                .name("Updated")
                .categoryId(categoryId)
                .durationMinutes(60)
                .price(BigDecimal.valueOf(2000))
                .status(TherapyMasterStatus.ACTIVE)
                .build();

        when(therapyRepository.findByIdAndDeletedFalse(therapyId)).thenReturn(Optional.of(therapy));
        when(treatmentCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(therapyRepository.findByTherapyName("Updated")).thenReturn(Optional.empty());
        when(therapyRepository.save(therapy)).thenReturn(therapy);
        when(therapyMapper.toResponse(therapy, "Massage Therapy")).thenReturn(therapyResponse);

        ApiResponse<TherapyResponse> response = therapyService.updateTherapy(therapyId, request);

        assertTrue(response.isSuccess());
        assertEquals("Updated", therapy.getTherapyName());
    }

    @Test
    void deleteTherapy_softDeletes() {
        when(therapyRepository.findByIdAndDeletedFalse(therapyId)).thenReturn(Optional.of(therapy));
        when(therapyRepository.save(therapy)).thenReturn(therapy);
        when(treatmentCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(therapyMapper.toResponse(eq(therapy), any())).thenReturn(therapyResponse);

        ApiResponse<TherapyResponse> response = therapyService.deleteTherapy(therapyId);

        assertTrue(response.isSuccess());
        assertEquals(Boolean.TRUE, therapy.getDeleted());
    }

    @Test
    void getTherapiesByCategory_categoryMissing_throws() {
        when(treatmentCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> therapyService.getTherapiesByCategory(categoryId, null));
    }

}
