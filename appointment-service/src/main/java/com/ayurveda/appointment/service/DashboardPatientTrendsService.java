package com.ayurveda.appointment.service;

import com.ayurveda.appointment.dto.response.DashboardPatientTrendsResponse;
import com.ayurveda.common.ApiResponse;

public interface DashboardPatientTrendsService {

    /** Dashboard Total Patients chart: monthly new patients vs follow-ups. */
    ApiResponse<DashboardPatientTrendsResponse> getPatientTrends(Integer year);

}
