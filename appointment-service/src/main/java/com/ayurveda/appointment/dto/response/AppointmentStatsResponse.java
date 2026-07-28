package com.ayurveda.appointment.dto.response;

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
public class AppointmentStatsResponse {

    private long currentMonthAppointmentCount;

    private long completedCount;

    private long ongoingCount;

    private long todayAppointmentCount;

}
