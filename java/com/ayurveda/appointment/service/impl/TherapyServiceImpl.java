package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.exception.DuplicateResourceException;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.TherapyMapper;
import com.ayurveda.appointment.repository.TherapyRepository;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.service.TherapyService;
import com.ayurveda.appointment.util.TherapyCodeGenerator;

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
    public ApiResponse<TherapyResponse> createTherapy(
            CreateTherapyRequest request) {

        log.info("Creating therapy : {}", request.getTherapyName());

        treatmentCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Treatment category not found with id : "
                                + request.getCategoryId()));

        if (therapyRepository.existsByTherapyName(request.getTherapyName())) {
            throw new DuplicateResourceException(
                    "Therapy already exists with name : "
                            + request.getTherapyName());
        }

        TherapyMaster therapy = therapyMapper.toEntity(request);

        therapy.setTherapyCode(
                therapyCodeGenerator.generateTherapyCode());

        TherapyMaster savedTherapy =
                therapyRepository.save(therapy);

        log.info("Therapy created successfully with id : {}",
                savedTherapy.getId());

        TherapyResponse response =
                therapyMapper.toResponse(savedTherapy);

        return ApiResponse.success(
                "Therapy created successfully",
                response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TherapyResponse> getTherapyById(
            UUID therapyId) {

        log.info("Fetching therapy : {}", therapyId);

        TherapyMaster therapy =
                therapyRepository.findById(therapyId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Therapy not found with id : " + therapyId));

        return ApiResponse.success(
                therapyMapper.toResponse(therapy));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapyResponse>> getAllTherapies() {

        log.info("Fetching all therapies");

        List<TherapyResponse> response =
                therapyRepository.findAll()
                        .stream()
                        .map(therapyMapper::toResponse)
                        .toList();

        return ApiResponse.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapyResponse>> getTherapiesByCategory(
            UUID categoryId) {

        log.info("Fetching therapies for category : {}", categoryId);

        treatmentCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Treatment category not found with id : "
                                + categoryId));

        List<TherapyResponse> response =
                therapyRepository.findByCategoryId(categoryId)
                        .stream()
                        .map(therapyMapper::toResponse)
                        .toList();

        return ApiResponse.success(response);
    }

}