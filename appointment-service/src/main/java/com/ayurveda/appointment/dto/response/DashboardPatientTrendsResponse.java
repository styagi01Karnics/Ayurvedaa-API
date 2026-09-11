package com.ayurveda.appointment.dto.response;

import java.util.List;

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
public class DashboardPatientTrendsResponse {

    private int year;
    private long totalNewPatients;
    private long totalFollowUps;
    private List<DashboardPatientTrendPointResponse> points;

}
