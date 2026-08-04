package com.ayurveda.appointment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateAppointmentPhysicalExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentPhysicalExaminationResponse;
import com.ayurveda.appointment.entity.AppointmentPhysicalExamination;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.AppointmentPhysicalExaminationMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentPhysicalExaminationRepository;
import com.ayurveda.appointment.service.AppointmentPhysicalExaminationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentPhysicalExaminationServiceImpl
        implements AppointmentPhysicalExaminationService {

    private final AppointmentPhysicalExaminationRepository
            appointmentPhysicalExaminationRepository;

    private final AppointmentBookingRepository
            appointmentBookingRepository;

    private final AppointmentPhysicalExaminationMapper
            appointmentPhysicalExaminationMapper;

    @Override
    public ApiResponse<AppointmentPhysicalExaminationResponse> savePhysicalExamination(
            CreateAppointmentPhysicalExaminationRequest request) {

        log.info("Saving physical examination for patient: {}",
                request.getPatientId());

        if (!appointmentBookingRepository.existsByPatientId(request.getPatientId())) {
            throw new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + request.getPatientId());
        }

        AppointmentPhysicalExamination physicalExamination =
                appointmentPhysicalExaminationRepository
                        .findByPatientId(request.getPatientId())
                        .orElse(null);

        String message;

        if (physicalExamination == null) {

            physicalExamination =
                    appointmentPhysicalExaminationMapper
                            .toEntity(request);

            message = "Physical examination created successfully.";

        } else {

            appointmentPhysicalExaminationMapper
                    .updateEntity(physicalExamination, request);

            message = "Physical examination updated successfully.";
        }

        AppointmentPhysicalExamination savedPhysicalExamination =
                appointmentPhysicalExaminationRepository
                        .save(physicalExamination);

        AppointmentPhysicalExaminationResponse response =
                appointmentPhysicalExaminationMapper
                        .toResponse(savedPhysicalExamination);

        log.info("Physical examination saved successfully for patient: {}",
                request.getPatientId());

        return ApiResponse.success(message, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentPhysicalExaminationResponse>
            getPhysicalExaminationByPatientId(UUID patientId) {

        log.info("Fetching physical examination for patient: {}",
                patientId);

        AppointmentPhysicalExamination physicalExamination =
                appointmentPhysicalExaminationRepository
                        .findByPatientId(patientId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Physical examination not found for patient: "
                                        + patientId));

        AppointmentPhysicalExaminationResponse response =
                appointmentPhysicalExaminationMapper
                        .toResponse(physicalExamination);

        log.info("Physical examination fetched successfully for patient: {}",
                patientId);

        return ApiResponse.success(
                "Physical examination fetched successfully.",
                response);
    }

}