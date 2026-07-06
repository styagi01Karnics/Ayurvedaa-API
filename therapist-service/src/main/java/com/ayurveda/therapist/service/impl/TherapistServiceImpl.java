package com.ayurveda.therapist.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.therapist.dto.request.CreateTherapistRequest;
import com.ayurveda.therapist.dto.response.TherapistResponse;
import com.ayurveda.therapist.entity.Therapist;
import com.ayurveda.therapist.mapper.TherapistMapper;
import com.ayurveda.therapist.repository.TherapistRepository;
import com.ayurveda.therapist.service.TherapistService;
import com.ayurveda.therapist.util.TherapistCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TherapistServiceImpl implements TherapistService {

    private final TherapistRepository therapistRepository;
    private final TherapistCodeGenerator therapistCodeGenerator;

    @Override
    @Transactional
    public ApiResponse<TherapistResponse> createTherapist(CreateTherapistRequest request) {
        if (StringUtils.hasText(request.getEmail())
                && therapistRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new DuplicateResourceException("Therapist with this email already exists.");
        }

        Therapist therapist = Therapist.builder()
                .therapistName(request.getTherapistName())
                .therapistCode(therapistCodeGenerator.generate())
                .specialization(request.getSpecialization())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .qualification(request.getQualification())
                .therapyRoom(request.getTherapyRoom())
                .active(true)
                .build();

        Therapist savedTherapist = therapistRepository.save(therapist);
        return ApiResponse.success("Therapist created successfully.", TherapistMapper.toResponse(savedTherapist));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TherapistResponse> getTherapistById(UUID therapistId) {
        Therapist therapist = therapistRepository.findByIdAndDeletedFalse(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException("Therapist not found."));
        return ApiResponse.success("Therapist fetched successfully.", TherapistMapper.toResponse(therapist));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapistResponse>> getAllTherapists() {
        List<TherapistResponse> therapists = therapistRepository.findAllByDeletedFalse().stream()
                .map(TherapistMapper::toResponse)
                .toList();
        return ApiResponse.success("Therapists fetched successfully.", therapists);
    }

}
