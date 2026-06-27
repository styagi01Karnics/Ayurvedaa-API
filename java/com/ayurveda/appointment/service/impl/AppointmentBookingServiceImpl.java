package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.entity.AppointmentBooking;
import com.ayurveda.appointment.entity.AppointmentConsultationType;
import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.ConsultationType;
import com.ayurveda.appointment.enums.WorkflowStep;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.AppointmentBookingMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentConsultationTypeRepository;
import com.ayurveda.appointment.repository.DoctorMasterRepository;
import com.ayurveda.appointment.service.AppointmentBookingService;
import com.ayurveda.appointment.util.PatientCodeGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentBookingServiceImpl implements AppointmentBookingService {

    private final AppointmentBookingRepository appointmentBookingRepository;
    private final AppointmentBookingMapper appointmentBookingMapper;
    private final DoctorMasterRepository doctorMasterRepository;
    private final PatientCodeGenerator patientCodeGenerator;
    private final AppointmentConsultationTypeRepository appointmentConsultationTypeRepository;

    @Override
    public ApiResponse<AppointmentBookingResponse> createAppointment(
            CreateAppointmentBookingRequest request) {

        log.info("Creating appointment for patient: {}", request.getFullName());

        // Validate doctor exists
        doctorMasterRepository.findById(request.getAssignedDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + request.getAssignedDoctorId()));

        AppointmentBooking appointment =
                appointmentBookingMapper.toEntity(request);

        appointment.setPatientCode(patientCodeGenerator.generatePatientCode());
        appointment.setWorkflowStep(WorkflowStep.STEP_ONE);
        appointment.setBookingStatus(BookingStatus.DRAFT);

        AppointmentBooking savedAppointment =
                appointmentBookingRepository.save(appointment);
        
        if (request.getConsultationTypes() != null
                && !request.getConsultationTypes().isEmpty()) {

            request.getConsultationTypes().forEach(type -> {

                AppointmentConsultationType consultation =
                        AppointmentConsultationType.builder()
                                .bookingId(savedAppointment.getId())
                                .consultationType(ConsultationType.valueOf(type))
                                .build();

                appointmentConsultationTypeRepository.save(consultation);
            });
        }

        log.info("Appointment created successfully with id: {}",
                savedAppointment.getId());

        AppointmentBookingResponse response =
                appointmentBookingMapper.toResponse(savedAppointment);
        response.setConsultationTypes(request.getConsultationTypes());

        return ApiResponse.success(
                "Appointment created successfully",
                response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentBookingResponse> getAppointmentById(
            UUID bookingId) {

        log.info("Fetching appointment with id: {}", bookingId);

        AppointmentBooking appointment =
                appointmentBookingRepository.findById(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Appointment not found with id: " + bookingId));

        AppointmentBookingResponse response =
                appointmentBookingMapper.toResponse(appointment);

        List<String> consultationTypes =
                appointmentConsultationTypeRepository
                        .findByBookingId(bookingId)
                        .stream()
                        .map(consultation -> consultation.getConsultationType().name())
                        .toList();

        response.setConsultationTypes(consultationTypes);

        return ApiResponse.success(response);
    }
}