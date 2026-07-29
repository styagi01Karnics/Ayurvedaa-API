package com.ayurveda.therapist.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.therapist.client.AppointmentServiceClient;
import com.ayurveda.therapist.dto.client.TherapyMasterClientResponse;
import com.ayurveda.therapist.dto.request.CreateTherapistRequest;
import com.ayurveda.therapist.dto.request.UpdateTherapistRequest;
import com.ayurveda.therapist.dto.request.UpdateTherapistStatusRequest;
import com.ayurveda.therapist.dto.response.AssignedTherapyResponse;
import com.ayurveda.therapist.dto.response.TherapistResponse;
import com.ayurveda.therapist.entity.Therapist;
import com.ayurveda.therapist.enums.TherapistStatus;
import com.ayurveda.therapist.mapper.TherapistMapper;
import com.ayurveda.therapist.repository.TherapistRepository;
import com.ayurveda.therapist.service.TherapistService;
import com.ayurveda.therapist.util.TherapistCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TherapistServiceImpl implements TherapistService {

    private final TherapistRepository therapistRepository;
    private final TherapistCodeGenerator therapistCodeGenerator;
    private final AppointmentServiceClient appointmentServiceClient;

    @Override
    @Transactional
    public ApiResponse<TherapistResponse> createTherapist(CreateTherapistRequest request) {
        TherapistStatus status = request.getStatus() != null ? request.getStatus() : TherapistStatus.ACTIVE;

        List<UUID> therapyIds = normalizeAndValidateTherapyIds(request.getAssignedTherapyIds());

        Therapist therapist = Therapist.builder()
                .therapistName(request.getName().trim())
                .therapistCode(therapistCodeGenerator.generate())
                .status(status)
                .assignedTherapyIds(new ArrayList<>(therapyIds))
                .build();

        Therapist savedTherapist = therapistRepository.save(therapist);
        return ApiResponse.success(
                AppConstants.THERAPIST_CREATED_SUCCESSFULLY, toResponse(savedTherapist));
    }

    @Override
    @Transactional
    public ApiResponse<TherapistResponse> updateTherapist(
            UUID therapistId, UpdateTherapistRequest request) {

        Therapist therapist = therapistRepository.findByIdAndDeletedFalse(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.THERAPIST_NOT_FOUND));

        List<UUID> therapyIds = normalizeAndValidateTherapyIds(request.getAssignedTherapyIds());

        therapist.setTherapistName(request.getName().trim());
        if (request.getStatus() != null) {
            therapist.setStatus(request.getStatus());
        }
        therapist.setAssignedTherapyIds(new ArrayList<>(therapyIds));

        Therapist savedTherapist = therapistRepository.save(therapist);
        return ApiResponse.success(
                AppConstants.THERAPIST_UPDATED_SUCCESSFULLY, toResponse(savedTherapist));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TherapistResponse> getTherapistById(UUID therapistId) {
        Therapist therapist = therapistRepository.findByIdAndDeletedFalse(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.THERAPIST_NOT_FOUND));
        return ApiResponse.success(
                AppConstants.THERAPIST_FETCHED_SUCCESSFULLY, toResponse(therapist));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapistResponse>> getAllTherapists() {
        Map<UUID, String> therapyNames = loadTherapyNameMap();
        List<TherapistResponse> therapists = therapistRepository.findAllByDeletedFalse().stream()
                .map(therapist -> TherapistMapper.toResponse(
                        therapist,
                        resolveAssignedTherapies(therapist.getAssignedTherapyIds(), therapyNames)))
                .toList();
        return ApiResponse.success(AppConstants.THERAPISTS_FETCHED_SUCCESSFULLY, therapists);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TherapistResponse>> getTherapistsByTherapyIds(List<UUID> therapyIds) {
        if (CollectionUtils.isEmpty(therapyIds)) {
            throw new BadRequestException(AppConstants.THERAPY_IDS_REQUIRED);
        }

        List<UUID> distinctIds = therapyIds.stream().distinct().toList();
        log.info("Fetching therapists for therapy ids {}", distinctIds);

        Map<UUID, String> therapyNames = loadTherapyNameMap();
        List<TherapistResponse> therapists = therapistRepository
                .findByAssignedTherapyIds(distinctIds)
                .stream()
                .map(therapist -> TherapistMapper.toResponse(
                        therapist,
                        resolveAssignedTherapies(therapist.getAssignedTherapyIds(), therapyNames)))
                .toList();

        return ApiResponse.success(
                AppConstants.THERAPISTS_BY_THERAPIES_FETCHED_SUCCESSFULLY, therapists);
    }

    @Override
    @Transactional
    public ApiResponse<TherapistResponse> updateTherapistStatus(
            UUID therapistId, UpdateTherapistStatusRequest request) {
        Therapist therapist = therapistRepository.findByIdAndDeletedFalse(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.THERAPIST_NOT_FOUND));

        therapist.setStatus(request.getStatus());
        Therapist savedTherapist = therapistRepository.save(therapist);

        return ApiResponse.success(
                AppConstants.THERAPIST_STATUS_UPDATED_SUCCESSFULLY, toResponse(savedTherapist));
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteTherapist(UUID therapistId) {
        Therapist therapist = therapistRepository.findByIdAndDeletedFalse(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.THERAPIST_NOT_FOUND));

        therapist.setDeleted(true);
        therapistRepository.save(therapist);

        return ApiResponse.success(AppConstants.THERAPIST_DELETED_SUCCESSFULLY, null);
    }

    private TherapistResponse toResponse(Therapist therapist) {
        return TherapistMapper.toResponse(
                therapist,
                resolveAssignedTherapies(therapist.getAssignedTherapyIds(), loadTherapyNameMap()));
    }

    private List<UUID> normalizeAndValidateTherapyIds(List<UUID> therapyIds) {
        List<UUID> distinctIds = therapyIds.stream().distinct().toList();
        for (UUID therapyId : distinctIds) {
            validateTherapyExists(therapyId);
        }
        return distinctIds;
    }

    private void validateTherapyExists(UUID therapyId) {
        try {
            ApiResponse<TherapyMasterClientResponse> response =
                    appointmentServiceClient.getTherapyById(therapyId);
            if (response == null || !response.isSuccess() || response.getData() == null) {
                throw new BadRequestException(AppConstants.INVALID_THERAPY_ID + therapyId);
            }
        } catch (BadRequestException ex) {
            throw ex;
        } catch (feign.FeignException.NotFound ex) {
            throw new BadRequestException(AppConstants.INVALID_THERAPY_ID + therapyId);
        } catch (feign.FeignException ex) {
            log.error("Appointment-service call failed while validating therapy {}: status={}, body={}",
                    therapyId, ex.status(), ex.contentUTF8(), ex);
            throw new BadRequestException(
                    "Unable to validate therapy id with appointment-service. "
                            + "Check services.appointment.url. Downstream status: " + ex.status());
        } catch (Exception ex) {
            log.error("Unable to validate therapy {}: {}", therapyId, ex.getMessage(), ex);
            throw new BadRequestException(
                    "Unable to validate therapy id with appointment-service: " + ex.getMessage());
        }
    }

    private Map<UUID, String> loadTherapyNameMap() {
        Map<UUID, String> therapyNames = new HashMap<>();
        try {
            ApiResponse<List<TherapyMasterClientResponse>> response =
                    appointmentServiceClient.getAllTherapies();
            if (response != null && response.isSuccess() && response.getData() != null) {
                for (TherapyMasterClientResponse therapy : response.getData()) {
                    if (therapy.getId() == null) {
                        continue;
                    }
                    therapyNames.put(therapy.getId(), resolveTherapyName(therapy));
                }
            }
        } catch (Exception ex) {
            log.error(
                    "Unable to load therapy names from appointment-service (check SERVICES_APPOINTMENT_URL / Docker network): {}",
                    ex.getMessage(),
                    ex);
        }
        return therapyNames;
    }

    private List<AssignedTherapyResponse> resolveAssignedTherapies(
            List<UUID> therapyIds, Map<UUID, String> therapyNames) {
        if (CollectionUtils.isEmpty(therapyIds)) {
            return List.of();
        }
        return therapyIds.stream()
                .map(id -> AssignedTherapyResponse.builder()
                        .id(id)
                        .name(resolveTherapyNameForId(id, therapyNames))
                        .build())
                .toList();
    }

    private String resolveTherapyNameForId(UUID therapyId, Map<UUID, String> therapyNames) {
        String cached = therapyNames.get(therapyId);
        if (StringUtils.hasText(cached)) {
            return cached;
        }
        try {
            ApiResponse<TherapyMasterClientResponse> response =
                    appointmentServiceClient.getTherapyById(therapyId);
            if (response != null && response.isSuccess() && response.getData() != null) {
                String name = resolveTherapyName(response.getData());
                if (StringUtils.hasText(name)) {
                    therapyNames.put(therapyId, name);
                    return name;
                }
            }
        } catch (Exception ex) {
            log.warn("Unable to resolve therapy name for {}: {}", therapyId, ex.getMessage());
        }
        return null;
    }

    private String resolveTherapyName(TherapyMasterClientResponse therapy) {
        if (therapy == null) {
            return null;
        }
        return StringUtils.hasText(therapy.getTherapyName())
                ? therapy.getTherapyName()
                : therapy.getName();
    }

}
