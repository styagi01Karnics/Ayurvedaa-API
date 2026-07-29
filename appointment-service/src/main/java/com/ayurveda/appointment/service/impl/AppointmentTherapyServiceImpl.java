package com.ayurveda.appointment.service.impl;

import java.time.LocalDate;
import java.util.List;
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
            throw new ResourceNotFoundException(
                    "Therapy details not found for patient: " + patientId);
        }

        PatientSummaryResponse patient = patientServiceClient
                .getPatientById(patientId)
                .getData();

        List<AppointmentTherapyResponse> responses = appointmentTherapies.stream()
                .map(therapy -> {

                    TherapistSummaryResponse therapist =
                            fetchTherapist(therapy.getAssignedTherapistId());

                    AppointmentTherapyResponse response =
                            appointmentTherapyMapper.toResponse(therapy, therapist);

                    response.setPatient(patient);

                    List<TherapyResponse> therapies =
                            appointmentTherapyRecommendationRepository
                                    .findByAppointmentTherapyId(therapy.getId())
                                    .stream()
                                    .map(AppointmentTherapyRecommendation::getTherapyMasterId)
                                    .map(id -> therapyRepository.findById(id)
                                            .orElseThrow(() -> new ResourceNotFoundException(
                                                    "Therapy not found: " + id)))
                                    .map(this::mapTherapy)
                                    .toList();

                    response.setTherapies(therapies);

                    TreatmentCategoryMaster category =
                            treatmentCategoryRepository
                                    .findById(therapy.getTreatmentCategoryId())
                                    .orElseThrow(() -> new ResourceNotFoundException(
                                            "Treatment category not found"));

                    response.setTreatmentCategory(mapTreatmentCategory(category));

                    return response;
                })
                .toList();

        return ApiResponse.success(responses);
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
                    PatientSummaryResponse patient = patientServiceClient
                            .getPatientById(therapy.getPatientId())
                            .getData();

                    List<String> therapyNames =
                            appointmentTherapyRecommendationRepository
                                    .findByAppointmentTherapyId(therapy.getId())
                                    .stream()
                                    .map(AppointmentTherapyRecommendation::getTherapyMasterId)
                                    .map(id -> therapyRepository.findById(id)
                                            .map(TherapyMaster::getTherapyName)
                                            .orElse("Unknown"))
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

        return ApiResponse.success(Constants.THERAPIST_TODAY_SCHEDULE_FETCHED, response);
    }

    private TherapyResponse mapTherapy(TherapyMaster therapy) {

        return TherapyResponse.builder()
                .id(therapy.getId())
                .name(therapy.getTherapyName())
                .therapyCode(therapy.getTherapyCode())
                .therapyName(therapy.getTherapyName())
                .description(therapy.getDescription())
                .categoryId(therapy.getCategoryId())
                .status(therapy.getStatus())
                .durationMinutes(therapy.getDurationMinutes())
                .price(therapy.getPrice())
                .build();
    }
    
    private TherapistSummaryResponse fetchTherapist(UUID therapistId) {

        return therapistServiceClient
                .getTherapistById(therapistId)
                .getData();
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
