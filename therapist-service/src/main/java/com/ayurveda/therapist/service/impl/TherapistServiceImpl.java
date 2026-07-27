package com.ayurveda.therapist.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.therapist.dto.request.CreateTherapistRequest;
import com.ayurveda.therapist.dto.response.TherapistResponse;
import com.ayurveda.therapist.entity.Therapist;
import com.ayurveda.therapist.enums.TherapistStatus;
import com.ayurveda.therapist.mapper.TherapistMapper;
import com.ayurveda.therapist.repository.TherapistRepository;
import com.ayurveda.therapist.service.TherapistService;
import com.ayurveda.therapist.util.TherapistCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TherapistServiceImpl implements TherapistService {

    private final TherapistRepository therapistRepository;
    private final TherapistCodeGenerator therapistCodeGenerator;

    @Override
    @Transactional
    public ApiResponse<TherapistResponse> createTherapist(CreateTherapistRequest request) {
        TherapistStatus status = request.getStatus() != null ? request.getStatus() : TherapistStatus.ACTIVE;

        List<String> therapies = request.getAssignedTherapies().stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        Therapist therapist = Therapist.builder()
                .therapistName(request.getName().trim())
                .therapistCode(therapistCodeGenerator.generate())
                .status(status)
                .assignedTherapies(new ArrayList<>(therapies))
                .active(status == TherapistStatus.ACTIVE)
                .build();

        Therapist savedTherapist = therapistRepository.save(therapist);
        return ApiResponse.success(
                AppConstants.THERAPIST_CREATED_SUCCESSFULLY, TherapistMapper.toResponse(savedTherapist));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TherapistResponse> getTherapistById(UUID therapistId) {
        Therapist therapist = therapistRepository.findByIdAndDeletedFalse(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.THERAPIST_NOT_FOUND));
        return ApiResponse.success(
                AppConstants.THERAPIST_FETCHED_SUCCESSFULLY, TherapistMapper.toResponse(therapist));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapistResponse>> getAllTherapists() {
        AtomicInteger serial = new AtomicInteger(1);
        List<TherapistResponse> therapists = therapistRepository.findAllByDeletedFalse().stream()
                .map(therapist -> TherapistMapper.toResponse(therapist, serial.getAndIncrement()))
                .toList();
        return ApiResponse.success(AppConstants.THERAPISTS_FETCHED_SUCCESSFULLY, therapists);
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteTherapist(UUID therapistId) {
        Therapist therapist = therapistRepository.findByIdAndDeletedFalse(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.THERAPIST_NOT_FOUND));

        therapist.setDeleted(true);
        therapist.setActive(false);
        therapist.setStatus(TherapistStatus.INACTIVE);
        therapistRepository.save(therapist);

        return ApiResponse.success(AppConstants.THERAPIST_DELETED_SUCCESSFULLY, null);
    }

}
