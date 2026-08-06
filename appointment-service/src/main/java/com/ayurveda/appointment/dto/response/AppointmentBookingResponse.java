package com.ayurveda.appointment.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookingResponse {

    private UUID id;

    private UUID patientId;

    private PatientSummaryResponse patient;

    private LocalDate registrationDate;

    private LocalTime slotTime;

    private UUID assignedDoctorId;

    private DoctorSummaryResponse assignedDoctor;

    private List<ConsultationTypeItemResponse> consultationTypes;

    private BookingStatus bookingStatus;

}
