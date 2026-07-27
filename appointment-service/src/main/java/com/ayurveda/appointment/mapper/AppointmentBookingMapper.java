package com.ayurveda.appointment.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.dto.response.DoctorSummaryResponse;
import com.ayurveda.appointment.dto.response.PatientSummaryResponse;
import com.ayurveda.appointment.entity.AppointmentBooking;

@Component
public class AppointmentBookingMapper {

    public AppointmentBooking toEntity(CreateAppointmentBookingRequest request, UUID patientId) {
        if (request == null) {
            return null;
        }

        return AppointmentBooking.builder()
                .patientId(patientId)
                .registrationDate(request.getRegistrationDate())
                .assignedDoctorId(request.getAssignedDoctorId())
                .build();
    }

    public AppointmentBookingResponse toResponse(
            AppointmentBooking entity,
            PatientSummaryResponse patient,
            DoctorSummaryResponse doctor) {
        if (entity == null) {
            return null;
        }

        return AppointmentBookingResponse.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .patient(patient)
                .registrationDate(entity.getRegistrationDate())
                .assignedDoctorId(entity.getAssignedDoctorId())
                .assignedDoctor(doctor)
                .bookingStatus(entity.getBookingStatus())
                .build();
    }

}
