package com.ayurveda.appointment.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ayurveda.appointment.client.DoctorServiceClient;
import com.ayurveda.appointment.client.PatientServiceClient;
import com.ayurveda.appointment.dto.request.CreatePrescriptionRequest;
import com.ayurveda.appointment.dto.request.CreatePrescriptionRequest.PrescriptionMedicineItemRequest;
import com.ayurveda.appointment.dto.request.CreatePrescriptionRequest.PrescriptionNextFollowUpRequest;
import com.ayurveda.appointment.dto.request.CreatePrescriptionRequest.PrescriptionTherapySuggestionRequest;
import com.ayurveda.appointment.dto.request.UpdatePrescriptionRequest;
import com.ayurveda.appointment.dto.response.DoctorSummaryResponse;
import com.ayurveda.appointment.dto.response.PatientSummaryResponse;
import com.ayurveda.appointment.dto.response.PrescriptionResponse;
import com.ayurveda.appointment.dto.response.PrescriptionResponse.ConsultantDetails;
import com.ayurveda.appointment.dto.response.PrescriptionResponse.PatientDetails;
import com.ayurveda.appointment.dto.response.PrescriptionResponse.PrescriptionMedicineItemResponse;
import com.ayurveda.appointment.dto.response.PrescriptionResponse.PrescriptionNextFollowUpResponse;
import com.ayurveda.appointment.dto.response.PrescriptionResponse.PrescriptionTherapySuggestionResponse;
import com.ayurveda.appointment.dto.response.PrescriptionResponse.RecommendedTherapyItemResponse;
import com.ayurveda.appointment.dto.response.PrescriptionResponse.TreatmentDetails;
import com.ayurveda.appointment.entity.AppointmentBooking;
import com.ayurveda.appointment.entity.AppointmentConsultationType;
import com.ayurveda.appointment.entity.AppointmentLifestyleInformation;
import com.ayurveda.appointment.entity.AppointmentPhysicalExamination;
import com.ayurveda.appointment.entity.ConsultationTypeMaster;
import com.ayurveda.appointment.entity.FollowUp;
import com.ayurveda.appointment.entity.Prescription;
import com.ayurveda.appointment.entity.PrescriptionMedicine;
import com.ayurveda.appointment.entity.PrescriptionTherapySuggestion;
import com.ayurveda.appointment.entity.PrescriptionTherapySuggestionItem;
import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.entity.Treatment;
import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.enums.FollowUpStatus;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentConsultationTypeRepository;
import com.ayurveda.appointment.repository.AppointmentLifestyleInformationRepository;
import com.ayurveda.appointment.repository.AppointmentPhysicalExaminationRepository;
import com.ayurveda.appointment.repository.ConsultationTypeMasterRepository;
import com.ayurveda.appointment.repository.FollowUpRepository;
import com.ayurveda.appointment.repository.PrescriptionMedicineRepository;
import com.ayurveda.appointment.repository.PrescriptionRepository;
import com.ayurveda.appointment.repository.PrescriptionTherapySuggestionItemRepository;
import com.ayurveda.appointment.repository.PrescriptionTherapySuggestionRepository;
import com.ayurveda.appointment.repository.TherapyRepository;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.repository.TreatmentRepository;
import com.ayurveda.appointment.service.PrescriptionService;
import com.ayurveda.appointment.util.AppMessages;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMedicineRepository prescriptionMedicineRepository;
    private final PrescriptionTherapySuggestionRepository therapySuggestionRepository;
    private final PrescriptionTherapySuggestionItemRepository therapySuggestionItemRepository;
    private final TreatmentCategoryRepository treatmentCategoryRepository;
    private final TherapyRepository therapyRepository;
    private final AppointmentBookingRepository appointmentBookingRepository;
    private final AppointmentConsultationTypeRepository appointmentConsultationTypeRepository;
    private final ConsultationTypeMasterRepository consultationTypeMasterRepository;
    private final AppointmentPhysicalExaminationRepository physicalExaminationRepository;
    private final AppointmentLifestyleInformationRepository lifestyleInformationRepository;
    private final TreatmentRepository treatmentRepository;
    private final FollowUpRepository followUpRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;

    @Override
    public ApiResponse<PrescriptionResponse> createPrescription(CreatePrescriptionRequest request) {
        log.info("Generating prescription for patient: {}", request.getPatientId());

        PatientSummaryResponse patient = fetchPatient(request.getPatientId());

        List<PrescriptionMedicineItemRequest> medicines =
                request.getMedicines() != null ? request.getMedicines() : List.of();
        List<PrescriptionTherapySuggestionRequest> therapySuggestions =
                request.getTherapySuggestions() != null ? request.getTherapySuggestions() : List.of();

        if (medicines.isEmpty() && therapySuggestions.isEmpty()) {
            throw new BadRequestException(AppMessages.PRESCRIPTION_MEDICINE_OR_THERAPY_REQUIRED);
        }

        validateTherapySuggestions(therapySuggestions);

        PrescriptionNextFollowUpRequest nextFollowUp = request.getNextFollowUp();
        boolean followUpRequired = nextFollowUp != null
                && Boolean.TRUE.equals(nextFollowUp.getSetUpRequired());

        Prescription prescription = Prescription.builder()
                .patientId(request.getPatientId())
                .appointmentBookingId(request.getAppointmentBookingId())
                .assignedDoctorId(request.getAssignedDoctorId())
                .followUpRequired(followUpRequired)
                .followUpSchedulingOption(nextFollowUp != null
                        ? trimToNull(nextFollowUp.getSchedulingOption())
                        : null)
                .followUpSuggestions(nextFollowUp != null
                        ? trimToNull(nextFollowUp.getSuggestions())
                        : null)
                .diagnosis(trimToNull(request.getDiagnosis()))
                .notes(trimToNull(request.getNotes()))
                .build();

        Prescription saved = prescriptionRepository.save(prescription);
        List<PrescriptionMedicine> savedMedicines = saveMedicines(saved.getId(), medicines);
        List<PrescriptionTherapySuggestion> savedSuggestions =
                saveTherapySuggestions(saved.getId(), therapySuggestions);

        log.info("Prescription generated successfully. Prescription ID: {}", saved.getId());

        return ApiResponse.success(
                AppMessages.PRESCRIPTION_CREATED,
                toEnrichedResponse(saved, savedMedicines, savedSuggestions, patient));
    }

    @Override
    public ApiResponse<PrescriptionResponse> updatePrescription(
            UUID prescriptionId, UpdatePrescriptionRequest request) {

        log.info("Updating prescription: {}", prescriptionId);

        Prescription prescription = prescriptionRepository.findByIdAndDeletedFalse(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.PRESCRIPTION_NOT_FOUND_WITH_ID + prescriptionId));

        List<PrescriptionMedicineItemRequest> medicines =
                request.getMedicines() != null ? request.getMedicines() : List.of();
        List<PrescriptionTherapySuggestionRequest> therapySuggestions =
                request.getTherapySuggestions() != null ? request.getTherapySuggestions() : List.of();

        if (medicines.isEmpty() && therapySuggestions.isEmpty()) {
            throw new BadRequestException(AppMessages.PRESCRIPTION_MEDICINE_OR_THERAPY_REQUIRED);
        }

        validateTherapySuggestions(therapySuggestions);

        PrescriptionNextFollowUpRequest nextFollowUp = request.getNextFollowUp();
        boolean followUpRequired = nextFollowUp != null
                && Boolean.TRUE.equals(nextFollowUp.getSetUpRequired());

        prescription.setAppointmentBookingId(request.getAppointmentBookingId());
        prescription.setAssignedDoctorId(request.getAssignedDoctorId());
        prescription.setFollowUpRequired(followUpRequired);
        prescription.setFollowUpSchedulingOption(nextFollowUp != null
                ? trimToNull(nextFollowUp.getSchedulingOption())
                : null);
        prescription.setFollowUpSuggestions(nextFollowUp != null
                ? trimToNull(nextFollowUp.getSuggestions())
                : null);
        prescription.setDiagnosis(trimToNull(request.getDiagnosis()));
        prescription.setNotes(trimToNull(request.getNotes()));

        Prescription saved = prescriptionRepository.save(prescription);

        softDeleteExistingChildren(prescriptionId);
        List<PrescriptionMedicine> savedMedicines = saveMedicines(saved.getId(), medicines);
        List<PrescriptionTherapySuggestion> savedSuggestions =
                saveTherapySuggestions(saved.getId(), therapySuggestions);

        log.info("Prescription updated successfully. Prescription ID: {}", prescriptionId);

        return ApiResponse.success(
                AppMessages.PRESCRIPTION_UPDATED,
                toEnrichedResponse(
                        saved,
                        savedMedicines,
                        savedSuggestions,
                        fetchPatientQuietly(saved.getPatientId())));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PrescriptionResponse> getPrescriptionById(UUID prescriptionId) {
        log.info("Fetching prescription by id: {}", prescriptionId);

        Prescription prescription = prescriptionRepository.findByIdAndDeletedFalse(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.PRESCRIPTION_NOT_FOUND_WITH_ID + prescriptionId));

        return ApiResponse.success(
                AppMessages.PRESCRIPTION_FETCHED,
                toEnrichedResponse(
                        prescription,
                        prescriptionMedicineRepository
                                .findAllByPrescriptionIdAndDeletedFalse(prescriptionId),
                        therapySuggestionRepository
                                .findAllByPrescriptionIdAndDeletedFalse(prescriptionId),
                        fetchPatientQuietly(prescription.getPatientId())));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PrescriptionResponse>> getPrescriptionsByPatientId(UUID patientId) {
        log.info("Fetching prescriptions for patient: {}", patientId);

        List<Prescription> prescriptions = prescriptionRepository
                .findAllByPatientIdAndDeletedFalseOrderByCreatedAtDesc(patientId);

        if (prescriptions.isEmpty()) {
            return ApiResponse.success(AppMessages.PRESCRIPTIONS_FETCHED, List.of());
        }

        PatientSummaryResponse patient = fetchPatientQuietly(patientId);

        List<UUID> prescriptionIds = prescriptions.stream()
                .map(Prescription::getId)
                .toList();

        Map<UUID, List<PrescriptionMedicine>> medicinesByPrescription =
                prescriptionMedicineRepository
                        .findAllByPrescriptionIdInAndDeletedFalse(prescriptionIds)
                        .stream()
                        .collect(Collectors.groupingBy(PrescriptionMedicine::getPrescriptionId));

        List<PrescriptionTherapySuggestion> allSuggestions = therapySuggestionRepository
                .findAllByPrescriptionIdInAndDeletedFalse(prescriptionIds);

        Map<UUID, List<PrescriptionTherapySuggestion>> suggestionsByPrescription =
                allSuggestions.stream()
                        .collect(Collectors.groupingBy(PrescriptionTherapySuggestion::getPrescriptionId));

        Map<UUID, List<PrescriptionTherapySuggestionItem>> itemsBySuggestion =
                loadSuggestionItems(allSuggestions);
        Map<UUID, TherapyMaster> therapiesById = loadTherapies(itemsBySuggestion.values());
        Map<UUID, TreatmentCategoryMaster> categoriesById = loadCategories(allSuggestions);

        EnrichmentContext context = loadEnrichmentContext(patientId, prescriptions, patient);

        List<PrescriptionResponse> responses = prescriptions.stream()
                .map(prescription -> toEnrichedResponse(
                        prescription,
                        medicinesByPrescription.getOrDefault(prescription.getId(), List.of()),
                        suggestionsByPrescription.getOrDefault(prescription.getId(), List.of()),
                        itemsBySuggestion,
                        therapiesById,
                        categoriesById,
                        context))
                .toList();

        log.info("Fetched {} prescriptions for patient: {}", responses.size(), patientId);
        return ApiResponse.success(AppMessages.PRESCRIPTIONS_FETCHED, responses);
    }

    private PrescriptionResponse toEnrichedResponse(
            Prescription prescription,
            List<PrescriptionMedicine> medicines,
            List<PrescriptionTherapySuggestion> suggestions,
            PatientSummaryResponse patient) {

        Map<UUID, List<PrescriptionTherapySuggestionItem>> itemsBySuggestion =
                loadSuggestionItems(suggestions);
        Map<UUID, TherapyMaster> therapiesById = loadTherapies(itemsBySuggestion.values());
        Map<UUID, TreatmentCategoryMaster> categoriesById = loadCategories(suggestions);
        EnrichmentContext context = loadEnrichmentContext(
                prescription.getPatientId(), List.of(prescription), patient);

        return toEnrichedResponse(
                prescription, medicines, suggestions, itemsBySuggestion, therapiesById, categoriesById, context);
    }

    private PrescriptionResponse toEnrichedResponse(
            Prescription prescription,
            List<PrescriptionMedicine> medicines,
            List<PrescriptionTherapySuggestion> suggestions,
            Map<UUID, List<PrescriptionTherapySuggestionItem>> itemsBySuggestion,
            Map<UUID, TherapyMaster> therapiesById,
            Map<UUID, TreatmentCategoryMaster> categoriesById,
            EnrichmentContext context) {

        List<PrescriptionMedicineItemResponse> medicineResponses = medicines.stream()
                .map(medicine -> PrescriptionMedicineItemResponse.builder()
                        .id(medicine.getId())
                        .medicineId(medicine.getMedicineId())
                        .medicineName(medicine.getMedicineName())
                        .dosage(medicine.getDosage())
                        .frequency(medicine.getFrequency())
                        .duration(medicine.getDuration())
                        .instruction(medicine.getNotes())
                        .notes(medicine.getNotes())
                        .build())
                .toList();

        List<PrescriptionTherapySuggestionResponse> therapyResponses = suggestions.stream()
                .map(suggestion -> {
                    TreatmentCategoryMaster category =
                            categoriesById.get(suggestion.getTherapyCategoryId());
                    List<RecommendedTherapyItemResponse> recommended = itemsBySuggestion
                            .getOrDefault(suggestion.getId(), List.of())
                            .stream()
                            .map(item -> {
                                TherapyMaster therapy = therapiesById.get(item.getTherapyMasterId());
                                return RecommendedTherapyItemResponse.builder()
                                        .therapyId(item.getTherapyMasterId())
                                        .therapyName(therapy != null ? therapy.getTherapyName() : null)
                                        .build();
                            })
                            .toList();

                    return PrescriptionTherapySuggestionResponse.builder()
                            .id(suggestion.getId())
                            .therapyCategoryId(suggestion.getTherapyCategoryId())
                            .therapyCategoryName(category != null ? category.getCategoryName() : null)
                            .recommendedTherapies(recommended)
                            .build();
                })
                .toList();

        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .patientId(prescription.getPatientId())
                .appointmentBookingId(prescription.getAppointmentBookingId())
                .assignedDoctorId(prescription.getAssignedDoctorId())
                .patient(buildPatientDetails(context.patient, context.physicalExam, context.lifestyle))
                .treatment(buildTreatmentDetails(prescription, context))
                .consultant(buildConsultantDetails(prescription, context))
                .diagnosis(prescription.getDiagnosis())
                .notes(prescription.getNotes())
                .medicines(medicineResponses)
                .therapySuggestions(therapyResponses)
                .nextFollowUp(PrescriptionNextFollowUpResponse.builder()
                        .setUpRequired(Boolean.TRUE.equals(prescription.getFollowUpRequired()))
                        .schedulingOption(prescription.getFollowUpSchedulingOption())
                        .suggestions(prescription.getFollowUpSuggestions())
                        .build())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }

    private PatientDetails buildPatientDetails(
            PatientSummaryResponse patient,
            AppointmentPhysicalExamination physicalExam,
            AppointmentLifestyleInformation lifestyle) {

        if (patient == null && physicalExam == null && lifestyle == null) {
            return null;
        }

        return PatientDetails.builder()
                .id(patient != null ? patient.getId() : null)
                .patientDisplayId(patient != null ? patient.getPatientDisplayId() : null)
                .patientCode(patient != null ? patient.getPatientCode() : null)
                .fullName(patient != null ? patient.getFullName() : null)
                .age(patient != null ? patient.getAge() : null)
                .gender(patient != null ? patient.getGender() : null)
                .weight(physicalExam != null ? physicalExam.getWeight() : null)
                .height(physicalExam != null ? physicalExam.getHeight() : null)
                .dietType(lifestyle != null ? lifestyle.getDietType() : null)
                .build();
    }

    private TreatmentDetails buildTreatmentDetails(Prescription prescription, EnrichmentContext context) {
        AppointmentBooking booking = null;
        if (prescription.getAppointmentBookingId() != null) {
            booking = context.bookingsById.get(prescription.getAppointmentBookingId());
        }
        if (booking == null) {
            booking = context.latestBooking;
        }

        List<String> consultationTypeNames = List.of();
        LocalDateTime consultationDateTime = null;
        if (booking != null) {
            consultationTypeNames = context.consultationTypeNamesByBookingId
                    .getOrDefault(booking.getId(), List.of());
            if (booking.getRegistrationDate() != null) {
                consultationDateTime = booking.getSlotTime() != null
                        ? LocalDateTime.of(booking.getRegistrationDate(), booking.getSlotTime())
                        : booking.getRegistrationDate().atStartOfDay();
            }
        }

        LocalDateTime nextAppointmentDateTime = context.nextFollowUpDateByPatientId
                .get(prescription.getPatientId());

        Integer visitNumber = null;
        Integer totalVisits = null;
        String visitDisplay = null;
        Treatment treatment = context.latestTreatment;
        if (treatment != null) {
            visitNumber = treatment.getCompletedSessions();
            totalVisits = treatment.getTotalSessions();
            if (visitNumber != null && totalVisits != null) {
                visitDisplay = String.format("%02d/%02d", visitNumber, totalVisits);
            }
        }

        return TreatmentDetails.builder()
                .consultationTypes(consultationTypeNames)
                .consultationDateTime(consultationDateTime)
                .nextAppointmentDateTime(nextAppointmentDateTime)
                .visitNumber(visitNumber)
                .totalVisits(totalVisits)
                .visitDisplay(visitDisplay)
                .build();
    }

    private ConsultantDetails buildConsultantDetails(
            Prescription prescription, EnrichmentContext context) {

        UUID doctorId = prescription.getAssignedDoctorId();
        if (doctorId == null && prescription.getAppointmentBookingId() != null) {
            AppointmentBooking booking = context.bookingsById.get(prescription.getAppointmentBookingId());
            if (booking != null) {
                doctorId = booking.getAssignedDoctorId();
            }
        }
        if (doctorId == null && context.latestBooking != null) {
            doctorId = context.latestBooking.getAssignedDoctorId();
        }
        if (doctorId == null) {
            return null;
        }

        DoctorSummaryResponse doctor = context.doctorsById.computeIfAbsent(
                doctorId, this::fetchDoctorQuietly);
        if (doctor == null) {
            return ConsultantDetails.builder().id(doctorId).build();
        }

        return ConsultantDetails.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialization(doctor.getSpecialization())
                .qualification(doctor.getQualification())
                .contactNumber(doctor.getMobileNumber())
                .build();
    }

    private EnrichmentContext loadEnrichmentContext(
            UUID patientId,
            List<Prescription> prescriptions,
            PatientSummaryResponse patient) {

        AppointmentPhysicalExamination physicalExam =
                physicalExaminationRepository.findByPatientId(patientId).orElse(null);
        AppointmentLifestyleInformation lifestyle =
                lifestyleInformationRepository.findByPatientId(patientId).orElse(null);

        List<AppointmentBooking> patientBookings =
                appointmentBookingRepository.findByPatientId(patientId);
        Map<UUID, AppointmentBooking> bookingsById = patientBookings.stream()
                .collect(Collectors.toMap(AppointmentBooking::getId, Function.identity(), (a, b) -> a));

        AppointmentBooking latestBooking = patientBookings.stream()
                .max(Comparator.comparing(AppointmentBooking::getRegistrationDate,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(AppointmentBooking::getCreatedAt,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        Set<UUID> bookingIds = new HashSet<>();
        for (Prescription prescription : prescriptions) {
            if (prescription.getAppointmentBookingId() != null) {
                bookingIds.add(prescription.getAppointmentBookingId());
            }
        }
        if (latestBooking != null) {
            bookingIds.add(latestBooking.getId());
        }

        Map<UUID, List<String>> consultationTypeNamesByBookingId = loadConsultationTypeNames(bookingIds);

        Treatment latestTreatment = treatmentRepository
                .findAllByPatientIdAndDeletedFalseOrderByStartDateDesc(patientId)
                .stream()
                .findFirst()
                .orElse(null);

        Map<UUID, LocalDateTime> nextFollowUpDateByPatientId = new HashMap<>();
        followUpRepository.findAllByPatientIdAndDeletedFalseOrderByAppointmentDateAsc(patientId)
                .stream()
                .filter(fu -> fu.getStatus() == FollowUpStatus.UPCOMING)
                .min(Comparator.comparing(FollowUp::getAppointmentDate))
                .ifPresent(fu -> nextFollowUpDateByPatientId.put(patientId, fu.getAppointmentDate()));

        return new EnrichmentContext(
                patient,
                physicalExam,
                lifestyle,
                bookingsById,
                latestBooking,
                consultationTypeNamesByBookingId,
                latestTreatment,
                nextFollowUpDateByPatientId,
                new HashMap<>());
    }

    private Map<UUID, List<String>> loadConsultationTypeNames(Set<UUID> bookingIds) {
        if (bookingIds.isEmpty()) {
            return Map.of();
        }

        List<AppointmentConsultationType> mappings =
                appointmentConsultationTypeRepository.findByBookingIdIn(bookingIds);
        Set<UUID> typeIds = mappings.stream()
                .map(AppointmentConsultationType::getConsultationTypeId)
                .collect(Collectors.toSet());

        Map<UUID, ConsultationTypeMaster> mastersById = consultationTypeMasterRepository
                .findByIdInAndDeletedFalse(typeIds)
                .stream()
                .collect(Collectors.toMap(ConsultationTypeMaster::getId, Function.identity()));

        return mappings.stream()
                .collect(Collectors.groupingBy(
                        AppointmentConsultationType::getBookingId,
                        Collectors.mapping(mapping -> {
                            ConsultationTypeMaster master =
                                    mastersById.get(mapping.getConsultationTypeId());
                            return master != null ? master.getName() : null;
                        }, Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
                                .filter(Objects::nonNull)
                                .toList()))));
    }

    private PatientSummaryResponse fetchPatient(UUID patientId) {
        try {
            PatientSummaryResponse patient = patientServiceClient.getPatientById(patientId).getData();
            if (patient == null) {
                throw new ResourceNotFoundException(
                        AppMessages.PATIENT_NOT_FOUND_WITH_ID + patientId);
            }
            return patient;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(
                    AppMessages.PATIENT_NOT_FOUND_WITH_ID + patientId);
        } catch (FeignException ex) {
            log.error("Failed to fetch patient {}: {}", patientId, ex.getMessage());
            throw new ResourceNotFoundException(
                    AppMessages.PATIENT_NOT_FOUND_WITH_ID + patientId);
        }
    }

    private PatientSummaryResponse fetchPatientQuietly(UUID patientId) {
        try {
            return fetchPatient(patientId);
        } catch (Exception ex) {
            log.warn("Patient details unavailable for {}: {}", patientId, ex.getMessage());
            return null;
        }
    }

    private DoctorSummaryResponse fetchDoctorQuietly(UUID doctorId) {
        try {
            return doctorServiceClient.getDoctorById(doctorId).getData();
        } catch (Exception ex) {
            log.warn("Doctor details unavailable for {}: {}", doctorId, ex.getMessage());
            return null;
        }
    }

    private void softDeleteExistingChildren(UUID prescriptionId) {
        List<PrescriptionMedicine> existingMedicines =
                prescriptionMedicineRepository.findAllByPrescriptionIdAndDeletedFalse(prescriptionId);
        for (PrescriptionMedicine medicine : existingMedicines) {
            medicine.setDeleted(true);
        }
        prescriptionMedicineRepository.saveAll(existingMedicines);

        List<PrescriptionTherapySuggestion> existingSuggestions =
                therapySuggestionRepository.findAllByPrescriptionIdAndDeletedFalse(prescriptionId);
        if (!existingSuggestions.isEmpty()) {
            List<UUID> suggestionIds = existingSuggestions.stream()
                    .map(PrescriptionTherapySuggestion::getId)
                    .toList();
            List<PrescriptionTherapySuggestionItem> existingItems =
                    therapySuggestionItemRepository
                            .findAllByTherapySuggestionIdInAndDeletedFalse(suggestionIds);
            for (PrescriptionTherapySuggestionItem item : existingItems) {
                item.setDeleted(true);
            }
            therapySuggestionItemRepository.saveAll(existingItems);

            for (PrescriptionTherapySuggestion suggestion : existingSuggestions) {
                suggestion.setDeleted(true);
            }
            therapySuggestionRepository.saveAll(existingSuggestions);
        }
    }

    private void validateTherapySuggestions(List<PrescriptionTherapySuggestionRequest> suggestions) {
        for (PrescriptionTherapySuggestionRequest suggestion : suggestions) {
            if (CollectionUtils.isEmpty(suggestion.getRecommendedTherapyIds())) {
                throw new BadRequestException(AppMessages.PRESCRIPTION_THERAPY_IDS_REQUIRED);
            }

            treatmentCategoryRepository.findById(suggestion.getTherapyCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            AppMessages.TREATMENT_CATEGORY_NOT_FOUND_WITH_ID
                                    + suggestion.getTherapyCategoryId()));

            for (UUID therapyId : suggestion.getRecommendedTherapyIds()) {
                therapyRepository.findByIdAndDeletedFalse(therapyId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                AppMessages.THERAPY_NOT_FOUND_WITH_ID + therapyId));
            }
        }
    }

    private List<PrescriptionMedicine> saveMedicines(
            UUID prescriptionId, List<PrescriptionMedicineItemRequest> medicines) {

        List<PrescriptionMedicine> entities = new ArrayList<>();
        for (PrescriptionMedicineItemRequest item : medicines) {
            entities.add(PrescriptionMedicine.builder()
                    .prescriptionId(prescriptionId)
                    .medicineId(item.getMedicineId())
                    .medicineName(item.getMedicineName().trim())
                    .dosage(trimToNull(item.getDosage()))
                    .frequency(trimToNull(item.getFrequency()))
                    .duration(trimToNull(item.getDuration()))
                    .notes(trimToNull(item.getNotes()))
                    .build());
        }
        return prescriptionMedicineRepository.saveAll(entities);
    }

    private List<PrescriptionTherapySuggestion> saveTherapySuggestions(
            UUID prescriptionId, List<PrescriptionTherapySuggestionRequest> suggestions) {

        List<PrescriptionTherapySuggestion> savedSuggestions = new ArrayList<>();
        for (PrescriptionTherapySuggestionRequest suggestion : suggestions) {
            PrescriptionTherapySuggestion saved = therapySuggestionRepository.save(
                    PrescriptionTherapySuggestion.builder()
                            .prescriptionId(prescriptionId)
                            .therapyCategoryId(suggestion.getTherapyCategoryId())
                            .build());

            List<PrescriptionTherapySuggestionItem> items = suggestion.getRecommendedTherapyIds()
                    .stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(therapyId -> PrescriptionTherapySuggestionItem.builder()
                            .therapySuggestionId(saved.getId())
                            .therapyMasterId(therapyId)
                            .build())
                    .toList();

            therapySuggestionItemRepository.saveAll(items);
            savedSuggestions.add(saved);
        }
        return savedSuggestions;
    }

    private Map<UUID, List<PrescriptionTherapySuggestionItem>> loadSuggestionItems(
            List<PrescriptionTherapySuggestion> suggestions) {

        if (suggestions.isEmpty()) {
            return Map.of();
        }

        Set<UUID> suggestionIds = suggestions.stream()
                .map(PrescriptionTherapySuggestion::getId)
                .collect(Collectors.toSet());

        return therapySuggestionItemRepository
                .findAllByTherapySuggestionIdInAndDeletedFalse(suggestionIds)
                .stream()
                .collect(Collectors.groupingBy(PrescriptionTherapySuggestionItem::getTherapySuggestionId));
    }

    private Map<UUID, TherapyMaster> loadTherapies(
            Collection<List<PrescriptionTherapySuggestionItem>> itemGroups) {

        Set<UUID> therapyIds = new HashSet<>();
        for (List<PrescriptionTherapySuggestionItem> items : itemGroups) {
            for (PrescriptionTherapySuggestionItem item : items) {
                therapyIds.add(item.getTherapyMasterId());
            }
        }
        if (therapyIds.isEmpty()) {
            return Map.of();
        }

        return therapyRepository.findByIdInAndDeletedFalse(new ArrayList<>(therapyIds))
                .stream()
                .collect(Collectors.toMap(TherapyMaster::getId, Function.identity()));
    }

    private Map<UUID, TreatmentCategoryMaster> loadCategories(
            List<PrescriptionTherapySuggestion> suggestions) {

        Set<UUID> categoryIds = suggestions.stream()
                .map(PrescriptionTherapySuggestion::getTherapyCategoryId)
                .collect(Collectors.toSet());

        if (categoryIds.isEmpty()) {
            return Map.of();
        }

        Map<UUID, TreatmentCategoryMaster> map = new HashMap<>();
        for (UUID categoryId : categoryIds) {
            treatmentCategoryRepository.findById(categoryId)
                    .ifPresent(category -> map.put(categoryId, category));
        }
        return map;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private record EnrichmentContext(
            PatientSummaryResponse patient,
            AppointmentPhysicalExamination physicalExam,
            AppointmentLifestyleInformation lifestyle,
            Map<UUID, AppointmentBooking> bookingsById,
            AppointmentBooking latestBooking,
            Map<UUID, List<String>> consultationTypeNamesByBookingId,
            Treatment latestTreatment,
            Map<UUID, LocalDateTime> nextFollowUpDateByPatientId,
            Map<UUID, DoctorSummaryResponse> doctorsById) {
    }

}
