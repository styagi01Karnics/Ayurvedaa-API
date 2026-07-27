package com.ayurveda.appointment.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class DashboardTodaysScheduleResponse {

    private LocalDate date;
    private LocalDateTime currentDateTime;
    private ScheduleItemResponse ongoingAppointment;
    private ScheduleItemResponse nextAppointment;
    private long remainingToday;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleItemResponse {
        private UUID bookingId;
        private UUID patientId;
        private String patientName;
        private String serviceType;
        private BookingStatus bookingStatus;
    }

}
