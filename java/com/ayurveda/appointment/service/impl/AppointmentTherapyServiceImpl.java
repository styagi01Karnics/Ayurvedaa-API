package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentTherapyRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;
import com.ayurveda.appointment.entity.AppointmentTherapy;
import com.ayurveda.appointment.entity.AppointmentTherapyRecommendation;
import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.AppointmentTherapyMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentTherapyRecommendationRepository;
import com.ayurveda.appointment.repository.AppointmentTherapyRepository;
import com.ayurveda.appointment.repository.DoctorMasterRepository;
import com.ayurveda.appointment.repository.TherapyRepository;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.service.AppointmentTherapyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentTherapyServiceImpl
        implements AppointmentTherapyService {

    private final AppointmentTherapyRepository appointmentTherapyRepository;

    private final AppointmentTherapyRecommendationRepository
            appointmentTherapyRecommendationRepository;

    private final AppointmentBookingRepository appointmentBookingRepository;

    private final TreatmentCategoryRepository treatmentCategoryRepository;

    private final TherapyRepository therapyRepository;

    // Temporary Therapist Validation
    private final DoctorMasterRepository doctorMasterRepository;

    private final AppointmentTherapyMapper appointmentTherapyMapper;
    
    @Override
    public ApiResponse<AppointmentTherapyResponse> createAppointmentTherapy(
            CreateAppointmentTherapyRequest request) {

        log.info("Creating therapy details for booking : {}",
                request.getBookingId());

        // Validate Booking
        appointmentBookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id : "
                                + request.getBookingId()));

        // Validate Treatment Category
        treatmentCategoryRepository.findById(request.getTreatmentCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Treatment category not found with id : "
                                + request.getTreatmentCategoryId()));

        // Validate Assigned Doctor (Temporary Therapist)
        doctorMasterRepository.findById(request.getAssignedTherapistId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id : "
                                + request.getAssignedTherapistId()));

        // Validate all Recommended Therapies
        for (UUID therapyId : request.getTherapyIds()) {

            therapyRepository.findById(therapyId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Therapy not found with id : " + therapyId));
        }

        // Save Appointment Therapy
        AppointmentTherapy appointmentTherapy =
                appointmentTherapyMapper.toEntity(request);

        AppointmentTherapy savedTherapy =
                appointmentTherapyRepository.save(appointmentTherapy);

        // Save Recommended Therapies
        for (UUID therapyId : request.getTherapyIds()) {

            AppointmentTherapyRecommendation recommendation =
                    AppointmentTherapyRecommendation.builder()
                            .appointmentTherapyId(savedTherapy.getId())
                            .therapyMasterId(therapyId)
                            .build();

            appointmentTherapyRecommendationRepository.save(recommendation);
        }

        AppointmentTherapyResponse response =
                appointmentTherapyMapper.toResponse(savedTherapy);

        response.setTherapyIds(request.getTherapyIds());

        log.info("Appointment therapy created successfully with id : {}",
                savedTherapy.getId());

        return ApiResponse.success(
                "Appointment therapy created successfully",
                response);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentTherapyResponse> getAppointmentTherapyByBookingId(
            UUID bookingId) {

        log.info("Fetching therapy details for booking : {}", bookingId);

        AppointmentTherapy appointmentTherapy =
                appointmentTherapyRepository.findByBookingId(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Therapy details not found for booking : "
                                        + bookingId));

        AppointmentTherapyResponse response =
                appointmentTherapyMapper.toResponse(appointmentTherapy);

        List<UUID> therapyIds =
                appointmentTherapyRecommendationRepository
                        .findByAppointmentTherapyId(appointmentTherapy.getId())
                        .stream()
                        .map(AppointmentTherapyRecommendation::getTherapyMasterId)
                        .toList();

        response.setTherapyIds(therapyIds);

        return ApiResponse.success(response);
    }

}