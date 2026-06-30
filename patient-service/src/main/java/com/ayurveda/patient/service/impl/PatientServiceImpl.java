package com.ayurveda.patient.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.patient.dto.request.CreatePatientRequest;
import com.ayurveda.patient.dto.response.PatientResponse;
import com.ayurveda.patient.entity.Patient;
import com.ayurveda.patient.mapper.PatientMapper;
import com.ayurveda.patient.repository.PatientRepository;
import com.ayurveda.patient.service.PatientService;
import com.ayurveda.patient.util.FullNameSplitter;
import com.ayurveda.patient.util.PatientCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientCodeGenerator patientCodeGenerator;

    @Override
    @Transactional
    public ApiResponse<PatientResponse> createPatient(CreatePatientRequest request) {
        if (StringUtils.hasText(request.getEmail())
                && patientRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new DuplicateResourceException("Patient with this email already exists.");
        }

        if (patientRepository.existsByMobileNumberAndDeletedFalse(request.getMobileNumber())) {
            throw new DuplicateResourceException("Patient with this mobile number already exists.");
        }

        String[] names = FullNameSplitter.split(request.getFullName());

        Patient patient = Patient.builder()
                .patientCode(patientCodeGenerator.generate())
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
                .active(true)
                .build();

        Patient savedPatient = patientRepository.save(patient);
        return ApiResponse.success("Patient created successfully.", PatientMapper.toResponse(savedPatient));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PatientResponse> getPatientById(UUID patientId) {
        Patient patient = patientRepository.findByIdAndDeletedFalse(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found."));
        return ApiResponse.success("Patient fetched successfully.", PatientMapper.toResponse(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> patients = patientRepository.findAllByDeletedFalse().stream()
                .map(PatientMapper::toResponse)
                .toList();
        return ApiResponse.success("Patients fetched successfully.", patients);
    }

}
