package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateDoshaRequest;
import com.ayurveda.appointment.dto.response.DoshaResponse;
import com.ayurveda.appointment.entity.DoshaMaster;
import com.ayurveda.appointment.mapper.DoshaMapper;
import com.ayurveda.appointment.repository.DoshaMasterRepository;
import com.ayurveda.appointment.service.DoshaMasterService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DoshaMasterServiceImpl implements DoshaMasterService {

    private final DoshaMasterRepository doshaMasterRepository;
    private final DoshaMapper doshaMapper;

    @Override
    public ApiResponse<DoshaResponse> createDosha(CreateDoshaRequest request) {
        log.info("Creating dosha: {}", request.getName());

        if (doshaMasterRepository.existsByNameIgnoreCaseAndDeletedFalse(request.getName())) {
            throw new DuplicateResourceException(
                    Constants.DOSHA_ALREADY_EXISTS_WITH_NAME + request.getName());
        }

        DoshaMaster saved = doshaMasterRepository.save(doshaMapper.toEntity(request));

        log.info("Dosha created successfully with id: {}", saved.getId());

        return ApiResponse.success(Constants.DOSHA_CREATED, doshaMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DoshaResponse> getDoshaById(UUID doshaId) {
        log.info("Fetching dosha with id: {}", doshaId);

        DoshaMaster dosha = doshaMasterRepository.findByIdAndDeletedFalse(doshaId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOSHA_NOT_FOUND));

        log.info("Dosha fetched successfully with id: {}", doshaId);

        return ApiResponse.success(Constants.DOSHA_FETCHED, doshaMapper.toResponse(dosha));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<DoshaResponse>> getAllDoshas() {
        log.info("Fetching all doshas");

        List<DoshaResponse> doshas = doshaMasterRepository.findAllByDeletedFalseOrderByNameAsc().stream()
                .map(doshaMapper::toResponse)
                .toList();

        log.info("Fetched {} doshas successfully", doshas.size());

        return ApiResponse.success(Constants.DOSHAS_FETCHED, doshas);
    }

}
