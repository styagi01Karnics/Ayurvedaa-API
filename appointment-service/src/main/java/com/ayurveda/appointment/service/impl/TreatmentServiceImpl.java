package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.client.PatientServiceClient;
import com.ayurveda.appointment.client.TherapistServiceClient;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateTreatmentRequest;
import com.ayurveda.appointment.dto.request.UpdateTreatmentRequest;
import com.ayurveda.appointment.dto.request.UpdateTreatmentStatusRequest;
import com.ayurveda.appointment.dto.response.TherapistSummaryResponse;
import com.ayurveda.appointment.dto.response.TreatmentResponse;
import com.ayurveda.appointment.entity.Treatment;
import com.ayurveda.appointment.entity.TreatmentPlanMaster;
import com.ayurveda.appointment.enums.TreatmentStatus;
import com.ayurveda.appointment.repository.TreatmentPlanMasterRepository;
import com.ayurveda.appointment.repository.TreatmentRepository;
import com.ayurveda.appointment.service.TreatmentService;
import com.ayurveda.appointment.util.AppMessages;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final TreatmentPlanMasterRepository treatmentPlanMasterRepository;
    private final PatientServiceClient patientServiceClient;
    private final TherapistServiceClient therapistServiceClient;

    @Override
    public ApiResponse<TreatmentResponse> createTreatment(CreateTreatmentRequest request) {
        log.info("Creating treatment for patient: {}", request.getPatientId());

        validatePatient(request.getPatientId());
        TreatmentPlanMaster treatmentPlan = fetchTreatmentPlan(request.getTreatmentPlanId());
        TherapistSummaryResponse therapist = fetchTherapist(request.getAssignedTherapistId());
        validateDates(request.getStartDate(), request.getEndDate());

        int completed = request.getCompletedSessions() != null ? request.getCompletedSessions() : 0;
        int remaining = calculateRemaining(request.getTotalSessions(), completed);

        TreatmentStatus status = request.getTreatmentStatus() != null
                ? request.getTreatmentStatus()
                : TreatmentStatus.SCHEDULED;

        Treatment treatment = Treatment.builder()
                .patientId(request.getPatientId())
                .treatmentPlanId(treatmentPlan.getId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalSessions(request.getTotalSessions())
                .completedSessions(completed)
                .remainingSessions(remaining)
                .assignedTherapistId(request.getAssignedTherapistId())
                .treatmentStatus(status)
                .build();

        Treatment saved = treatmentRepository.save(treatment);
        log.info("Treatment created successfully. Treatment ID: {}", saved.getId());

        return ApiResponse.success(
                AppMessages.TREATMENT_CREATED, toResponse(saved, therapist, treatmentPlan));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TreatmentResponse>> getAllTreatments() {
        log.info("Fetching all treatments");

        List<TreatmentResponse> responses = treatmentRepository
                .findAllByDeletedFalseOrderByStartDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("Fetched {} treatments successfully", responses.size());
        return ApiResponse.success(AppMessages.TREATMENTS_FETCHED, responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TreatmentResponse>> getTreatmentsByPatientId(UUID patientId) {
        log.info("Fetching treatments for patient: {}", patientId);

        // Patient soft-delete must not block reading existing treatment rows.
        List<TreatmentResponse> responses = treatmentRepository
                .findAllByPatientIdAndDeletedFalseOrderByStartDateDesc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("Fetched {} treatments for patient: {}", responses.size(), patientId);
        return ApiResponse.success(AppMessages.TREATMENTS_FETCHED, responses);
    }

    @Override
    public ApiResponse<TreatmentResponse> updateTreatment(
            UUID treatmentId, UpdateTreatmentRequest request) {

        log.info("Updating treatment: {}", treatmentId);

        Treatment treatment = findActiveTreatment(treatmentId);
        TreatmentPlanMaster treatmentPlan = fetchTreatmentPlan(request.getTreatmentPlanId());
        TherapistSummaryResponse therapist = fetchTherapist(request.getAssignedTherapistId());
        validateDates(request.getStartDate(), request.getEndDate());

        int remaining = calculateRemaining(request.getTotalSessions(), request.getCompletedSessions());

        treatment.setTreatmentPlanId(treatmentPlan.getId());
        treatment.setStartDate(request.getStartDate());
        treatment.setEndDate(request.getEndDate());
        treatment.setTotalSessions(request.getTotalSessions());
        treatment.setCompletedSessions(request.getCompletedSessions());
        treatment.setRemainingSessions(remaining);
        treatment.setAssignedTherapistId(request.getAssignedTherapistId());

        Treatment saved = treatmentRepository.save(treatment);
        log.info("Treatment updated successfully. Treatment ID: {}", treatmentId);

        return ApiResponse.success(
                AppMessages.TREATMENT_UPDATED, toResponse(saved, therapist, treatmentPlan));
    }

    @Override
    public ApiResponse<TreatmentResponse> updateTreatmentStatus(
            UUID treatmentId, UpdateTreatmentStatusRequest request) {

        log.info("Updating treatment status. Treatment ID: {}, Status: {}",
                treatmentId, request.getTreatmentStatus());

        Treatment treatment = findActiveTreatment(treatmentId);
        treatment.setTreatmentStatus(request.getTreatmentStatus());
        Treatment saved = treatmentRepository.save(treatment);

        log.info("Treatment status updated successfully. Treatment ID: {}", treatmentId);
        return ApiResponse.success(AppMessages.TREATMENT_STATUS_UPDATED, toResponse(saved));
    }

    private Treatment findActiveTreatment(UUID treatmentId) {
        return treatmentRepository.findByIdAndDeletedFalse(treatmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.TREATMENT_NOT_FOUND_WITH_ID + treatmentId));
    }

    private TreatmentPlanMaster fetchTreatmentPlan(UUID treatmentPlanId) {
        return treatmentPlanMasterRepository.findByIdAndDeletedFalse(treatmentPlanId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.TREATMENT_PLAN_MASTER_NOT_FOUND));
    }

    private void validatePatient(UUID patientId) {
        try {
            if (patientServiceClient.getPatientById(patientId).getData() == null) {
                throw new ResourceNotFoundException(AppMessages.PATIENT_NOT_FOUND_WITH_ID + patientId);
            }
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (feign.FeignException.NotFound ex) {
            throw new ResourceNotFoundException(AppMessages.PATIENT_NOT_FOUND_WITH_ID + patientId);
        }
    }

    private TherapistSummaryResponse fetchTherapist(UUID therapistId) {
        try {
            TherapistSummaryResponse therapist = therapistServiceClient
                    .getTherapistById(therapistId)
                    .getData();

            if (therapist == null) {
                throw new ResourceNotFoundException(AppMessages.THERAPIST_NOT_FOUND_WITH_ID + therapistId);
            }
            return therapist;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (feign.FeignException.NotFound ex) {
            throw new ResourceNotFoundException(AppMessages.THERAPIST_NOT_FOUND_WITH_ID + therapistId);
        }
    }

    private void validateDates(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException(AppMessages.TREATMENT_END_DATE_BEFORE_START);
        }
    }

    private int calculateRemaining(int totalSessions, int completedSessions) {
        if (completedSessions > totalSessions) {
            throw new BadRequestException(AppMessages.TREATMENT_COMPLETED_SESSIONS_EXCEED_TOTAL);
        }
        return totalSessions - completedSessions;
    }

    private TreatmentResponse toResponse(Treatment treatment) {
        TherapistSummaryResponse therapist = null;
        TreatmentPlanMaster treatmentPlan = null;
        try {
            therapist = fetchTherapist(treatment.getAssignedTherapistId());
        } catch (Exception ex) {
            // Soft-deleted / missing therapist must not fail treatment GET.
            log.warn("Therapist unavailable for treatment {}: {} ({})",
                    treatment.getId(), treatment.getAssignedTherapistId(), ex.getMessage());
        }
        try {
            treatmentPlan = fetchTreatmentPlan(treatment.getTreatmentPlanId());
        } catch (Exception ex) {
            log.warn("Treatment plan unavailable for treatment {}: {} ({})",
                    treatment.getId(), treatment.getTreatmentPlanId(), ex.getMessage());
        }
        return toResponse(treatment, therapist, treatmentPlan);
    }

    private TreatmentResponse toResponse(
            Treatment treatment,
            TherapistSummaryResponse therapist,
            TreatmentPlanMaster treatmentPlan) {

        String therapistName = null;
        if (therapist != null) {
            therapistName = therapist.getName() != null
                    ? therapist.getName()
                    : therapist.getTherapistName();
        }

        return TreatmentResponse.builder()
                .id(treatment.getId())
                .patientId(treatment.getPatientId())
                .treatmentPlanId(treatment.getTreatmentPlanId())
                .treatmentPlanName(treatmentPlan != null ? treatmentPlan.getName() : null)
                .startDate(treatment.getStartDate())
                .endDate(treatment.getEndDate())
                .totalSessions(treatment.getTotalSessions())
                .completedSessions(treatment.getCompletedSessions())
                .remainingSessions(treatment.getRemainingSessions())
                .assignedTherapistId(treatment.getAssignedTherapistId())
                .assignedTherapistName(therapistName)
                .treatmentStatus(treatment.getTreatmentStatus())
                .build();
    }

}
