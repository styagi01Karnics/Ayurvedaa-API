package com.ayurveda.doctor.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.activity.ActivityActionType;
import com.ayurveda.common.activity.ActivityLogPublisher;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.dto.PagedResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.util.PageRequests;
import com.ayurveda.doctor.dto.request.AssignDoctorUserCodeRequest;
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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private static final String DOCTOR_USER_CODE_ALREADY_EXISTS = "Doctor user code already exists: ";
    private static final String DOCTOR_USER_CODE_ALREADY_ASSIGNED = "Doctor already has user code: ";
    private static final String DOCTOR_USER_CODE_ASSIGNED = "Doctor user code assigned successfully.";

    private final DoctorRepository doctorRepository;
    private final DoctorCodeGenerator doctorCodeGenerator;
    private final ActivityLogPublisher activityLogPublisher;

    @Override
    @Transactional
    public ApiResponse<DoctorResponse> createDoctor(CreateDoctorRequest request) {
        log.info("Creating doctor: {}", request.getName());

        String userCode = StringUtils.hasText(request.getUserCode()) ? request.getUserCode().trim() : null;
        if (userCode != null && doctorRepository.existsByUserCodeIgnoreCaseAndDeletedFalse(userCode)) {
            throw new DuplicateResourceException(DOCTOR_USER_CODE_ALREADY_EXISTS + userCode);
        }

        DoctorStatus status = request.getStatus() != null ? request.getStatus() : DoctorStatus.ACTIVE;

        Doctor doctor = Doctor.builder()
                .doctorName(request.getName().trim())
                .doctorCode(doctorCodeGenerator.generate())
                .mobileNumber(request.getMobileNumber().trim())
                .email(request.getEmail().trim().toLowerCase())
                .specialization(request.getSpecialization().trim())
                .qualification(StringUtils.hasText(request.getQualification()) ? request.getQualification().trim() : null)
                .status(status)
                .consultationFees(request.getConsultationFees())
                .followUpFees(request.getFollowUpFees())
                .availability(request.getAvailability().trim())
                .userCode(userCode)
                .build();

        Doctor savedDoctor = doctorRepository.saveAndFlush(doctor);

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
                PageRequests.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
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
                PageRequests.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        PagedResponse<DoctorResponse> paged = PagedResponse.of(result.map(DoctorMapper::toResponse));

        log.info("Successfully fetched {} active doctors (total={})",
                paged.getContent().size(), paged.getTotalElements());

        return ApiResponse.success(AppConstants.ACTIVE_DOCTORS_FETCHED_SUCCESSFULLY, paged);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorResponse>> getActiveDoctorsWithoutUserCode() {
        log.info("Fetching active doctors without user code");

        List<DoctorResponse> doctors = doctorRepository
                .findAllByStatusAndDeletedFalseAndUserCodeIsNullOrderByCreatedAtDesc(DoctorStatus.ACTIVE)
                .stream()
                .map(DoctorMapper::toResponse)
                .toList();

        log.info("Successfully fetched {} active doctors without user code", doctors.size());

        return ApiResponse.success(AppConstants.ACTIVE_DOCTORS_FETCHED_SUCCESSFULLY, doctors);
    }

    @Override
    @Transactional
    public ApiResponse<DoctorResponse> assignUserCode(UUID doctorId, AssignDoctorUserCodeRequest request) {
        log.info("Assigning user code to doctor. Doctor ID: {}", doctorId);

        Doctor doctor = doctorRepository.findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.DOCTOR_NOT_FOUND));

        if (StringUtils.hasText(doctor.getUserCode())) {
            throw new BadRequestException(DOCTOR_USER_CODE_ALREADY_ASSIGNED + doctor.getUserCode());
        }

        String userCode = request.getUserCode().trim();
        if (doctorRepository.existsByUserCodeIgnoreCaseAndDeletedFalse(userCode)) {
            throw new DuplicateResourceException(DOCTOR_USER_CODE_ALREADY_EXISTS + userCode);
        }

        doctor.setUserCode(userCode);
        Doctor savedDoctor = doctorRepository.saveAndFlush(doctor);

        log.info("User code assigned to doctor. Doctor ID: {}", doctorId);

        return ApiResponse.success(DOCTOR_USER_CODE_ASSIGNED, DoctorMapper.toResponse(savedDoctor));
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

    @Override
    public ApiResponse<DoctorResponse> updateDoctorData(UUID doctorId, CreateDoctorRequest request) {
        log.info("Updating doctor data. Doctor ID: {}", doctorId);

        return doctorRepository.findByIdAndDeletedFalse(doctorId)
                .map(existingDoctor -> {
                    existingDoctor.setDoctorName(request.getName());
                    existingDoctor.setSpecialization(request.getSpecialization());
                    existingDoctor.setQualification(StringUtils.hasText(request.getQualification()) ? request.getQualification().trim() : null);
                    if (request.getStatus() != null) {
                        existingDoctor.setStatus(request.getStatus());
                    }
                    existingDoctor.setConsultationFees(request.getConsultationFees());
                    existingDoctor.setFollowUpFees(request.getFollowUpFees());
                    existingDoctor.setAvailability(request.getAvailability());
                    existingDoctor.setMobileNumber(request.getMobileNumber());
                    existingDoctor.setEmail(request.getEmail());
                    existingDoctor.setUpdatedAt(java.time.LocalDateTime.now());
                    return doctorRepository.save(existingDoctor);
                })
                .map(savedDoctor -> {
                    log.info("Doctor data updated successfully. Doctor ID: {}", savedDoctor.getId());
                    return ApiResponse.success("Doctor data updated successfully with id: " + savedDoctor.getId(), DoctorMapper.toResponse(savedDoctor));
                })
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.DOCTOR_NOT_FOUND));

    }

}
