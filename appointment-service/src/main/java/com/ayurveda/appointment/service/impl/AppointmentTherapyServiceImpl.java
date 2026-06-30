package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.client.TherapistServiceClient;
import com.ayurveda.appointment.dto.request.CreateAppointmentTherapyRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;
import com.ayurveda.appointment.dto.response.TherapistSummaryResponse;
import com.ayurveda.appointment.entity.AppointmentTherapy;
import com.ayurveda.appointment.entity.AppointmentTherapyRecommendation;
import com.ayurveda.appointment.mapper.AppointmentTherapyMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentTherapyRecommendationRepository;
import com.ayurveda.appointment.repository.AppointmentTherapyRepository;
import com.ayurveda.appointment.repository.TherapyRepository;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.service.AppointmentTherapyService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentTherapyServiceImpl implements AppointmentTherapyService {

    private final AppointmentTherapyRepository appointmentTherapyRepository;
    private final AppointmentTherapyRecommendationRepository appointmentTherapyRecommendationRepository;
    private final AppointmentBookingRepository appointmentBookingRepository;
    private final TreatmentCategoryRepository treatmentCategoryRepository;
    private final TherapyRepository therapyRepository;
    private final TherapistServiceClient therapistServiceClient;
    private final AppointmentTherapyMapper appointmentTherapyMapper;

    @Override
    public ApiResponse<AppointmentTherapyResponse> createAppointmentTherapy(
            CreateAppointmentTherapyRequest request) {

        log.info("Creating therapy details for booking: {}", request.getBookingId());

        appointmentBookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + request.getBookingId()));

        treatmentCategoryRepository.findById(request.getTreatmentCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Treatment category not found with id: " + request.getTreatmentCategoryId()));

        TherapistSummaryResponse therapist = fetchTherapist(request.getAssignedTherapistId());

        for (UUID therapyId : request.getTherapyIds()) {
            therapyRepository.findById(therapyId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Therapy not found with id: " + therapyId));
        }

        AppointmentTherapy appointmentTherapy = appointmentTherapyMapper.toEntity(request);
        AppointmentTherapy savedTherapy = appointmentTherapyRepository.save(appointmentTherapy);

        for (UUID therapyId : request.getTherapyIds()) {
            AppointmentTherapyRecommendation recommendation = AppointmentTherapyRecommendation.builder()
                    .appointmentTherapyId(savedTherapy.getId())
                    .therapyMasterId(therapyId)
                    .build();
            appointmentTherapyRecommendationRepository.save(recommendation);
        }

        AppointmentTherapyResponse response = appointmentTherapyMapper.toResponse(savedTherapy, therapist);
        response.setTherapyIds(request.getTherapyIds());

        log.info("Appointment therapy created successfully with id: {}", savedTherapy.getId());

        return ApiResponse.success("Appointment therapy created successfully", response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentTherapyResponse> getAppointmentTherapyByBookingId(UUID bookingId) {

        log.info("Fetching therapy details for booking: {}", bookingId);

        AppointmentTherapy appointmentTherapy = appointmentTherapyRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Therapy details not found for booking: " + bookingId));

        TherapistSummaryResponse therapist = fetchTherapist(appointmentTherapy.getAssignedTherapistId());

        AppointmentTherapyResponse response = appointmentTherapyMapper.toResponse(appointmentTherapy, therapist);

        List<UUID> therapyIds = appointmentTherapyRecommendationRepository
                .findByAppointmentTherapyId(appointmentTherapy.getId())
                .stream()
                .map(AppointmentTherapyRecommendation::getTherapyMasterId)
                .toList();

        response.setTherapyIds(therapyIds);

        return ApiResponse.success(response);
    }

    private TherapistSummaryResponse fetchTherapist(UUID therapistId) {
        ApiResponse<TherapistSummaryResponse> therapistResponse =
                therapistServiceClient.getTherapistById(therapistId);
        if (therapistResponse == null || !therapistResponse.isSuccess() || therapistResponse.getData() == null) {
            throw new ResourceNotFoundException("Therapist not found with id: " + therapistId);
        }
        return therapistResponse.getData();
    }

}
