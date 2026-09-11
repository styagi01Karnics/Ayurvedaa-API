package com.ayurveda.patient.dto.response;

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
public class DashboardNewPatientsMonthlyResponse {

    private int year;
    private List<MonthlyCountResponse> months;

}
