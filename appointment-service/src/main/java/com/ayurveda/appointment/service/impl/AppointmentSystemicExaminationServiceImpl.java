package com.ayurveda.appointment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateAppointmentSystemicExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentSystemicExaminationResponse;
import com.ayurveda.appointment.entity.AppointmentSystemicExamination;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.AppointmentSystemicExaminationMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentSystemicExaminationRepository;
import com.ayurveda.appointment.service.AppointmentSystemicExaminationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentSystemicExaminationServiceImpl
        implements AppointmentSystemicExaminationService {

    private final AppointmentSystemicExaminationRepository
            appointmentSystemicExaminationRepository;

    private final AppointmentBookingRepository
            appointmentBookingRepository;

    private final AppointmentSystemicExaminationMapper
            appointmentSystemicExaminationMapper;

    @Override
    public ApiResponse<AppointmentSystemicExaminationResponse> saveSystemicExamination(
            CreateAppointmentSystemicExaminationRequest request) {

        log.info("Saving systemic examination for patient: {}",
                request.getPatientId());

        if (!appointmentBookingRepository.existsByPatientId(request.getPatientId())) {
            throw new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + request.getPatientId());
        }

        AppointmentSystemicExamination systemicExamination =
                appointmentSystemicExaminationRepository
                        .findByPatientId(request.getPatientId())
                        .orElse(null);

        String message;

        if (systemicExamination == null) {

            systemicExamination =
                    appointmentSystemicExaminationMapper
                            .toEntity(request);

            message = Constants.SYSTEMIC_EXAMINATION_CREATED;

        } else {

            appointmentSystemicExaminationMapper
                    .updateEntity(systemicExamination, request);

            message = Constants.SYSTEMIC_EXAMINATION_UPDATED;
        }

        AppointmentSystemicExamination savedSystemicExamination =
                appointmentSystemicExaminationRepository
                        .save(systemicExamination);

        AppointmentSystemicExaminationResponse response =
                appointmentSystemicExaminationMapper
                        .toResponse(savedSystemicExamination);

        log.info("Systemic examination saved successfully for patient: {}",
                request.getPatientId());

        return ApiResponse.success(message, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentSystemicExaminationResponse>
            getSystemicExaminationByPatientId(UUID patientId) {

        log.info("Fetching systemic examination for patient: {}",
                patientId);

        AppointmentSystemicExamination systemicExamination =
                appointmentSystemicExaminationRepository
                        .findByPatientId(patientId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                Constants.SYSTEMIC_EXAMINATION_NOT_FOUND
                                        + patientId));

        AppointmentSystemicExaminationResponse response =
                appointmentSystemicExaminationMapper
                        .toResponse(systemicExamination);

        log.info("Systemic examination fetched successfully for patient: {}",
                patientId);

        return ApiResponse.success(
                Constants.SYSTEMIC_EXAMINATION_FETCHED,
                response);
    }

}