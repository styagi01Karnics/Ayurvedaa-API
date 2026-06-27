package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateDoctorRequest;
import com.ayurveda.appointment.dto.response.DoctorResponse;
import com.ayurveda.appointment.entity.DoctorMaster;
import com.ayurveda.appointment.exception.DuplicateResourceException;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.DoctorMapper;
import com.ayurveda.appointment.repository.DoctorMasterRepository;
import com.ayurveda.appointment.service.DoctorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorMasterRepository doctorMasterRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public ApiResponse<DoctorResponse> createDoctor(CreateDoctorRequest request) {

        log.info("Creating doctor with code : {}", request.getDoctorCode());

        if (doctorMasterRepository.existsByDoctorCode(request.getDoctorCode())) {
            throw new DuplicateResourceException(
                    "Doctor already exists with code : " + request.getDoctorCode());
        }

        if (doctorMasterRepository.existsByDoctorName(request.getDoctorName())) {
            throw new DuplicateResourceException(
                    "Doctor already exists with name : " + request.getDoctorName());
        }

        DoctorMaster doctor = doctorMapper.toEntity(request);

        DoctorMaster savedDoctor = doctorMasterRepository.save(doctor);

        log.info("Doctor created successfully with id : {}", savedDoctor.getId());

        DoctorResponse response = doctorMapper.toResponse(savedDoctor);

        return ApiResponse.success(
                "Doctor created successfully",
                response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DoctorResponse> getDoctorById(UUID doctorId) {

        log.info("Fetching doctor with id : {}", doctorId);

        DoctorMaster doctor = doctorMasterRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id : " + doctorId));

        DoctorResponse response = doctorMapper.toResponse(doctor);

        return ApiResponse.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorResponse>> getAllDoctors() {

        log.info("Fetching all doctors");

        List<DoctorResponse> response = doctorMasterRepository.findAll()
                .stream()
                .map(doctorMapper::toResponse)
                .toList();

        return ApiResponse.success(response);
    }

}