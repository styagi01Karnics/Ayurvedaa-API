package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (doshaMasterRepository.existsByNameIgnoreCaseAndDeletedFalse(request.getName())) {
            throw new DuplicateResourceException("Dosha already exists with name: " + request.getName());
        }

        DoshaMaster saved = doshaMasterRepository.save(doshaMapper.toEntity(request));
        return ApiResponse.success("Dosha created successfully.", doshaMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DoshaResponse> getDoshaById(UUID doshaId) {
        DoshaMaster dosha = doshaMasterRepository.findByIdAndDeletedFalse(doshaId)
                .orElseThrow(() -> new ResourceNotFoundException("Dosha not found."));
        return ApiResponse.success("Dosha fetched successfully.", doshaMapper.toResponse(dosha));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<DoshaResponse>> getAllDoshas() {
        List<DoshaResponse> doshas = doshaMasterRepository.findAllByDeletedFalseOrderByNameAsc().stream()
                .map(doshaMapper::toResponse)
                .toList();
        return ApiResponse.success("Doshas fetched successfully.", doshas);
    }

}
