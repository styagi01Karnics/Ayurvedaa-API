package com.ayurveda.appointment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentMedicalHistoryRequest;
import com.ayurveda.appointment.dto.response.AppointmentMedicalHistoryResponse;
import com.ayurveda.appointment.entity.AppointmentMedicalHistory;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
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

        log.info("Saving medical history for booking : {}",
                request.getBookingId());

        appointmentBookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id : "
                                + request.getBookingId()));

        AppointmentMedicalHistory medicalHistory =
                appointmentMedicalHistoryRepository
                        .findByBookingId(request.getBookingId())
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

        return ApiResponse.success(message, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentMedicalHistoryResponse>
            getMedicalHistoryByBookingId(UUID bookingId) {

        log.info("Fetching medical history for booking : {}", bookingId);

        AppointmentMedicalHistory medicalHistory =
                appointmentMedicalHistoryRepository
                        .findByBookingId(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Medical history not found for booking : "
                                        + bookingId));

        AppointmentMedicalHistoryResponse response =
                appointmentMedicalHistoryMapper
                        .toResponse(medicalHistory);

        return ApiResponse.success(response);
    }

}