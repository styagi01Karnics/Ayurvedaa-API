package com.ayurveda.appointment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.Constants;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentMedicalHistoryRequest;
import com.ayurveda.appointment.dto.response.AppointmentMedicalHistoryResponse;
import com.ayurveda.appointment.entity.AppointmentMedicalHistory;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.AppointmentMedicalHistoryMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentMedicalHistoryRepository;
import com.ayurveda.appointment.service.AppointmentMedicalHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentMedicalHistoryServiceImpl
        implements AppointmentMedicalHistoryService {

    private final AppointmentMedicalHistoryRepository
            appointmentMedicalHistoryRepository;

    private final AppointmentBookingRepository
            appointmentBookingRepository;

    private final AppointmentMedicalHistoryMapper
            appointmentMedicalHistoryMapper;

    @Override
    public ApiResponse<AppointmentMedicalHistoryResponse> saveMedicalHistory(
            CreateAppointmentMedicalHistoryRequest request) {

        log.info("Saving medical history for patient : {}",
                request.getPatientId());

        if (!appointmentBookingRepository.existsByPatientId(request.getPatientId())) {
            throw new ResourceNotFoundException(
                    Constants.APPOINTMENT_NOT_FOUND_FOR_PATIENT + request.getPatientId());
        }

        AppointmentMedicalHistory medicalHistory =
                appointmentMedicalHistoryRepository
                        .findByPatientId(request.getPatientId())
                        .orElse(null);

        String message;

        if (medicalHistory == null) {

            medicalHistory =
                    appointmentMedicalHistoryMapper.toEntity(request);

            message = "Medical history created successfully";

        } else {

            appointmentMedicalHistoryMapper
                    .updateEntity(medicalHistory, request);

            message = "Medical history updated successfully";
        }

        AppointmentMedicalHistory savedMedicalHistory =
                appointmentMedicalHistoryRepository.save(medicalHistory);

        AppointmentMedicalHistoryResponse response =
                appointmentMedicalHistoryMapper
                        .toResponse(savedMedicalHistory);

        log.info("Medical history saved successfully for patient: {}",
                request.getPatientId());

        return ApiResponse.success(message, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentMedicalHistoryResponse>
            getMedicalHistoryByPatientId(UUID patientId) {

        log.info("Fetching medical history for patient : {}", patientId);

        AppointmentMedicalHistory medicalHistory =
                appointmentMedicalHistoryRepository
                        .findByPatientId(patientId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Medical history not found for patient : "
                                        + patientId));

        AppointmentMedicalHistoryResponse response =
                appointmentMedicalHistoryMapper
                        .toResponse(medicalHistory);

        log.info("Medical history fetched successfully for patient: {}", patientId);

        return ApiResponse.success(response);
    }

}