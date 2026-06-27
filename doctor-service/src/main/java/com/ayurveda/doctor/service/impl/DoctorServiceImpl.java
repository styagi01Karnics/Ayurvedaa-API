package com.ayurveda.doctor.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.doctor.dto.request.CreateDoctorRequest;
import com.ayurveda.doctor.dto.response.DoctorResponse;
import com.ayurveda.doctor.entity.Doctor;
import com.ayurveda.doctor.mapper.DoctorMapper;
import com.ayurveda.doctor.repository.DoctorRepository;
import com.ayurveda.doctor.service.DoctorService;
import com.ayurveda.doctor.util.DoctorCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorCodeGenerator doctorCodeGenerator;

    @Override
    @Transactional
    public ApiResponse<DoctorResponse> createDoctor(CreateDoctorRequest request) {
        if (StringUtils.hasText(request.getEmail())
                && doctorRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new DuplicateResourceException("Doctor with this email already exists.");
        }

        Doctor doctor = Doctor.builder()
                .doctorName(request.getDoctorName())
                .doctorCode(doctorCodeGenerator.generate())
                .specialization(request.getSpecialization())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .qualification(request.getQualification())
                .department(request.getDepartment())
                .consultationRoom(request.getConsultationRoom())
                .active(true)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        return ApiResponse.success("Doctor created successfully.", DoctorMapper.toResponse(savedDoctor));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DoctorResponse> getDoctorById(UUID doctorId) {
        Doctor doctor = doctorRepository.findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));
        return ApiResponse.success("Doctor fetched successfully.", DoctorMapper.toResponse(doctor));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorResponse>> getAllDoctors() {
        List<DoctorResponse> doctors = doctorRepository.findAllByDeletedFalse().stream()
                .map(DoctorMapper::toResponse)
                .toList();
        return ApiResponse.success("Doctors fetched successfully.", doctors);
    }

}
