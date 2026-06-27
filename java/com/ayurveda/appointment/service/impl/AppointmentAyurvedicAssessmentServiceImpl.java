package com.ayurveda.appointment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateAppointmentAyurvedicAssessmentRequest;
import com.ayurveda.appointment.dto.response.AppointmentAyurvedicAssessmentResponse;
import com.ayurveda.appointment.entity.AppointmentAyurvedicAssessment;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.AppointmentAyurvedicAssessmentMapper;
import com.ayurveda.appointment.repository.AppointmentAyurvedicAssessmentRepository;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.service.AppointmentAyurvedicAssessmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentAyurvedicAssessmentServiceImpl
        implements AppointmentAyurvedicAssessmentService {

    private final AppointmentAyurvedicAssessmentRepository
            appointmentAyurvedicAssessmentRepository;

    private final AppointmentBookingRepository
            appointmentBookingRepository;

    private final AppointmentAyurvedicAssessmentMapper
            appointmentAyurvedicAssessmentMapper;

    @Override
    public ApiResponse<AppointmentAyurvedicAssessmentResponse> saveAyurvedicAssessment(
            CreateAppointmentAyurvedicAssessmentRequest request) {

        log.info("Saving ayurvedic assessment for booking: {}",
                request.getBookingId());

        appointmentBookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        Constants.APPOINTMENT_NOT_FOUND
                                + request.getBookingId()));

        AppointmentAyurvedicAssessment assessment =
                appointmentAyurvedicAssessmentRepository
                        .findByBookingId(request.getBookingId())
                        .orElse(null);

        String message;

        if (assessment == null) {

            assessment =
                    appointmentAyurvedicAssessmentMapper
                            .toEntity(request);

            message = Constants.AYURVEDIC_ASSESSMENT_CREATED;

        } else {

            appointmentAyurvedicAssessmentMapper
                    .updateEntity(assessment, request);

            message = Constants.AYURVEDIC_ASSESSMENT_UPDATED;
        }

        AppointmentAyurvedicAssessment savedAssessment =
                appointmentAyurvedicAssessmentRepository
                        .save(assessment);

        AppointmentAyurvedicAssessmentResponse response =
                appointmentAyurvedicAssessmentMapper
                        .toResponse(savedAssessment);

        return ApiResponse.success(message, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentAyurvedicAssessmentResponse>
            getAyurvedicAssessmentByBookingId(UUID bookingId) {

        log.info("Fetching ayurvedic assessment for booking: {}",
                bookingId);

        AppointmentAyurvedicAssessment assessment =
                appointmentAyurvedicAssessmentRepository
                        .findByBookingId(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                Constants.AYURVEDIC_ASSESSMENT_NOT_FOUND
                                        + bookingId));

        AppointmentAyurvedicAssessmentResponse response =
                appointmentAyurvedicAssessmentMapper
                        .toResponse(assessment);

        return ApiResponse.success(
                Constants.AYURVEDIC_ASSESSMENT_FETCHED,
                response);
    }

}