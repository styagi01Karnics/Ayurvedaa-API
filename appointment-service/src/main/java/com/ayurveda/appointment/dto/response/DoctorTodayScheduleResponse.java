package com.ayurveda.appointment.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private long totalAppointments;

    private List<DoctorTodayAppointmentResponse> appointments;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorTodayAppointmentResponse {

        private UUID bookingId;

        /** When the appointment was booked (ascending sort key). */
        private LocalDateTime bookingTime;

        private BookingStatus bookingStatus;

        private UUID patientId;

        private String patientName;

        private String patientMobileNumber;

        private List<String> consultationTypes;

    }

}
