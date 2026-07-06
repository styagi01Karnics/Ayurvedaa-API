package com.ayurveda.appointment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateAppointmentAyurvedicAssessmentRequest;
import com.ayurveda.appointment.dto.response.AppointmentAyurvedicAssessmentResponse;
import com.ayurveda.appointment.dto.response.DoshaResponse;
import com.ayurveda.appointment.entity.AppointmentAyurvedicAssessment;
import com.ayurveda.appointment.mapper.AppointmentAyurvedicAssessmentMapper;
import com.ayurveda.appointment.repository.AppointmentAyurvedicAssessmentRepository;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.service.AppointmentAyurvedicAssessmentService;
import com.ayurveda.appointment.service.DoshaMasterService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentAyurvedicAssessmentServiceImpl
        implements AppointmentAyurvedicAssessmentService {

    private final AppointmentAyurvedicAssessmentRepository appointmentAyurvedicAssessmentRepository;
    private final AppointmentBookingRepository appointmentBookingRepository;
    private final AppointmentAyurvedicAssessmentMapper appointmentAyurvedicAssessmentMapper;
    private final DoshaMasterService doshaMasterService;

    @Override
    public ApiResponse<AppointmentAyurvedicAssessmentResponse> saveAyurvedicAssessment(
            CreateAppointmentAyurvedicAssessmentRequest request) {

        log.info("Saving ayurvedic assessment for patient: {}", request.getPatientId());

        if (!appointmentBookingRepository.existsByPatientId(request.getPatientId())) {
            throw new ResourceNotFoundException(Constants.APPOINTMENT_NOT_FOUND + request.getPatientId());
        }

        DoshaResponse dosha = getDosha(request.getDoshaId());

        AppointmentAyurvedicAssessment assessment = appointmentAyurvedicAssessmentRepository
                .findByPatientId(request.getPatientId())
                .orElse(null);

        String message;

        if (assessment == null) {
            assessment = appointmentAyurvedicAssessmentMapper.toEntity(request);
            message = Constants.AYURVEDIC_ASSESSMENT_CREATED;
        } else {
            appointmentAyurvedicAssessmentMapper.updateEntity(assessment, request);
            message = Constants.AYURVEDIC_ASSESSMENT_UPDATED;
        }

        AppointmentAyurvedicAssessment savedAssessment =
                appointmentAyurvedicAssessmentRepository.save(assessment);

        return ApiResponse.success(message,
                appointmentAyurvedicAssessmentMapper.toResponse(savedAssessment, dosha));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentAyurvedicAssessmentResponse> getAyurvedicAssessmentByPatientId(
            UUID patientId) {

        log.info("Fetching ayurvedic assessment for patient: {}", patientId);

        AppointmentAyurvedicAssessment assessment = appointmentAyurvedicAssessmentRepository
                .findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        Constants.AYURVEDIC_ASSESSMENT_NOT_FOUND + patientId));

        DoshaResponse dosha = getDosha(assessment.getDoshaId());

        return ApiResponse.success(Constants.AYURVEDIC_ASSESSMENT_FETCHED,
                appointmentAyurvedicAssessmentMapper.toResponse(assessment, dosha));
    }

    private DoshaResponse getDosha(UUID doshaId) {
        ApiResponse<DoshaResponse> response = doshaMasterService.getDoshaById(doshaId);
        return response.getData();
    }

}
