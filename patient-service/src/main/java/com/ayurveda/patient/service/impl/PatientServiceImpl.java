package com.ayurveda.patient.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.activity.ActivityActionType;
import com.ayurveda.common.activity.ActivityLogPublisher;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.dto.PagedResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.notification.EmailNotificationPublisher;
import com.ayurveda.common.util.PageRequests;
import com.ayurveda.common.util.ResponseUtil;
import com.ayurveda.common.validation.IdProofValidator;
import com.ayurveda.patient.dto.request.CreatePatientRequest;
import com.ayurveda.patient.dto.response.DashboardNewPatientsMonthlyResponse;
import com.ayurveda.patient.dto.response.MonthlyCountResponse;
import com.ayurveda.patient.dto.response.PatientCountResponse;
import com.ayurveda.patient.dto.response.PatientResponse;
import com.ayurveda.patient.entity.Patient;
import com.ayurveda.patient.enums.PatientStatus;
import com.ayurveda.patient.mapper.PatientMapper;
import com.ayurveda.patient.repository.PatientRepository;
import com.ayurveda.patient.service.PatientService;
import com.ayurveda.patient.util.FullNameSplitter;
import com.ayurveda.patient.util.PatientCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientCodeGenerator patientCodeGenerator;
    private final ActivityLogPublisher activityLogPublisher;
    private final EmailNotificationPublisher emailNotificationPublisher;

    @Override
    @Transactional
    public ApiResponse<PatientResponse> createPatient(CreatePatientRequest request) {
    	
    	log.info("Creating patient with mobile number: {}", request.getMobileNumber());

        normalizeRequest(request);

        validatePatient(request);

        validateDuplicatePatient(request);

        String[] names = FullNameSplitter.split(request.getFullName());
        String patientCode = patientCodeGenerator.generate();

        Patient patient = Patient.builder()
                .patientCode(patientCode)
                .firstName(names[0])
                .lastName(names[1])
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .age(request.getAge())
                .preferredLanguage(request.getPreferredLanguage())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .state(request.getState())
                .city(request.getCity())
                .address(request.getAddress())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyRelationship(request.getEmergencyRelationship())
                .emergencyPhoneNumber(request.getEmergencyPhoneNumber())
                .idProofType(request.getIdProofType())
                .idProofNumber(request.getIdProofNumber())
                .occupation(request.getOccupation())
                .insuranceDetails(request.getInsuranceDetails())
                .status(PatientStatus.ACTIVE)
                .build();

        Patient savedPatient = patientRepository.save(patient);
        
        log.info("Patient created successfully. Patient ID: {}, Patient Code: {}",
                savedPatient.getId(),
                savedPatient.getPatientCode());

        activityLogPublisher.record(
                "Patients",
                ActivityActionType.CREATED,
                "Patient " + savedPatient.getPatientCode());

        emailPatientRegistered(savedPatient);

        return ResponseUtil.success(
                AppConstants.PATIENT_CREATED_SUCCESSFULLY,
                PatientMapper.toResponse(savedPatient)
        );
    }

    private void emailPatientRegistered(Patient patient) {
        if (patient == null || !StringUtils.hasText(patient.getEmail())) {
            return;
        }
        String name = ((patient.getFirstName() != null ? patient.getFirstName() : "")
                + " "
                + (patient.getLastName() != null ? patient.getLastName() : "")).trim();
        if (!StringUtils.hasText(name)) {
            name = "Patient";
        }
        String body = """
                Hello %s,

                Your patient profile has been registered at our Ayurveda hospital.

                Patient ID: %s

                Please keep this ID handy for future visits and appointments.

                Thank you.
                """.formatted(name, patient.getPatientCode());
        emailNotificationPublisher.sendEmail(
                patient.getEmail(),
                "Welcome — patient registration confirmed",
                body);
    }
    
    private void normalizeRequest(CreatePatientRequest request) {

        request.setFullName(request.getFullName().trim());

        request.setMobileNumber(request.getMobileNumber().trim());

        if (StringUtils.hasText(request.getEmail())) {
            request.setEmail(request.getEmail().trim().toLowerCase());
        }

        if (StringUtils.hasText(request.getState())) {
            request.setState(request.getState().trim());
        }

        if (StringUtils.hasText(request.getCity())) {
            request.setCity(request.getCity().trim());
        }

        if (StringUtils.hasText(request.getAddress())) {
            request.setAddress(request.getAddress().trim());
        }

        if (StringUtils.hasText(request.getPreferredLanguage())) {
            request.setPreferredLanguage(request.getPreferredLanguage().trim());
        }

        if (StringUtils.hasText(request.getEmergencyContactName())) {
            request.setEmergencyContactName(request.getEmergencyContactName().trim());
        }

        if (StringUtils.hasText(request.getEmergencyRelationship())) {
            request.setEmergencyRelationship(request.getEmergencyRelationship().trim());
        }

        if (StringUtils.hasText(request.getEmergencyPhoneNumber())) {
            request.setEmergencyPhoneNumber(request.getEmergencyPhoneNumber().trim());
        }

        if (StringUtils.hasText(request.getOccupation())) {
            request.setOccupation(request.getOccupation().trim());
        }

        if (StringUtils.hasText(request.getInsuranceDetails())) {
            request.setInsuranceDetails(request.getInsuranceDetails().trim());
        }

        if (StringUtils.hasText(request.getIdProofNumber())) {
            request.setIdProofNumber(request.getIdProofNumber().trim().toUpperCase());
        }
    }
    
    private void validateDuplicatePatient(CreatePatientRequest request) {

        if (StringUtils.hasText(request.getEmail())
                && patientRepository.existsByEmailAndDeletedFalse(request.getEmail())) {

            throw new DuplicateResourceException(
                    AppConstants.PATIENT_EMAIL_ALREADY_EXISTS
            );
        }

        // Mobile number may be shared (e.g. parent and child) — do not treat as duplicate.
    }
    
    private void validatePatient(CreatePatientRequest request) {

        if (request.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new BadRequestException(AppConstants.INVALID_DATE_OF_BIRTH);
        }

        if (StringUtils.hasText(request.getEmergencyPhoneNumber())
                && request.getEmergencyPhoneNumber().equals(request.getMobileNumber())) {

            throw new BadRequestException(
                    AppConstants.INVALID_EMERGENCY_CONTACT
            );
        }

        IdProofValidator.validate(request.getIdProofType(), request.getIdProofNumber());
    }
     
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PatientResponse> getPatientById(UUID patientId) {

        log.info("Fetching patient details for patientId: {}", patientId);

        Patient patient = patientRepository.findByIdAndDeletedFalse(patientId)
                .orElseThrow(() -> {
                    log.warn("Patient not found with patientId: {}", patientId);
                    return new ResourceNotFoundException(AppConstants.PATIENT_NOT_FOUND);
                });

        log.info("Patient fetched successfully. Patient ID: {}", patientId);

        return ResponseUtil.success(
                AppConstants.PATIENT_FETCHED_SUCCESSFULLY,
                PatientMapper.toResponse(patient)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<PatientResponse>> getAllPatients(int page, int size) {

        log.info("Fetching patients page={}, size={}", page, size);

        Page<Patient> result = patientRepository.findAllByDeletedFalse(
                PageRequests.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        PagedResponse<PatientResponse> paged = PagedResponse.of(
                result.map(PatientMapper::toResponse));

        log.info("Successfully fetched {} patients (total={}).",
                paged.getContent().size(), paged.getTotalElements());

        return ResponseUtil.success(
                AppConstants.PATIENTS_FETCHED_SUCCESSFULLY,
                paged
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PatientCountResponse> getTotalPatientCount() {

        log.info("Fetching patient counts (total, active, inactive).");

        long totalPatients = patientRepository.countByDeletedFalse();
        long activePatients = patientRepository.countByDeletedFalseAndStatus(PatientStatus.ACTIVE);
        long inactivePatients = patientRepository.countByDeletedFalseAndStatus(PatientStatus.INACTIVE);

        log.info("Patient counts — total: {}, active: {}, inactive: {}",
                totalPatients, activePatients, inactivePatients);

        PatientCountResponse counts = PatientCountResponse.builder()
                .totalPatients(totalPatients)
                .activePatients(activePatients)
                .inactivePatients(inactivePatients)
                .build();

        return ResponseUtil.success(
                "Patient counts fetched successfully.",
                counts
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DashboardNewPatientsMonthlyResponse> getDashboardNewPatientsByMonth(Integer year) {
        int resolvedYear = resolveYear(year);
        LocalDateTime from = LocalDate.of(resolvedYear, 1, 1).atStartOfDay();
        LocalDateTime to = LocalDate.of(resolvedYear + 1, 1, 1).atStartOfDay();

        log.info("Fetching dashboard new-patient monthly counts for year {}", resolvedYear);

        Map<Integer, Long> byMonth = new HashMap<>();
        for (Object[] row : patientRepository.countCreatedByMonth(from, to)) {
            byMonth.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }

        List<MonthlyCountResponse> months = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            months.add(MonthlyCountResponse.builder()
                    .monthNumber(month)
                    .count(byMonth.getOrDefault(month, 0L))
                    .build());
        }

        return ResponseUtil.success(
                AppConstants.DASHBOARD_PATIENT_TRENDS_FETCHED,
                DashboardNewPatientsMonthlyResponse.builder()
                        .year(resolvedYear)
                        .months(months)
                        .build());
    }

    private static int resolveYear(Integer year) {
        int current = LocalDate.now().getYear();
        int resolved = year != null ? year : current;
        if (resolved < 2000 || resolved > current + 1) {
            throw new BadRequestException("Year must be between 2000 and " + (current + 1) + ".");
        }
        return resolved;
    }

    @Override
    @Transactional
    public ApiResponse<Void> deletePatient(UUID patientId) {

        log.info("Received request to delete patient with ID: {}", patientId);

        Patient patient = patientRepository.findByIdAndDeletedFalse(patientId)
                .orElseThrow(() -> {
                    log.warn("Patient not found with ID: {}", patientId);
                    return new ResourceNotFoundException(
                            AppConstants.PATIENT_NOT_FOUND
                    );
                });

        patient.setDeleted(true);
        patient.setStatus(PatientStatus.INACTIVE);

        patientRepository.save(patient);

        log.info("Patient deleted successfully. Patient ID: {}", patientId);

        activityLogPublisher.record(
                "Patients",
                ActivityActionType.DELETED,
                "Patient " + patient.getPatientCode());

        return ResponseUtil.success(
                AppConstants.PATIENT_DELETED_SUCCESSFULLY
        );
    }

}
