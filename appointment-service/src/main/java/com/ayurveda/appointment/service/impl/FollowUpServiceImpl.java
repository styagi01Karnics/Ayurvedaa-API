package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.client.DoctorServiceClient;
import com.ayurveda.appointment.client.PatientServiceClient;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateFollowUpRequest;
import com.ayurveda.appointment.dto.request.UpdateFollowUpStatusRequest;
import com.ayurveda.appointment.dto.response.DoctorSummaryResponse;
import com.ayurveda.appointment.dto.response.FollowUpResponse;
import com.ayurveda.appointment.dto.response.PatientSummaryResponse;
import com.ayurveda.appointment.entity.ConsultationTypeMaster;
import com.ayurveda.appointment.entity.FollowUp;
import com.ayurveda.appointment.enums.FollowUpStatus;
import com.ayurveda.appointment.repository.ConsultationTypeMasterRepository;
import com.ayurveda.appointment.repository.FollowUpRepository;
import com.ayurveda.appointment.service.FollowUpService;
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
public class FollowUpServiceImpl implements FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final ConsultationTypeMasterRepository consultationTypeMasterRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;

    @Override
    public ApiResponse<FollowUpResponse> createFollowUp(CreateFollowUpRequest request) {
        log.info("Creating follow-up for patient: {}, doctor: {}",
                request.getPatientId(), request.getAssignedDoctorId());

        PatientSummaryResponse patient = fetchPatient(request.getPatientId());
        DoctorSummaryResponse doctor = fetchDoctor(request.getAssignedDoctorId());
        ConsultationTypeMaster visitType = fetchConsultationType(request.getVisitTypeId());

        FollowUpStatus status = request.getStatus() != null
                ? request.getStatus()
                : FollowUpStatus.UPCOMING;

        FollowUp followUp = FollowUp.builder()
                .patientId(request.getPatientId())
                .assignedDoctorId(request.getAssignedDoctorId())
                .sourceBookingId(request.getSourceBookingId())
                .visitTypeId(visitType.getId())
                .appointmentDate(request.getAppointmentDate())
                .schedulingOption(request.getSchedulingOption() != null
                        ? request.getSchedulingOption().trim()
                        : null)
                .smsReminderEnabled(Boolean.TRUE.equals(request.getSmsReminderEnabled()))
                .status(status)
                .build();

        FollowUp saved = followUpRepository.save(followUp);
        log.info("Follow-up created successfully. Follow-up ID: {}", saved.getId());

        return ApiResponse.success(
                AppMessages.FOLLOW_UP_CREATED, toResponse(saved, patient, doctor, visitType));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<FollowUpResponse>> getAllFollowUps() {
        log.info("Fetching all follow-ups");

        List<FollowUpResponse> responses = followUpRepository
                .findAllByDeletedFalseOrderByAppointmentDateAsc()
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("Fetched {} follow-ups successfully", responses.size());
        return ApiResponse.success(AppMessages.FOLLOW_UPS_FETCHED, responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<FollowUpResponse>> getFollowUpsByPatientId(UUID patientId) {
        log.info("Fetching follow-ups for patient: {}", patientId);

        // Soft-deleted patient must not block reading existing follow-up rows.
        List<FollowUpResponse> responses = followUpRepository
                .findAllByPatientIdAndDeletedFalseOrderByAppointmentDateAsc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("Fetched {} follow-ups for patient: {}", responses.size(), patientId);
        return ApiResponse.success(AppMessages.FOLLOW_UPS_FETCHED, responses);
    }

    @Override
    public ApiResponse<FollowUpResponse> updateFollowUpStatus(
            UUID followUpId, UpdateFollowUpStatusRequest request) {

        log.info("Updating follow-up status. Follow-up ID: {}, Status: {}",
                followUpId, request.getStatus());

        FollowUp followUp = followUpRepository.findByIdAndDeletedFalse(followUpId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.FOLLOW_UP_NOT_FOUND_WITH_ID + followUpId));

        followUp.setStatus(request.getStatus());
        FollowUp saved = followUpRepository.save(followUp);

        log.info("Follow-up status updated successfully. Follow-up ID: {}", followUpId);
        return ApiResponse.success(AppMessages.FOLLOW_UP_STATUS_UPDATED, toResponse(saved));
    }

    @Override
    public ApiResponse<FollowUpResponse> cancelFollowUp(UUID followUpId) {
        log.info("Cancelling follow-up. Follow-up ID: {}", followUpId);

        FollowUp followUp = followUpRepository.findByIdAndDeletedFalse(followUpId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.FOLLOW_UP_NOT_FOUND_WITH_ID + followUpId));

        if (followUp.getStatus() == FollowUpStatus.CANCELLED) {
            throw new BadRequestException(AppMessages.FOLLOW_UP_ALREADY_CANCELLED);
        }

        followUp.setStatus(FollowUpStatus.CANCELLED);
        FollowUp saved = followUpRepository.save(followUp);

        log.info("Follow-up cancelled successfully. Follow-up ID: {}", followUpId);
        return ApiResponse.success(AppMessages.FOLLOW_UP_CANCELLED, toResponse(saved));
    }

    private PatientSummaryResponse fetchPatient(UUID patientId) {
        try {
            PatientSummaryResponse patient = patientServiceClient.getPatientById(patientId).getData();
            if (patient == null) {
                throw new ResourceNotFoundException(AppMessages.PATIENT_NOT_FOUND_WITH_ID + patientId);
            }
            return patient;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (feign.FeignException.NotFound ex) {
            throw new ResourceNotFoundException(AppMessages.PATIENT_NOT_FOUND_WITH_ID + patientId);
        }
    }

    private DoctorSummaryResponse fetchDoctor(UUID doctorId) {
        try {
            DoctorSummaryResponse doctor = doctorServiceClient.getDoctorById(doctorId).getData();
            if (doctor == null) {
                throw new ResourceNotFoundException(AppMessages.DOCTOR_NOT_FOUND_WITH_ID + doctorId);
            }
            return doctor;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (feign.FeignException.NotFound ex) {
            throw new ResourceNotFoundException(AppMessages.DOCTOR_NOT_FOUND_WITH_ID + doctorId);
        }
    }

    private ConsultationTypeMaster fetchConsultationType(UUID visitTypeId) {
        return consultationTypeMasterRepository.findByIdAndDeletedFalse(visitTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CONSULTATION_TYPE_NOT_FOUND));
    }

    private FollowUpResponse toResponse(FollowUp followUp) {
        PatientSummaryResponse patient = null;
        DoctorSummaryResponse doctor = null;
        ConsultationTypeMaster visitType = null;
        try {
            patient = fetchPatient(followUp.getPatientId());
        } catch (Exception ex) {
            log.warn("Patient not found for follow-up {}: {}", followUp.getId(), followUp.getPatientId());
        }
        try {
            doctor = fetchDoctor(followUp.getAssignedDoctorId());
        } catch (Exception ex) {
            log.warn("Doctor not found for follow-up {}: {}", followUp.getId(), followUp.getAssignedDoctorId());
        }
        try {
            visitType = fetchConsultationType(followUp.getVisitTypeId());
        } catch (Exception ex) {
            log.warn("Visit type not found for follow-up {}: {}", followUp.getId(), followUp.getVisitTypeId());
        }
        return toResponse(followUp, patient, doctor, visitType);
    }

    private FollowUpResponse toResponse(
            FollowUp followUp,
            PatientSummaryResponse patient,
            DoctorSummaryResponse doctor,
            ConsultationTypeMaster visitType) {

        return FollowUpResponse.builder()
                .id(followUp.getId())
                .patientId(followUp.getPatientId())
                .patientDisplayId(patient != null ? patient.getPatientDisplayId() : null)
                .patientName(patient != null ? patient.getFullName() : null)
                .assignedDoctorId(followUp.getAssignedDoctorId())
                .doctorName(doctor != null ? doctor.getName() : null)
                .sourceBookingId(followUp.getSourceBookingId())
                .visitTypeId(followUp.getVisitTypeId())
                .visitTypeName(visitType != null ? visitType.getName() : null)
                .appointmentDate(followUp.getAppointmentDate())
                .schedulingOption(followUp.getSchedulingOption())
                .smsReminderEnabled(followUp.getSmsReminderEnabled())
                .status(followUp.getStatus())
                .build();
    }

}
