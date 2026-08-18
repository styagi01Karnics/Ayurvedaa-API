package com.ayurveda.appointment.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.client.PatientServiceClient;
import com.ayurveda.appointment.client.TherapistServiceClient;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.util.AppMessages;
import com.ayurveda.appointment.dto.request.CreateAppointmentTherapyRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;
import com.ayurveda.appointment.dto.response.PatientSummaryResponse;
import com.ayurveda.appointment.dto.response.TherapistSummaryResponse;
import com.ayurveda.appointment.dto.response.TherapistTodayScheduleResponse;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;
import com.ayurveda.appointment.entity.AppointmentTherapy;
import com.ayurveda.appointment.entity.AppointmentTherapyRecommendation;
import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.enums.TherapyStatus;
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
    private final PatientServiceClient patientServiceClient;

    @Override
    public ApiResponse<AppointmentTherapyResponse> createAppointmentTherapy(
            CreateAppointmentTherapyRequest request) {

        log.info("Creating therapy details for patient: {}", request.getPatientId());

        if (!appointmentBookingRepository.existsByPatientId(request.getPatientId())) {
            throw new ResourceNotFoundException(
                    Constants.APPOINTMENT_NOT_FOUND_FOR_PATIENT + request.getPatientId());
        }

        treatmentCategoryRepository.findById(request.getTreatmentCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.TREATMENT_CATEGORY_NOT_FOUND_WITH_ID + request.getTreatmentCategoryId()));

        TherapistSummaryResponse therapist = fetchTherapist(request.getAssignedTherapistId());

        for (UUID therapyId : request.getTherapyIds()) {
            therapyRepository.findById(therapyId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            AppMessages.THERAPY_NOT_FOUND_WITH_ID + therapyId));
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
        List<TherapyResponse> therapies = request.getTherapyIds()
                .stream()
                .map(id -> therapyRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                AppMessages.THERAPY_NOT_FOUND_WITH_ID + id)))
                .map(this::mapTherapy)
                .toList();

        response.setTherapies(therapies);

        PatientSummaryResponse patient = patientServiceClient
                .getPatientById(request.getPatientId())
                .getData();

        response.setPatient(patient);
        
        TreatmentCategoryMaster category = treatmentCategoryRepository
                .findById(savedTherapy.getTreatmentCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        Constants.TREATMENT_CATEGORY_NOT_FOUND));

        response.setTreatmentCategory(mapTreatmentCategory(category));

        log.info("Appointment therapy created successfully with id: {}", savedTherapy.getId());

        return ApiResponse.success(Constants.APPOINTMENT_THERAPY_CREATED, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentTherapyResponse>> getAppointmentTherapyByPatientId(UUID patientId) {

        log.info("Fetching therapy details for patient: {}", patientId);

        List<AppointmentTherapy> appointmentTherapies =
                appointmentTherapyRepository.findAllByPatientId(patientId);

        if (appointmentTherapies.isEmpty()) {
            log.info("No therapy details found for patient: {}", patientId);
            return ApiResponse.success(
                    Constants.APPOINTMENT_THERAPY_NO_RECORDS, List.of());
        }

        PatientSummaryResponse loadedPatient = null;
        try {
            loadedPatient = patientServiceClient.getPatientById(patientId).getData();
        } catch (Exception ex) {
            log.warn("Patient unavailable for appointment therapy list {}: {}", patientId, ex.getMessage());
        }
        final PatientSummaryResponse patient = loadedPatient;

        List<AppointmentTherapyResponse> responses = appointmentTherapies.stream()
                .map(therapy -> {

                    TherapistSummaryResponse therapist = null;
                    try {
                        therapist = fetchTherapist(therapy.getAssignedTherapistId());
                    } catch (Exception ex) {
                        log.warn("Therapist unavailable for appointment therapy {}: {} ({})",
                                therapy.getId(), therapy.getAssignedTherapistId(), ex.getMessage());
                    }

                    AppointmentTherapyResponse response =
                            appointmentTherapyMapper.toResponse(therapy, therapist);

                    response.setPatient(patient);

                    List<TherapyResponse> therapies =
                            appointmentTherapyRecommendationRepository
                                    .findByAppointmentTherapyId(therapy.getId())
                                    .stream()
                                    .map(AppointmentTherapyRecommendation::getTherapyMasterId)
                                    .map(id -> therapyRepository.findById(id).orElse(null))
                                    .filter(Objects::nonNull)
                                    .map(this::mapTherapy)
                                    .toList();

                    response.setTherapies(therapies);

                    TreatmentCategoryMaster category =
                            treatmentCategoryRepository
                                    .findById(therapy.getTreatmentCategoryId())
                                    .orElse(null);

                    if (category != null) {
                        response.setTreatmentCategory(mapTreatmentCategory(category));
                    }

                    return response;
                })
                .toList();

        log.info("Therapy details fetched successfully for patient: {} (count: {})",
                patientId, responses.size());

        return ApiResponse.success(Constants.APPOINTMENT_THERAPY_FETCHED, responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TherapistTodayScheduleResponse> getTherapistTodaySchedule(UUID therapistId) {
        LocalDate today = LocalDate.now();
        log.info("Fetching today's schedule for therapist {} on {}", therapistId, today);

        fetchTherapist(therapistId);

        List<AppointmentTherapy> therapies =
                appointmentTherapyRepository.findByTherapistAndDateExcludingCancelled(
                        therapistId, today, TherapyStatus.CANCELLED);

        List<TherapistTodayScheduleResponse.TherapistTodaySlotResponse> slots = therapies.stream()
                .map(therapy -> {
                    PatientSummaryResponse patient = null;
                    try {
                        patient = patientServiceClient
                                .getPatientById(therapy.getPatientId())
                                .getData();
                    } catch (Exception ex) {
                        log.warn("Patient unavailable for therapist schedule therapy {}: {}",
                                therapy.getId(), ex.getMessage());
                    }

                    List<String> therapyNames =
                            appointmentTherapyRecommendationRepository
                                    .findByAppointmentTherapyId(therapy.getId())
                                    .stream()
                                    .map(AppointmentTherapyRecommendation::getTherapyMasterId)
                                    .map(id -> therapyRepository.findById(id)
                                            .map(TherapyMaster::getTherapyName)
                                            .orElse(null))
                                    .filter(Objects::nonNull)
                                    .toList();

                    String categoryName = treatmentCategoryRepository
                            .findById(therapy.getTreatmentCategoryId())
                            .map(TreatmentCategoryMaster::getCategoryName)
                            .orElse(null);

                    return TherapistTodayScheduleResponse.TherapistTodaySlotResponse.builder()
                            .appointmentTherapyId(therapy.getId())
                            .scheduleTime(therapy.getScheduleTime())
                            .sessionDuration(therapy.getSessionDuration())
                            .therapyStatus(therapy.getTherapyStatus())
                            .patientId(therapy.getPatientId())
                            .patientName(patient != null ? patient.getFullName() : null)
                            .patientMobileNumber(patient != null ? patient.getMobileNumber() : null)
                            .therapies(therapyNames)
                            .treatmentCategoryName(categoryName)
                            .build();
                })
                .toList();

        TherapistTodayScheduleResponse response = TherapistTodayScheduleResponse.builder()
                .therapistId(therapistId)
                .date(today)
                .totalSlots(slots.size())
                .slots(slots)
                .build();

        log.info("Therapist today's schedule fetched successfully for therapist {} (slots: {})",
                therapistId, slots.size());

        return ApiResponse.success(Constants.THERAPIST_TODAY_SCHEDULE_FETCHED, response);
    }

    private TherapyResponse mapTherapy(TherapyMaster therapy) {
        String categoryName = therapy.getCategoryId() == null
                ? null
                : treatmentCategoryRepository.findById(therapy.getCategoryId())
                        .map(TreatmentCategoryMaster::getCategoryName)
                        .orElse(null);

        return TherapyResponse.builder()
                .id(therapy.getId())
                .name(therapy.getTherapyName())
                .therapyCode(therapy.getTherapyCode())
                .therapyName(therapy.getTherapyName())
                .description(therapy.getDescription())
                .categoryId(therapy.getCategoryId())
                .categoryName(categoryName)
                .status(therapy.getStatus())
                .durationMinutes(therapy.getDurationMinutes())
                .price(therapy.getPrice())
                .build();
    }
    
    private TherapistSummaryResponse fetchTherapist(UUID therapistId) {
        try {
            TherapistSummaryResponse therapist = therapistServiceClient
                    .getTherapistById(therapistId)
                    .getData();

            if (therapist == null) {
                throw new ResourceNotFoundException(AppMessages.THERAPIST_NOT_FOUND_WITH_ID + therapistId);
            }

            // Return only fields aligned with current therapist master (no legacy nulls).
            return TherapistSummaryResponse.builder()
                    .id(therapist.getId())
                    .name(therapist.getName() != null ? therapist.getName() : therapist.getTherapistName())
                    .therapistName(therapist.getTherapistName())
                    .therapistCode(therapist.getTherapistCode())
                    .status(therapist.getStatus())
                    .build();
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (feign.FeignException.NotFound ex) {
            throw new ResourceNotFoundException(AppMessages.THERAPIST_NOT_FOUND_WITH_ID + therapistId);
        }
    }
    
    private TreatmentCategoryResponse mapTreatmentCategory(TreatmentCategoryMaster category) {

        return TreatmentCategoryResponse.builder()
                .id(category.getId())
                .categoryCode(category.getCategoryCode())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .status(category.getStatus())
                .build();
    }

}
