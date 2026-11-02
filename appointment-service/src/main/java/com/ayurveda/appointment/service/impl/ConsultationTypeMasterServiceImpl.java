package com.ayurveda.appointment.service.impl;

import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateConsultationTypeRequest;
import com.ayurveda.appointment.dto.response.ConsultationTypeResponse;
import com.ayurveda.appointment.entity.ConsultationTypeMaster;
import com.ayurveda.appointment.enums.ConsultationTypeMasterStatus;
import com.ayurveda.appointment.mapper.ConsultationTypeMapper;
import com.ayurveda.appointment.repository.ConsultationTypeMasterRepository;
import com.ayurveda.appointment.service.ConsultationTypeMasterService;
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
public class ConsultationTypeMasterServiceImpl implements ConsultationTypeMasterService {

    private final ConsultationTypeMasterRepository consultationTypeMasterRepository;
    private final ConsultationTypeMapper consultationTypeMapper;

    @Override
    public ApiResponse<ConsultationTypeResponse> create(CreateConsultationTypeRequest request) {
        log.info("Creating consultation type: {}", request.getName());

        if (consultationTypeMasterRepository.existsByNameIgnoreCaseAndDeletedFalse(request.getName())) {
            throw new DuplicateResourceException(
                    Constants.CONSULTATION_TYPE_ALREADY_EXISTS_WITH_NAME + request.getName());
        }

        ConsultationTypeMaster saved =
                consultationTypeMasterRepository.save(consultationTypeMapper.toEntity(request));

        log.info("Consultation type created successfully with id: {}", saved.getId());
        return ApiResponse.success(Constants.CONSULTATION_TYPE_CREATED, consultationTypeMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<ConsultationTypeResponse> getById(UUID consultationTypeId) {
        log.info("Fetching consultation type with id: {}", consultationTypeId);

        ConsultationTypeMaster entity = consultationTypeMasterRepository
                .findByIdAndDeletedFalse(consultationTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CONSULTATION_TYPE_NOT_FOUND));

        return ApiResponse.success(Constants.CONSULTATION_TYPE_FETCHED, consultationTypeMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ConsultationTypeResponse>> getAll() {
        log.info("Fetching all consultation types");

        List<ConsultationTypeResponse> types = consultationTypeMasterRepository
                .findAllByDeletedFalseOrderByNameAsc()
                .stream()
                .map(consultationTypeMapper::toResponse)
                .toList();

        log.info("Fetched {} consultation types successfully", types.size());
        return ApiResponse.success(Constants.CONSULTATION_TYPES_FETCHED, types);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ConsultationTypeResponse>> getActive() {
        log.info("Fetching active consultation types");

        List<ConsultationTypeResponse> types = consultationTypeMasterRepository
                .findAllByStatusAndDeletedFalseOrderByNameAsc(ConsultationTypeMasterStatus.ACTIVE)
                .stream()
                .map(consultationTypeMapper::toResponse)
                .toList();

        log.info("Fetched {} active consultation types successfully", types.size());
        return ApiResponse.success(Constants.CONSULTATION_TYPES_FETCHED, types);
    }

    @Override
    public ApiResponse<ConsultationTypeResponse> updateConsultationType(UUID consultationTypeId, CreateConsultationTypeRequest request) {
        log.info("Updating consultation type by id: {}", consultationTypeId);

        return consultationTypeMasterRepository.findByIdAndDeletedFalse(consultationTypeId)
                .map(existingType -> {
                    existingType.setName(request.getName());
                    existingType.setStatus(request.getStatus());
                    existingType.setUpdatedAt(java.time.LocalDateTime.now());
                    return consultationTypeMasterRepository.save(existingType);
                })
                .map(savedType -> {
                    log.info("Consultation type updated successfully with id: {}", savedType.getId());
                    ConsultationTypeResponse response = consultationTypeMapper.toResponse(savedType);
                    return ApiResponse.success("Consultation type updated successfully with id: " + savedType.getId(), response);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Consultation type not found with id: " + consultationTypeId));
    }

    @Override
    public ApiResponse<Void> deleteConsultationType(UUID consultationTypeId) {
        log.info("Deleting consultation type by id: {}", consultationTypeId);

        return consultationTypeMasterRepository.findByIdAndDeletedFalse(consultationTypeId)
                .map(existingType -> {
                    existingType.setStatus(ConsultationTypeMasterStatus.INACTIVE);
                    existingType.setDeleted(Boolean.TRUE);
                    existingType.setUpdatedAt(java.time.LocalDateTime.now());
                    consultationTypeMasterRepository.save(existingType);
                    log.info("Consultation type deactivated successfully with id: {}", consultationTypeId);
                    return ApiResponse.<Void>success("Consultation type deactivated successfully with id: " + consultationTypeId, null);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consultation type not found with id: " + consultationTypeId));
    }

}
