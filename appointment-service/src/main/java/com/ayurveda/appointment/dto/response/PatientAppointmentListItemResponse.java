package com.ayurveda.appointment.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class PatientAppointmentListItemResponse {

    private UUID bookingId;
    private UUID patientId;

    /** e.g. GAN-DL-PT-00001 */
    private String patientCode;

    private String patientFullName;
    private String patientMobileNumber;

    private UUID assignedDoctorId;
    private String doctorName;

    private List<ConsultationTypeItemResponse> consultationTypes;
    private LocalDate appointmentDate;
    private LocalTime slotTime;
    private LocalDateTime bookingTime;

    private UUID doshaId;
    private String doshaName;

    private BookingStatus bookingStatus;

}
