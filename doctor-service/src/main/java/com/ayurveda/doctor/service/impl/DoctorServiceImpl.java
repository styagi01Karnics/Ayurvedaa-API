package com.ayurveda.doctor.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.activity.ActivityActionType;
import com.ayurveda.common.activity.ActivityLogPublisher;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.dto.PagedResponse;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.util.PageRequests;
import com.ayurveda.doctor.dto.request.CreateDoctorRequest;
import com.ayurveda.doctor.dto.request.UpdateDoctorStatusRequest;
import com.ayurveda.doctor.dto.response.DoctorResponse;
import com.ayurveda.doctor.entity.Doctor;
import com.ayurveda.doctor.enums.DoctorStatus;
import com.ayurveda.doctor.mapper.DoctorMapper;
import com.ayurveda.doctor.repository.DoctorRepository;
import com.ayurveda.doctor.service.DoctorService;
import com.ayurveda.doctor.util.DoctorCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorCodeGenerator doctorCodeGenerator;
    private final ActivityLogPublisher activityLogPublisher;

    @Override
    @Transactional
    public ApiResponse<DoctorResponse> createDoctor(CreateDoctorRequest request) {
        log.info("Creating doctor: {}", request.getName());

        DoctorStatus status = request.getStatus() != null ? request.getStatus() : DoctorStatus.ACTIVE;

        Doctor doctor = Doctor.builder()
                .doctorName(request.getName().trim())
                .doctorCode(doctorCodeGenerator.generate())
                .specialization(request.getSpecialization().trim())
                .status(status)
                .consultationFees(request.getConsultationFees())
                .followUpFees(request.getFollowUpFees())
                .availability(request.getAvailability().trim())
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        log.info("Doctor created successfully. Doctor ID: {}", savedDoctor.getId());

        activityLogPublisher.record(
                "Doctors",
                ActivityActionType.CREATED,
                "Doctor " + savedDoctor.getDoctorName());

        return ApiResponse.success(AppConstants.DOCTOR_CREATED_SUCCESSFULLY, DoctorMapper.toResponse(savedDoctor));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DoctorResponse> getDoctorById(UUID doctorId) {
        log.info("Fetching doctor by ID: {}", doctorId);

        Doctor doctor = doctorRepository.findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.DOCTOR_NOT_FOUND));

        log.info("Doctor fetched successfully. Doctor ID: {}", doctorId);

        return ApiResponse.success(AppConstants.DOCTOR_FETCHED_SUCCESSFULLY, DoctorMapper.toResponse(doctor));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<DoctorResponse>> getAllDoctors(int page, int size) {
        log.info("Fetching doctors page={}, size={}", page, size);

        Page<Doctor> result = doctorRepository.findAllByDeletedFalse(
                PageRequests.of(page, size, Sort.by(Sort.Direction.ASC, "doctorName")));
        PagedResponse<DoctorResponse> paged = PagedResponse.of(result.map(DoctorMapper::toResponse));

        log.info("Successfully fetched {} doctors (total={})",
                paged.getContent().size(), paged.getTotalElements());

        return ApiResponse.success(AppConstants.DOCTORS_FETCHED_SUCCESSFULLY, paged);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<DoctorResponse>> getActiveDoctors(int page, int size) {
        log.info("Fetching active doctors page={}, size={}", page, size);

        Page<Doctor> result = doctorRepository.findAllByStatusAndDeletedFalse(
                DoctorStatus.ACTIVE,
                PageRequests.of(page, size, Sort.by(Sort.Direction.ASC, "doctorName")));
        PagedResponse<DoctorResponse> paged = PagedResponse.of(result.map(DoctorMapper::toResponse));

        log.info("Successfully fetched {} active doctors (total={})",
                paged.getContent().size(), paged.getTotalElements());

        return ApiResponse.success(AppConstants.ACTIVE_DOCTORS_FETCHED_SUCCESSFULLY, paged);
    }

    @Override
    @Transactional
    public ApiResponse<DoctorResponse> updateDoctorStatus(
            UUID doctorId, UpdateDoctorStatusRequest request) {
        log.info("Updating doctor status. Doctor ID: {}, Status: {}", doctorId, request.getStatus());

        Doctor doctor = doctorRepository.findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.DOCTOR_NOT_FOUND));

        doctor.setStatus(request.getStatus());
        Doctor savedDoctor = doctorRepository.save(doctor);

        log.info("Doctor status updated successfully. Doctor ID: {}", doctorId);

        activityLogPublisher.record(
                "Doctors",
                ActivityActionType.UPDATED,
                "Doctor " + savedDoctor.getDoctorName(),
                null,
                String.valueOf(request.getStatus()));

        return ApiResponse.success(
                AppConstants.DOCTOR_STATUS_UPDATED_SUCCESSFULLY, DoctorMapper.toResponse(savedDoctor));
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteDoctor(UUID doctorId) {
        log.info("Deleting doctor. Doctor ID: {}", doctorId);

        Doctor doctor = doctorRepository.findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.DOCTOR_NOT_FOUND));

        doctor.setDeleted(true);
        doctorRepository.save(doctor);

        log.info("Doctor deleted successfully. Doctor ID: {}", doctorId);

        activityLogPublisher.record(
                "Doctors",
                ActivityActionType.DELETED,
                "Doctor " + doctor.getDoctorName());

        return ApiResponse.success(AppConstants.DOCTOR_DELETED_SUCCESSFULLY, null);
    }

}
