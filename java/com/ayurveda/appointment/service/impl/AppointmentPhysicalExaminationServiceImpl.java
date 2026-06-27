package com.ayurveda.appointment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateAppointmentPhysicalExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentPhysicalExaminationResponse;
import com.ayurveda.appointment.entity.AppointmentPhysicalExamination;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
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

        log.info("Saving physical examination for booking: {}",
                request.getBookingId());

        appointmentBookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        Constants.APPOINTMENT_NOT_FOUND
                                + request.getBookingId()));

        AppointmentPhysicalExamination physicalExamination =
                appointmentPhysicalExaminationRepository
                        .findByBookingId(request.getBookingId())
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

        return ApiResponse.success(message, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentPhysicalExaminationResponse>
            getPhysicalExaminationByBookingId(UUID bookingId) {

        log.info("Fetching physical examination for booking: {}",
                bookingId);

        AppointmentPhysicalExamination physicalExamination =
                appointmentPhysicalExaminationRepository
                        .findByBookingId(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Physical examination not found for booking: "
                                        + bookingId));

        AppointmentPhysicalExaminationResponse response =
                appointmentPhysicalExaminationMapper
                        .toResponse(physicalExamination);

        return ApiResponse.success(
                "Physical examination fetched successfully.",
                response);
    }

}