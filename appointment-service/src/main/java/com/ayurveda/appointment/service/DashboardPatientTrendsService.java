package com.ayurveda.appointment.service;

import java.util.UUID;

import com.ayurveda.appointment.dto.response.DashboardPatientTrendsResponse;
import com.ayurveda.appointment.dto.response.DoctorPatientCountResponse;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.DashboardPeriod;

public interface DashboardPatientTrendsService {

    /** Dashboard Total Patients chart: monthly new patients vs follow-ups. */
    ApiResponse<DashboardPatientTrendsResponse> getPatientTrends(Integer year);

    /** Same chart limited to one doctor's appointments and follow-ups. */
    ApiResponse<DashboardPatientTrendsResponse> getPatientTrends(Integer year, UUID doctorId);

    /** New patients (first non-cancelled visit with this doctor) within the period. */
    ApiResponse<DoctorPatientCountResponse> getDoctorPatientCount(UUID doctorId, DashboardPeriod period);

}
