package com.ayurveda.doctor.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.doctor.dto.request.CreateDoctorRequest;
import com.ayurveda.doctor.dto.response.DoctorResponse;
import com.ayurveda.doctor.entity.Doctor;
import com.ayurveda.doctor.enums.DoctorStatus;
import com.ayurveda.doctor.mapper.DoctorMapper;
import com.ayurveda.doctor.repository.DoctorRepository;
import com.ayurveda.doctor.service.DoctorService;
import com.ayurveda.doctor.util.DoctorCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorCodeGenerator doctorCodeGenerator;

    @Override
    @Transactional
    public ApiResponse<DoctorResponse> createDoctor(CreateDoctorRequest request) {
        DoctorStatus status = request.getStatus() != null ? request.getStatus() : DoctorStatus.ACTIVE;

        Doctor doctor = Doctor.builder()
                .doctorName(request.getName().trim())
                .doctorCode(doctorCodeGenerator.generate())
                .specialization(request.getSpecialization().trim())
                .status(status)
                .consultationFees(request.getConsultationFees())
                .followUpFees(request.getFollowUpFees())
                .availability(request.getAvailability().trim())
                .active(status == DoctorStatus.ACTIVE)
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
        AtomicInteger serial = new AtomicInteger(1);
        List<DoctorResponse> doctors = doctorRepository.findAllByDeletedFalse().stream()
                .map(doctor -> DoctorMapper.toResponse(doctor, serial.getAndIncrement()))
                .toList();
        return ApiResponse.success("Doctors fetched successfully.", doctors);
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteDoctor(UUID doctorId) {
        Doctor doctor = doctorRepository.findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));

        doctor.setDeleted(true);
        doctor.setActive(false);
        doctor.setStatus(DoctorStatus.INACTIVE);
        doctorRepository.save(doctor);

        return ApiResponse.success("Doctor deleted successfully.", null);
    }

}
