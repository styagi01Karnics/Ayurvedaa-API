package com.ayurveda.appointment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentLifestyleInformationRequest;
import com.ayurveda.appointment.dto.response.AppointmentLifestyleInformationResponse;
import com.ayurveda.appointment.entity.AppointmentLifestyleInformation;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.AppointmentLifestyleInformationMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentLifestyleInformationRepository;
import com.ayurveda.appointment.service.AppointmentLifestyleInformationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentLifestyleInformationServiceImpl
        implements AppointmentLifestyleInformationService {

    private final AppointmentLifestyleInformationRepository
            appointmentLifestyleInformationRepository;

    private final AppointmentBookingRepository
            appointmentBookingRepository;

    private final AppointmentLifestyleInformationMapper
            appointmentLifestyleInformationMapper;

    @Override
    public ApiResponse<AppointmentLifestyleInformationResponse> saveLifestyleInformation(
            CreateAppointmentLifestyleInformationRequest request) {

        log.info("Saving lifestyle information for patient : {}",
                request.getPatientId());

        if (!appointmentBookingRepository.existsByPatientId(request.getPatientId())) {
            throw new ResourceNotFoundException("Appointment not found for patient id : " + request.getPatientId());
        }

        AppointmentLifestyleInformation lifestyleInformation =
                appointmentLifestyleInformationRepository
                        .findByPatientId(request.getPatientId())
                        .orElse(null);

        String message;

        if (lifestyleInformation == null) {

            lifestyleInformation =
                    appointmentLifestyleInformationMapper
                            .toEntity(request);

            message = "Lifestyle information created successfully";

        } else {

            appointmentLifestyleInformationMapper
                    .updateEntity(lifestyleInformation, request);

            message = "Lifestyle information updated successfully";
        }

        AppointmentLifestyleInformation savedLifestyleInformation =
                appointmentLifestyleInformationRepository
                        .save(lifestyleInformation);

        AppointmentLifestyleInformationResponse response =
                appointmentLifestyleInformationMapper
                        .toResponse(savedLifestyleInformation);

        return ApiResponse.success(message, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentLifestyleInformationResponse>
            getLifestyleInformationByPatientId(UUID patientId) {

        log.info("Fetching lifestyle information for patient : {}",
                patientId);

        AppointmentLifestyleInformation lifestyleInformation =
                appointmentLifestyleInformationRepository
                        .findByPatientId(patientId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Lifestyle information not found for patient : "
                                        + patientId));

        AppointmentLifestyleInformationResponse response =
                appointmentLifestyleInformationMapper
                        .toResponse(lifestyleInformation);

        return ApiResponse.success(response);
    }

}