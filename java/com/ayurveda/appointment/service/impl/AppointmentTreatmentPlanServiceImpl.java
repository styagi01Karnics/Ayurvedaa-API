package com.ayurveda.appointment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateAppointmentTreatmentPlanRequest;
import com.ayurveda.appointment.dto.response.AppointmentTreatmentPlanResponse;
import com.ayurveda.appointment.entity.AppointmentTreatmentPlan;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.AppointmentTreatmentPlanMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentTreatmentPlanRepository;
import com.ayurveda.appointment.service.AppointmentTreatmentPlanService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentTreatmentPlanServiceImpl
        implements AppointmentTreatmentPlanService {

    private final AppointmentTreatmentPlanRepository
            appointmentTreatmentPlanRepository;

    private final AppointmentBookingRepository
            appointmentBookingRepository;

    private final AppointmentTreatmentPlanMapper
            appointmentTreatmentPlanMapper;

    @Override
    public ApiResponse<AppointmentTreatmentPlanResponse> saveTreatmentPlan(
            CreateAppointmentTreatmentPlanRequest request) {

        log.info("Saving treatment plan for booking: {}",
                request.getBookingId());

        appointmentBookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        Constants.APPOINTMENT_NOT_FOUND
                                + request.getBookingId()));

        AppointmentTreatmentPlan treatmentPlan =
                appointmentTreatmentPlanRepository
                        .findByBookingId(request.getBookingId())
                        .orElse(null);

        String message;

        if (treatmentPlan == null) {

            treatmentPlan =
                    appointmentTreatmentPlanMapper.toEntity(request);

            message = Constants.TREATMENT_PLAN_CREATED;

        } else {

            appointmentTreatmentPlanMapper
                    .updateEntity(treatmentPlan, request);

            message = Constants.TREATMENT_PLAN_UPDATED;
        }

        AppointmentTreatmentPlan savedTreatmentPlan =
                appointmentTreatmentPlanRepository
                        .save(treatmentPlan);

        AppointmentTreatmentPlanResponse response =
                appointmentTreatmentPlanMapper
                        .toResponse(savedTreatmentPlan);

        return ApiResponse.success(message, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentTreatmentPlanResponse>
            getTreatmentPlanByBookingId(UUID bookingId) {

        log.info("Fetching treatment plan for booking: {}",
                bookingId);

        AppointmentTreatmentPlan treatmentPlan =
                appointmentTreatmentPlanRepository
                        .findByBookingId(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                Constants.TREATMENT_PLAN_NOT_FOUND
                                        + bookingId));

        AppointmentTreatmentPlanResponse response =
                appointmentTreatmentPlanMapper
                        .toResponse(treatmentPlan);

        return ApiResponse.success(
                Constants.TREATMENT_PLAN_FETCHED,
                response);
    }

}