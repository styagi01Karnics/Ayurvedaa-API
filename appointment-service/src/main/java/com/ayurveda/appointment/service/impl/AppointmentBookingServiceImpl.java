package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.client.DoctorServiceClient;
import com.ayurveda.appointment.client.PatientServiceClient;
import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.request.CreatePatientClientRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.dto.response.DoctorSummaryResponse;
import com.ayurveda.appointment.dto.response.PatientSummaryResponse;
import com.ayurveda.appointment.entity.AppointmentBooking;
import com.ayurveda.appointment.entity.AppointmentConsultationType;
import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.ConsultationType;
import com.ayurveda.appointment.enums.WorkflowStep;
import com.ayurveda.appointment.mapper.AppointmentBookingMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentConsultationTypeRepository;
import com.ayurveda.appointment.service.AppointmentBookingService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentBookingServiceImpl implements AppointmentBookingService {

    private final AppointmentBookingRepository appointmentBookingRepository;
    private final AppointmentBookingMapper appointmentBookingMapper;
    private final AppointmentConsultationTypeRepository appointmentConsultationTypeRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;

    @Override
    public ApiResponse<AppointmentBookingResponse> createAppointment(
            CreateAppointmentBookingRequest request) {

        log.info("Starting appointment booking for patient: {}", request.getPatient().getFullName());

        PatientSummaryResponse patient = resolvePatient(request);
        DoctorSummaryResponse doctor = fetchDoctor(request.getAssignedDoctorId());

        AppointmentBooking appointment =
                appointmentBookingMapper.toEntity(request, patient.getId());
        appointment.setWorkflowStep(WorkflowStep.STEP_TWO);
        appointment.setBookingStatus(BookingStatus.DRAFT);

        AppointmentBooking savedAppointment = appointmentBookingRepository.save(appointment);

        request.getConsultationTypes().forEach(type -> {
            AppointmentConsultationType consultation = AppointmentConsultationType.builder()
                    .bookingId(savedAppointment.getId())
                    .consultationType(ConsultationType.valueOf(type))
                    .build();
            appointmentConsultationTypeRepository.save(consultation);
        });

        log.info("Patient saved and appointment created with id: {}", savedAppointment.getId());

        AppointmentBookingResponse response =
                appointmentBookingMapper.toResponse(savedAppointment, patient, doctor);
        response.setConsultationTypes(request.getConsultationTypes());

        return ApiResponse.success(
                "Patient saved and appointment created successfully. Proceed to therapy details.",
                response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AppointmentBookingResponse> getAppointmentById(UUID bookingId) {

        log.info("Fetching appointment with id: {}", bookingId);

        AppointmentBooking appointment = appointmentBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + bookingId));

        PatientSummaryResponse patient = fetchPatient(appointment.getPatientId());
        DoctorSummaryResponse doctor = fetchDoctor(appointment.getAssignedDoctorId());

        AppointmentBookingResponse response =
                appointmentBookingMapper.toResponse(appointment, patient, doctor);

        List<String> consultationTypes = appointmentConsultationTypeRepository.findByBookingId(bookingId)
                .stream()
                .map(consultation -> consultation.getConsultationType().name())
                .toList();

        response.setConsultationTypes(consultationTypes);

        return ApiResponse.success(response);
    }

    private PatientSummaryResponse resolvePatient(CreateAppointmentBookingRequest request) {
        if (request.getPatientId() != null) {
            return fetchPatient(request.getPatientId());
        }

        ApiResponse<PatientSummaryResponse> patientResponse = patientServiceClient.createPatient(
                CreatePatientClientRequest.from(request.getPatient()));

        if (patientResponse == null || !patientResponse.isSuccess() || patientResponse.getData() == null) {
            throw new ResourceNotFoundException("Unable to save patient details.");
        }

        return patientResponse.getData();
    }

    private PatientSummaryResponse fetchPatient(UUID patientId) {
        ApiResponse<PatientSummaryResponse> patientResponse = patientServiceClient.getPatientById(patientId);
        if (patientResponse == null || !patientResponse.isSuccess() || patientResponse.getData() == null) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return patientResponse.getData();
    }

    private DoctorSummaryResponse fetchDoctor(UUID doctorId) {
        ApiResponse<DoctorSummaryResponse> doctorResponse = doctorServiceClient.getDoctorById(doctorId);
        if (doctorResponse == null || !doctorResponse.isSuccess() || doctorResponse.getData() == null) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }
        return doctorResponse.getData();
    }

}
