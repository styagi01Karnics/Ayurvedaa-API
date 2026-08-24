package com.ayurveda.patient.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.activity.ActivityActionType;
import com.ayurveda.common.activity.ActivityLogPublisher;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.util.ResponseUtil;
import com.ayurveda.common.validation.IdProofValidator;
import com.ayurveda.patient.dto.request.CreatePatientRequest;
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientCodeGenerator patientCodeGenerator;
    private final ActivityLogPublisher activityLogPublisher;

    @Override
    @Transactional
    public ApiResponse<PatientResponse> createPatient(CreatePatientRequest request) {
    	
    	log.info("Creating patient with mobile number: {}", request.getMobileNumber());

        normalizeRequest(request);

        validatePatient(request);

        validateDuplicatePatient(request);

        String[] names = FullNameSplitter.split(request.getFullName());
        PatientCodeGenerator.PatientIdentifiers identifiers = patientCodeGenerator.generate();

        Patient patient = Patient.builder()
                .patientDisplayId(identifiers.patientDisplayId())
                .patientCode(identifiers.patientCode())
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
        
        log.info("Patient created successfully. Patient ID: {}, Display ID: {}, Patient Code: {}",
                savedPatient.getId(),
                savedPatient.getPatientDisplayId(),
                savedPatient.getPatientCode());

        activityLogPublisher.record(
                "Patients",
                ActivityActionType.CREATED,
                "Patient " + savedPatient.getPatientDisplayId());

        return ResponseUtil.success(
                AppConstants.PATIENT_CREATED_SUCCESSFULLY,
                PatientMapper.toResponse(savedPatient)
        );
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
    public ApiResponse<List<PatientResponse>> getAllPatients() {

        log.info("Fetching all active patients.");

        List<PatientResponse> patients = patientRepository.findAllByDeletedFalse()
                .stream()
                .map(PatientMapper::toResponse)
                .toList();

        log.info("Successfully fetched {} active patients.", patients.size());

        return ResponseUtil.success(
                AppConstants.PATIENTS_FETCHED_SUCCESSFULLY,
                patients
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Long> getTotalPatientCount() {

        log.info("Fetching total active patient count.");

        long count = patientRepository.countByDeletedFalse();

        log.info("Total active patients: {}", count);

        return ResponseUtil.success(
                "Total patient count fetched successfully.",
                count
        );
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
                "Patient " + patient.getPatientDisplayId());

        return ResponseUtil.success(
                AppConstants.PATIENT_DELETED_SUCCESSFULLY
        );
    }

}
