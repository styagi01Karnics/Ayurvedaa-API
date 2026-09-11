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
public class DashboardPatientTrendPointResponse {

    /** Short month label for the chart X-axis, e.g. Jan. */
    private String month;
    private int monthNumber;
    private long newPatients;
    private long followUps;

}
