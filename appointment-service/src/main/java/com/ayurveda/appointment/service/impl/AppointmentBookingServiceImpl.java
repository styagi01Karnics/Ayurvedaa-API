package com.ayurveda.appointment.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.appointment.client.DoctorServiceClient;
import com.ayurveda.appointment.client.PatientServiceClient;
import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.request.CreatePatientClientRequest;
import com.ayurveda.appointment.dto.request.RescheduleAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.dto.response.AppointmentStatsResponse;
import com.ayurveda.appointment.dto.response.DashboardTodaysScheduleResponse;
import com.ayurveda.appointment.dto.response.DoctorSummaryResponse;
import com.ayurveda.appointment.dto.response.DoctorTodayScheduleResponse;
import com.ayurveda.appointment.dto.response.PatientAppointmentListItemResponse;
import com.ayurveda.appointment.dto.response.PatientSummaryResponse;
import com.ayurveda.appointment.entity.AppointmentAyurvedicAssessment;
import com.ayurveda.appointment.entity.AppointmentBooking;
import com.ayurveda.appointment.entity.AppointmentConsultationType;
import com.ayurveda.appointment.entity.AppointmentTherapy;
import com.ayurveda.appointment.entity.DoshaMaster;
import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.ConsultationType;
import com.ayurveda.appointment.enums.PatientListTab;
import com.ayurveda.appointment.mapper.AppointmentBookingMapper;
import com.ayurveda.appointment.repository.AppointmentAyurvedicAssessmentRepository;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentConsultationTypeRepository;
import com.ayurveda.appointment.repository.AppointmentTherapyRepository;
import com.ayurveda.appointment.repository.DoshaMasterRepository;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.service.AppointmentBookingService;
import com.ayurveda.appointment.util.AppMessages;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.validation.IdProofValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentBookingServiceImpl implements AppointmentBookingService {

    private final AppointmentBookingRepository appointmentBookingRepository;
    private final AppointmentBookingMapper appointmentBookingMapper;
    private final AppointmentConsultationTypeRepository appointmentConsultationTypeRepository;
    private final AppointmentTherapyRepository appointmentTherapyRepository;
    private final TreatmentCategoryRepository treatmentCategoryRepository;
    private final AppointmentAyurvedicAssessmentRepository appointmentAyurvedicAssessmentRepository;
    private final DoshaMasterRepository doshaMasterRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;

    @Override
    @Transactional
    public ApiResponse<AppointmentBookingResponse> createAppointment(
            CreateAppointmentBookingRequest request) {

        log.info("Starting appointment booking for patient: {}",
                request.getPatient().getFullName());

        IdProofValidator.validate(
                request.getPatient().getIdProofType(),
                request.getPatient().getIdProofNumber());

        ApiResponse<PatientSummaryResponse> patientResponse =
                patientServiceClient.createPatient(
                        CreatePatientClientRequest.from(request.getPatient()));

        if (patientResponse == null
                || !patientResponse.isSuccess()
                || patientResponse.getData() == null) {

            String message = patientResponse != null && StringUtils.hasText(patientResponse.getMessage())
                    ? patientResponse.getMessage()
                    : AppConstants.UNABLE_TO_CREATE_PATIENT;
            throw new BadRequestException(message);
        }

        PatientSummaryResponse patient = patientResponse.getData();

        DoctorSummaryResponse doctor =
                fetchDoctor(request.getAssignedDoctorId());

        ensureDoctorSlotAvailable(
                request.getAssignedDoctorId(),
                request.getRegistrationDate(),
                request.getSlotTime(),
                null);

        AppointmentBooking appointment =
                appointmentBookingMapper.toEntity(request, patient.getId());

        appointment.setBookingStatus(BookingStatus.SCHEDULED);

        AppointmentBooking savedAppointment =
                appointmentBookingRepository.save(appointment);

        request.getConsultationTypes().forEach(type -> {

            AppointmentConsultationType consultation =
                    AppointmentConsultationType.builder()
                            .bookingId(savedAppointment.getId())
                            .consultationType(ConsultationType.valueOf(type))
                            .build();

            appointmentConsultationTypeRepository.save(consultation);
        });

        log.info("Appointment created successfully with id: {}",
                savedAppointment.getId());

        AppointmentBookingResponse response =
                appointmentBookingMapper.toResponse(
                        savedAppointment,
                        patient,
                        doctor);

        response.setConsultationTypes(request.getConsultationTypes());

        return ApiResponse.success(AppMessages.PATIENT_CREATED_AND_APPOINTMENT_BOOKED, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentBookingResponse> getAppointmentById(UUID bookingId) {

        log.info("Fetching appointment with id: {}", bookingId);

        AppointmentBooking appointment = findActiveBooking(bookingId);

        return ApiResponse.success(toResponse(appointment));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PatientAppointmentListItemResponse>> getPatientList(
            PatientListTab statusTab,
            String search,
            BookingStatus bookingStatus,
            ConsultationType consultationType,
            UUID doshaId,
            UUID doctorId) {

        log.info("Fetching {} patient list. status={}, visitType={}, doshaId={}, doctorId={}, search={}",
                statusTab, bookingStatus, consultationType, doshaId, doctorId, search);

        Set<BookingStatus> statuses = resolvePatientListStatuses(statusTab, bookingStatus);

        List<AppointmentBooking> bookings = appointmentBookingRepository.findPatientList(
                statuses, doctorId, consultationType, doshaId);

        if (bookings.isEmpty()) {
            return ApiResponse.success(AppMessages.PATIENT_LIST_FETCHED, List.of());
        }

        Map<UUID, List<String>> consultationTypesByBooking = loadConsultationTypes(bookings);
        Map<UUID, AppointmentAyurvedicAssessment> assessmentByPatient = loadAssessments(bookings);
        Map<UUID, String> doshaNameById = loadDoshaNames(assessmentByPatient.values());
        Map<UUID, PatientSummaryResponse> patientsById = new HashMap<>();
        Map<UUID, DoctorSummaryResponse> doctorsById = new HashMap<>();

        List<PatientAppointmentListItemResponse> items = new ArrayList<>();
        for (AppointmentBooking booking : bookings) {
            PatientSummaryResponse patient = patientsById.computeIfAbsent(
                    booking.getPatientId(), this::fetchPatientQuietly);
            DoctorSummaryResponse doctor = doctorsById.computeIfAbsent(
                    booking.getAssignedDoctorId(), this::fetchDoctorQuietly);

            if (!matchesPatientSearch(patient, search)) {
                continue;
            }

            AppointmentAyurvedicAssessment assessment =
                    assessmentByPatient.get(booking.getPatientId());

            items.add(PatientAppointmentListItemResponse.builder()
                    .bookingId(booking.getId())
                    .patientId(booking.getPatientId())
                    .patientDisplayId(patient != null ? patient.getPatientDisplayId() : null)
                    .patientCode(patient != null ? patient.getPatientCode() : null)
                    .patientFullName(patient != null ? patient.getFullName() : null)
                    .patientMobileNumber(patient != null ? patient.getMobileNumber() : null)
                    .assignedDoctorId(booking.getAssignedDoctorId())
                    .doctorName(doctor != null ? doctor.getDoctorName() : null)
                    .consultationTypes(consultationTypesByBooking.getOrDefault(booking.getId(), List.of()))
                    .appointmentDate(booking.getRegistrationDate())
                    .slotTime(booking.getSlotTime())
                    .bookingTime(resolveBookingDateTime(booking))
                    .doshaId(assessment != null ? assessment.getDoshaId() : null)
                    .doshaName(assessment != null
                            ? doshaNameById.get(assessment.getDoshaId())
                            : null)
                    .bookingStatus(booking.getBookingStatus())
                    .build());
        }

        return ApiResponse.success(AppMessages.PATIENT_LIST_FETCHED, items);
    }

    private Set<BookingStatus> resolvePatientListStatuses(
            PatientListTab statusTab, BookingStatus bookingStatus) {

        Set<BookingStatus> tabStatuses = statusTab.getBookingStatuses();
        if (bookingStatus == null) {
            return tabStatuses;
        }
        if (!tabStatuses.contains(bookingStatus)) {
            throw new BadRequestException(AppMessages.INVALID_PATIENT_LIST_STATUS);
        }
        return EnumSet.of(bookingStatus);
    }

    private Map<UUID, List<String>> loadConsultationTypes(List<AppointmentBooking> bookings) {
        Set<UUID> bookingIds = bookings.stream()
                .map(AppointmentBooking::getId)
                .collect(Collectors.toSet());

        return appointmentConsultationTypeRepository.findByBookingIdIn(bookingIds).stream()
                .collect(Collectors.groupingBy(
                        AppointmentConsultationType::getBookingId,
                        Collectors.mapping(
                                type -> type.getConsultationType().name(),
                                Collectors.toList())));
    }

    private Map<UUID, AppointmentAyurvedicAssessment> loadAssessments(List<AppointmentBooking> bookings) {
        Set<UUID> patientIds = bookings.stream()
                .map(AppointmentBooking::getPatientId)
                .collect(Collectors.toSet());

        return appointmentAyurvedicAssessmentRepository
                .findByPatientIdInAndDeletedFalse(patientIds)
                .stream()
                .collect(Collectors.toMap(
                        AppointmentAyurvedicAssessment::getPatientId,
                        Function.identity(),
                        (left, right) -> left));
    }

    private Map<UUID, String> loadDoshaNames(Collection<AppointmentAyurvedicAssessment> assessments) {
        Set<UUID> doshaIds = assessments.stream()
                .map(AppointmentAyurvedicAssessment::getDoshaId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (doshaIds.isEmpty()) {
            return Map.of();
        }

        Map<UUID, String> names = new HashMap<>();
        for (UUID doshaId : doshaIds) {
            doshaMasterRepository.findByIdAndDeletedFalse(doshaId)
                    .map(DoshaMaster::getName)
                    .ifPresent(name -> names.put(doshaId, name));
        }
        return names;
    }

    private boolean matchesPatientSearch(PatientSummaryResponse patient, String search) {
        if (!StringUtils.hasText(search)) {
            return true;
        }
        if (patient == null) {
            return false;
        }

        String term = search.trim().toLowerCase(Locale.ROOT).replace("#", "");
        return containsIgnoreCase(patient.getPatientDisplayId(), term)
                || containsIgnoreCase(patient.getPatientCode(), term)
                || containsIgnoreCase(patient.getFullName(), term)
                || containsIgnoreCase(patient.getMobileNumber(), term);
    }

    private boolean containsIgnoreCase(String value, String term) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        return value.toLowerCase(Locale.ROOT).replace("#", "").contains(term);
    }

    private PatientSummaryResponse fetchPatientQuietly(UUID patientId) {
        try {
            return fetchPatient(patientId);
        } catch (Exception ex) {
            log.warn("Unable to load patient {}: {}", patientId, ex.getMessage());
            return null;
        }
    }

    private DoctorSummaryResponse fetchDoctorQuietly(UUID doctorId) {
        try {
            return fetchDoctor(doctorId);
        } catch (Exception ex) {
            log.warn("Unable to load doctor {}: {}", doctorId, ex.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentBookingResponse>> getAppointmentsByPatientId(UUID patientId) {

        log.info("Fetching appointments for patient id: {}", patientId);

        List<AppointmentBooking> appointments =
                appointmentBookingRepository.findByPatientId(patientId);

        if (appointments.isEmpty()) {
            throw new ResourceNotFoundException(AppMessages.NO_APPOINTMENTS_FOR_PATIENT + patientId);
        }

        PatientSummaryResponse patient = fetchPatient(patientId);

        List<AppointmentBookingResponse> responses = appointments.stream().map(appointment -> {

            DoctorSummaryResponse doctor =
                    fetchDoctor(appointment.getAssignedDoctorId());

            AppointmentBookingResponse response =
                    appointmentBookingMapper.toResponse(appointment, patient, doctor);

            List<String> consultationTypes =
                    appointmentConsultationTypeRepository.findByBookingId(appointment.getId())
                            .stream()
                            .map(consultation -> consultation.getConsultationType().name())
                            .toList();

            response.setConsultationTypes(consultationTypes);

            return response;

        }).toList();

        return ApiResponse.success(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentBookingResponse>> getAppointmentsByBookingStatus(
            BookingStatus bookingStatus) {

        log.info("Fetching appointments for booking status: {}", bookingStatus);

        List<AppointmentBooking> appointments =
                appointmentBookingRepository.findByBookingStatusAndDeletedFalse(bookingStatus);

        if (appointments.isEmpty()) {
            throw new ResourceNotFoundException(AppMessages.NO_APPOINTMENTS_FOR_STATUS + bookingStatus);
        }

        List<AppointmentBookingResponse> responses = appointments.stream()
                .map(this::toResponse)
                .toList();

        return ApiResponse.success(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentBookingResponse>> getAppointmentsByDate(
            LocalDate registrationDate) {

        log.info("Fetching appointments for date: {}", registrationDate);

        List<AppointmentBooking> appointments =
                appointmentBookingRepository.findByRegistrationDateAndDeletedFalse(registrationDate);

        if (appointments.isEmpty()) {
            throw new ResourceNotFoundException(AppMessages.NO_APPOINTMENTS_FOR_DATE + registrationDate);
        }

        List<AppointmentBookingResponse> responses = appointments.stream()
                .map(this::toResponse)
                .toList();

        return ApiResponse.success(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentStatsResponse> getAppointmentStats() {

        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();
        LocalDate today = LocalDate.now();

        log.info("Fetching appointment stats for month {} and date {}", currentMonth, today);

        long currentMonthCount = appointmentBookingRepository
                .countByRegistrationDateBetweenAndDeletedFalseAndBookingStatusNot(
                        monthStart, monthEnd, BookingStatus.CANCELLED);

        long completedCount = appointmentBookingRepository
                .countByRegistrationDateBetweenAndDeletedFalseAndBookingStatus(
                        monthStart, monthEnd, BookingStatus.COMPLETED);

        long ongoingCount = currentMonthCount - completedCount;
        if (ongoingCount < 0) {
            ongoingCount = 0;
        }

        long todayCount = appointmentBookingRepository
                .countByRegistrationDateAndDeletedFalseAndBookingStatusNot(
                        today, BookingStatus.CANCELLED);

        AppointmentStatsResponse stats = AppointmentStatsResponse.builder()
                .currentMonthAppointmentCount(currentMonthCount)
                .completedCount(completedCount)
                .ongoingCount(ongoingCount)
                .todayAppointmentCount(todayCount)
                .build();

        return ApiResponse.success(AppMessages.APPOINTMENT_STATS_FETCHED, stats);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentBookingResponse>> getCancelledAppointments() {

        log.info("Fetching cancelled appointments with patient details");

        List<AppointmentBooking> appointments =
                appointmentBookingRepository.findByBookingStatusAndDeletedFalse(
                        BookingStatus.CANCELLED);

        List<AppointmentBookingResponse> responses = appointments.stream()
                .map(this::toResponse)
                .toList();

        return ApiResponse.success(AppMessages.CANCELLED_APPOINTMENTS_FETCHED, responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentBookingResponse>> getTodayAppointmentsByConsultationType(
            ConsultationType consultationType) {

        LocalDate today = LocalDate.now();
        log.info("Fetching today's appointments for consultation type: {}", consultationType);

        List<AppointmentBooking> appointments =
                appointmentBookingRepository.findByDateAndConsultationType(
                        today, consultationType, BookingStatus.CANCELLED);

        List<AppointmentBookingResponse> responses = appointments.stream()
                .map(this::toResponse)
                .toList();

        return ApiResponse.success(
                AppMessages.TODAY_APPOINTMENTS_BY_CONSULTATION_TYPE_FETCHED + consultationType + ".",
                responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DoctorTodayScheduleResponse> getDoctorTodaySchedule(UUID doctorId) {
        LocalDate today = LocalDate.now();
        log.info("Fetching today's schedule for doctor {} on {}", doctorId, today);

        // Ensure doctor exists
        fetchDoctor(doctorId);

        List<AppointmentBooking> appointmentsList =
                appointmentBookingRepository.findByDoctorAndDateExcludingCancelled(
                        doctorId, today, BookingStatus.CANCELLED);

        List<DoctorTodayScheduleResponse.DoctorTodayAppointmentResponse> appointments =
                appointmentsList.stream()
                .map(appointment -> {
                    PatientSummaryResponse patient = fetchPatient(appointment.getPatientId());

                    List<String> consultationTypes =
                            appointmentConsultationTypeRepository.findByBookingId(appointment.getId())
                                    .stream()
                                    .map(c -> c.getConsultationType().name())
                                    .toList();

                    return DoctorTodayScheduleResponse.DoctorTodayAppointmentResponse.builder()
                            .bookingId(appointment.getId())
                            .slotTime(appointment.getSlotTime())
                            .bookingTime(resolveBookingDateTime(appointment))
                            .bookingStatus(appointment.getBookingStatus())
                            .patientId(appointment.getPatientId())
                            .patientName(patient.getFullName())
                            .patientMobileNumber(patient.getMobileNumber())
                            .consultationTypes(consultationTypes)
                            .build();
                })
                .toList();

        DoctorTodayScheduleResponse response = DoctorTodayScheduleResponse.builder()
                .doctorId(doctorId)
                .date(today)
                .totalAppointments(appointments.size())
                .appointments(appointments)
                .build();

        return ApiResponse.success(AppMessages.DOCTOR_TODAY_APPOINTMENTS_FETCHED, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DashboardTodaysScheduleResponse> getDashboardTodaysSchedule(UUID doctorId) {
        LocalDate today = LocalDate.now();
        log.info("Fetching dashboard today's schedule for date {}, doctorId={}", today, doctorId);

        if (doctorId != null) {
            fetchDoctor(doctorId);
        }

        List<AppointmentBooking> todayAppointments =
                appointmentBookingRepository.findTodaySchedule(today, BookingStatus.CANCELLED, doctorId);

        AppointmentBooking ongoing = todayAppointments.stream()
                .filter(a -> a.getBookingStatus() == BookingStatus.IN_CONSULTATION)
                .findFirst()
                .orElse(null);

        AppointmentBooking next = todayAppointments.stream()
                .filter(a -> a.getBookingStatus() == BookingStatus.SCHEDULED
                        || a.getBookingStatus() == BookingStatus.RESCHEDULED)
                .filter(a -> ongoing == null || !a.getId().equals(ongoing.getId()))
                .min(Comparator.comparing(AppointmentBooking::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        long remainingToday = todayAppointments.stream()
                .filter(a -> a.getBookingStatus() == BookingStatus.SCHEDULED
                        || a.getBookingStatus() == BookingStatus.RESCHEDULED
                        || a.getBookingStatus() == BookingStatus.IN_CONSULTATION)
                .count();

        DashboardTodaysScheduleResponse response = DashboardTodaysScheduleResponse.builder()
                .date(today)
                .currentDateTime(LocalDateTime.now())
                .ongoingAppointment(ongoing != null ? toScheduleItem(ongoing) : null)
                .nextAppointment(next != null ? toScheduleItem(next) : null)
                .remainingToday(remainingToday)
                .build();

        return ApiResponse.success(AppMessages.DASHBOARD_TODAY_SCHEDULE_FETCHED, response);
    }

    private DashboardTodaysScheduleResponse.ScheduleItemResponse toScheduleItem(AppointmentBooking appointment) {
        PatientSummaryResponse patient = fetchPatient(appointment.getPatientId());
        return DashboardTodaysScheduleResponse.ScheduleItemResponse.builder()
                .bookingId(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(patient.getFullName())
                .serviceType(resolveServiceType(appointment))
                .bookingStatus(appointment.getBookingStatus())
                .build();
    }

    private String resolveServiceType(AppointmentBooking appointment) {
        String categoryName = resolveTreatmentCategoryName(appointment.getPatientId());
        List<String> consultationTypes =
                appointmentConsultationTypeRepository.findByBookingId(appointment.getId())
                        .stream()
                        .map(AppointmentConsultationType::getConsultationType)
                        .map(this::formatConsultationType)
                        .toList();

        if (StringUtils.hasText(categoryName) && !consultationTypes.isEmpty()) {
            return categoryName + " " + consultationTypes.get(0);
        }
        if (StringUtils.hasText(categoryName)) {
            return categoryName;
        }
        if (!consultationTypes.isEmpty()) {
            return String.join(" / ", consultationTypes);
        }
        return null;
    }

    private String resolveTreatmentCategoryName(UUID patientId) {
        List<AppointmentTherapy> therapies = appointmentTherapyRepository.findAllByPatientId(patientId);
        if (therapies == null || therapies.isEmpty()) {
            return null;
        }

        return therapies.stream()
                .filter(t -> !Boolean.TRUE.equals(t.getDeleted()))
                .sorted(Comparator.comparing(AppointmentTherapy::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(AppointmentTherapy::getTreatmentCategoryId)
                .map(treatmentCategoryRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(TreatmentCategoryMaster::getCategoryName)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private String formatConsultationType(ConsultationType type) {
        if (type == null) {
            return null;
        }
        String lower = type.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    @Override
    public ApiResponse<AppointmentBookingResponse> rescheduleAppointment(
            UUID bookingId, RescheduleAppointmentBookingRequest request) {

        log.info("Rescheduling appointment with id: {}", bookingId);

        AppointmentBooking appointment = findActiveBooking(bookingId);

        BookingStatus currentStatus = appointment.getBookingStatus();
        if (currentStatus == BookingStatus.CANCELLED
                || currentStatus == BookingStatus.COMPLETED
                || currentStatus == BookingStatus.IN_CONSULTATION) {
            throw new BadRequestException(
                    AppMessages.APPOINTMENT_CANNOT_RESCHEDULE_FROM_STATUS + currentStatus);
        }

        if (!appointment.getPatientId().equals(request.getPatientId())) {
            throw new BadRequestException(AppMessages.PATIENT_ID_MISMATCH);
        }

        PatientSummaryResponse patient = fetchPatient(request.getPatientId());
        DoctorSummaryResponse doctor = fetchDoctor(request.getAssignedDoctorId());

        ensureDoctorSlotAvailable(
                request.getAssignedDoctorId(),
                request.getRegistrationDate(),
                request.getSlotTime(),
                bookingId);

        appointment.setRegistrationDate(request.getRegistrationDate());
        appointment.setSlotTime(request.getSlotTime());
        appointment.setAssignedDoctorId(request.getAssignedDoctorId());
        appointment.setBookingStatus(BookingStatus.RESCHEDULED);

        AppointmentBooking saved = appointmentBookingRepository.save(appointment);

        appointmentConsultationTypeRepository.deleteByBookingId(saved.getId());
        request.getConsultationTypes().forEach(type -> {
            AppointmentConsultationType consultation =
                    AppointmentConsultationType.builder()
                            .bookingId(saved.getId())
                            .consultationType(ConsultationType.valueOf(type))
                            .build();
            appointmentConsultationTypeRepository.save(consultation);
        });

        AppointmentBookingResponse response =
                appointmentBookingMapper.toResponse(saved, patient, doctor);
        response.setConsultationTypes(request.getConsultationTypes());

        return ApiResponse.success(AppMessages.APPOINTMENT_RESCHEDULED, response);
    }

    @Override
    public ApiResponse<AppointmentBookingResponse> cancelAppointment(UUID bookingId) {
        log.info("Cancelling appointment with id: {}", bookingId);

        AppointmentBooking appointment = findActiveBooking(bookingId);
        validateCancellable(appointment);

        appointment.setBookingStatus(BookingStatus.CANCELLED);
        AppointmentBooking saved = appointmentBookingRepository.save(appointment);

        return ApiResponse.success(AppMessages.APPOINTMENT_CANCELLED, toResponse(saved));
    }

    @Override
    public ApiResponse<Void> deleteAppointment(UUID bookingId) {
        log.info("Deleting (soft) appointment with id: {}", bookingId);

        AppointmentBooking appointment = findActiveBooking(bookingId);
        validateCancellable(appointment);

        appointment.setBookingStatus(BookingStatus.CANCELLED);
        appointment.setDeleted(true);
        appointmentBookingRepository.save(appointment);

        return ApiResponse.success(AppMessages.APPOINTMENT_DELETED, null);
    }

    @Override
    public ApiResponse<AppointmentBookingResponse> markInConsultation(UUID bookingId) {
        log.info("Marking appointment as in-consultation. id: {}", bookingId);

        AppointmentBooking appointment = findActiveBooking(bookingId);

        BookingStatus currentStatus = appointment.getBookingStatus();
        if (currentStatus != BookingStatus.SCHEDULED
                && currentStatus != BookingStatus.RESCHEDULED) {
            throw new BadRequestException(
                    AppMessages.APPOINTMENT_IN_CONSULTATION_INVALID_STATUS + currentStatus);
        }

        appointment.setBookingStatus(BookingStatus.IN_CONSULTATION);
        AppointmentBooking saved = appointmentBookingRepository.save(appointment);

        return ApiResponse.success(AppMessages.APPOINTMENT_MARKED_IN_CONSULTATION, toResponse(saved));
    }

    @Override
    public ApiResponse<AppointmentBookingResponse> markCompleted(UUID bookingId) {
        log.info("Marking appointment as completed. id: {}", bookingId);

        AppointmentBooking appointment = findActiveBooking(bookingId);

        if (appointment.getBookingStatus() != BookingStatus.IN_CONSULTATION) {
            throw new BadRequestException(
                    AppMessages.APPOINTMENT_COMPLETED_INVALID_STATUS + appointment.getBookingStatus());
        }

        appointment.setBookingStatus(BookingStatus.COMPLETED);
        AppointmentBooking saved = appointmentBookingRepository.save(appointment);

        return ApiResponse.success(AppMessages.APPOINTMENT_MARKED_COMPLETED, toResponse(saved));
    }

    private void validateCancellable(AppointmentBooking appointment) {
        BookingStatus status = appointment.getBookingStatus();
        if (status == BookingStatus.CANCELLED) {
            throw new BadRequestException(AppMessages.APPOINTMENT_ALREADY_CANCELLED);
        }
        if (status == BookingStatus.COMPLETED) {
            throw new BadRequestException(AppMessages.COMPLETED_APPOINTMENTS_CANNOT_BE_CANCELLED);
        }
    }

    private AppointmentBooking findActiveBooking(UUID bookingId) {
        AppointmentBooking appointment = appointmentBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.APPOINTMENT_NOT_FOUND_WITH_ID + bookingId));

        if (Boolean.TRUE.equals(appointment.getDeleted())) {
            throw new ResourceNotFoundException(
                    AppMessages.APPOINTMENT_NOT_FOUND_WITH_ID + bookingId);
        }

        return appointment;
    }

    private AppointmentBookingResponse toResponse(AppointmentBooking appointment) {
        PatientSummaryResponse patient = fetchPatient(appointment.getPatientId());
        DoctorSummaryResponse doctor = fetchDoctor(appointment.getAssignedDoctorId());

        AppointmentBookingResponse response =
                appointmentBookingMapper.toResponse(appointment, patient, doctor);

        List<String> consultationTypes =
                appointmentConsultationTypeRepository.findByBookingId(appointment.getId())
                        .stream()
                        .map(consultation -> consultation.getConsultationType().name())
                        .toList();

        response.setConsultationTypes(consultationTypes);
        return response;
    }

    private void ensureDoctorSlotAvailable(
            UUID doctorId,
            LocalDate registrationDate,
            java.time.LocalTime slotTime,
            UUID excludeBookingId) {

        boolean conflict = appointmentBookingRepository.existsDoctorSlotConflict(
                doctorId,
                registrationDate,
                slotTime,
                BookingStatus.CANCELLED,
                excludeBookingId);

        if (conflict) {
            throw new BadRequestException(AppMessages.DOCTOR_SLOT_ALREADY_BOOKED);
        }
    }

    private LocalDateTime resolveBookingDateTime(AppointmentBooking booking) {
        if (booking.getRegistrationDate() != null && booking.getSlotTime() != null) {
            return LocalDateTime.of(booking.getRegistrationDate(), booking.getSlotTime());
        }
        return booking.getCreatedAt();
    }

    private PatientSummaryResponse fetchPatient(UUID patientId) {
        ApiResponse<PatientSummaryResponse> patientResponse = patientServiceClient.getPatientById(patientId);
        if (patientResponse == null || !patientResponse.isSuccess() || patientResponse.getData() == null) {
            throw new ResourceNotFoundException(AppConstants.PATIENT_NOT_FOUND_WITH_ID + patientId);
        }
        return patientResponse.getData();
    }

    private DoctorSummaryResponse fetchDoctor(UUID doctorId) {
        ApiResponse<DoctorSummaryResponse> doctorResponse = doctorServiceClient.getDoctorById(doctorId);
        if (doctorResponse == null || !doctorResponse.isSuccess() || doctorResponse.getData() == null) {
            throw new ResourceNotFoundException(AppConstants.DOCTOR_NOT_FOUND_WITH_ID + doctorId);
        }
        return doctorResponse.getData();
    }

}
