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
public class DoctorTodayScheduleResponse {

    private UUID doctorId;

    private LocalDate date;

    /** Total matching appointments today (before paging). */
    private long totalAppointments;

    private int page;

    private int size;

    private int totalPages;

    private List<DoctorTodayAppointmentResponse> appointments;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorTodayAppointmentResponse {

        private UUID bookingId;

        private UUID assignedDoctorId;

        private LocalTime slotTime;

        /** Appointment date + slot time (falls back to createdAt if slot missing). */
        private LocalDateTime bookingTime;

        private BookingStatus bookingStatus;

        private UUID patientId;

        private String patientName;

        private String patientMobileNumber;

        private List<ConsultationTypeItemResponse> consultationTypes;

    }

}
