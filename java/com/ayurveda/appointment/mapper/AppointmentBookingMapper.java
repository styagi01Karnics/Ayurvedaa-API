package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.entity.AppointmentBooking;

@Component
public class AppointmentBookingMapper {

    public AppointmentBooking toEntity(CreateAppointmentBookingRequest request) {

        if (request == null) {
            return null;
        }

        return AppointmentBooking.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .age(request.getAge())
                .preferredLanguage(request.getPreferredLanguage())
                .registrationDate(request.getRegistrationDate())
                .assignedDoctorId(request.getAssignedDoctorId())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .state(request.getState())
                .city(request.getCity())
                .permanentAddress(request.getPermanentAddress())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyRelationship(request.getEmergencyRelationship())
                .emergencyPhoneNumber(request.getEmergencyPhoneNumber())
//                .patientCode(request.getPatientCode())
                .idProofType(request.getIdProofType())
                .idProofNumber(request.getIdProofNumber())
                .occupation(request.getOccupation())
                .insuranceDetails(request.getInsuranceDetails())
//                .workflowStep(request.getWorkflowStep())
//                .bookingStatus(request.getBookingStatus())
                .build();
    }

    public AppointmentBookingResponse toResponse(AppointmentBooking entity) {

        if (entity == null) {
            return null;
        }

        return AppointmentBookingResponse.builder()
        		.id(entity.getId())
                .fullName(entity.getFullName())
                .gender(entity.getGender())
                .dateOfBirth(entity.getDateOfBirth())
                .age(entity.getAge())
                .preferredLanguage(entity.getPreferredLanguage())
                .registrationDate(entity.getRegistrationDate())
                .assignedDoctorId(entity.getAssignedDoctorId())
                .mobileNumber(entity.getMobileNumber())
                .email(entity.getEmail())
                .state(entity.getState())
                .city(entity.getCity())
                .permanentAddress(entity.getPermanentAddress())
                .emergencyContactName(entity.getEmergencyContactName())
                .emergencyRelationship(entity.getEmergencyRelationship())
                .emergencyPhoneNumber(entity.getEmergencyPhoneNumber())
                .patientCode(entity.getPatientCode())
                .idProofType(entity.getIdProofType())
                .idProofNumber(entity.getIdProofNumber())
                .occupation(entity.getOccupation())
                .insuranceDetails(entity.getInsuranceDetails())
                .workflowStep(entity.getWorkflowStep())
                .bookingStatus(entity.getBookingStatus())
                .build();
    }

}