package com.ayurveda.appointment.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.client.PatientServiceClient;
import com.ayurveda.appointment.dto.response.DashboardPatientTrendPointResponse;
import com.ayurveda.appointment.dto.response.DashboardPatientTrendsResponse;
import com.ayurveda.appointment.dto.response.MonthlyNewPatientsClientResponse;
import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.FollowUpStatus;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.FollowUpRepository;
import com.ayurveda.appointment.service.DashboardPatientTrendsService;
import com.ayurveda.appointment.util.AppMessages;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.util.ResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardPatientTrendsServiceImpl implements DashboardPatientTrendsService {

    private final PatientServiceClient patientServiceClient;
    private final FollowUpRepository followUpRepository;
    private final AppointmentBookingRepository appointmentBookingRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DashboardPatientTrendsResponse> getPatientTrends(Integer year) {
        int resolvedYear = resolveYear(year);
        LocalDate fromDate = LocalDate.of(resolvedYear, 1, 1);
        LocalDate toDate = LocalDate.of(resolvedYear + 1, 1, 1);
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atStartOfDay();

        log.info("Fetching dashboard patient trends for year {}", resolvedYear);

        Map<Integer, Long> newPatients = loadNewPatientsByMonth(resolvedYear, fromDate, toDate);
        Map<Integer, Long> followUps = toMonthCounts(
                followUpRepository.countByAppointmentMonth(from, to, FollowUpStatus.CANCELLED));

        List<DashboardPatientTrendPointResponse> points = new ArrayList<>();
        long totalNew = 0;
        long totalFollowUps = 0;
        for (int month = 1; month <= 12; month++) {
            long newCount = newPatients.getOrDefault(month, 0L);
            long followUpCount = followUps.getOrDefault(month, 0L);
            totalNew += newCount;
            totalFollowUps += followUpCount;
            points.add(DashboardPatientTrendPointResponse.builder()
                    .month(Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .monthNumber(month)
                    .newPatients(newCount)
                    .followUps(followUpCount)
                    .build());
        }

        return ResponseUtil.success(
                AppMessages.DASHBOARD_PATIENT_TRENDS_FETCHED,
                DashboardPatientTrendsResponse.builder()
                        .year(resolvedYear)
                        .totalNewPatients(totalNew)
                        .totalFollowUps(totalFollowUps)
                        .points(points)
                        .build());
    }

    private Map<Integer, Long> loadNewPatientsByMonth(int year, LocalDate fromDate, LocalDate toDate) {
        try {
            ApiResponse<MonthlyNewPatientsClientResponse> response =
                    patientServiceClient.getNewPatientsByMonth(year);
            MonthlyNewPatientsClientResponse data = response != null ? response.getData() : null;
            if (data != null && data.getMonths() != null) {
                Map<Integer, Long> byMonth = new HashMap<>();
                for (MonthlyNewPatientsClientResponse.MonthCount row : data.getMonths()) {
                    byMonth.put(row.getMonthNumber(), row.getCount());
                }
                return byMonth;
            }
        } catch (Exception ex) {
            log.warn("Patient-service monthly counts unavailable; using first-visit fallback: {}",
                    ex.getMessage());
        }
        return toMonthCounts(appointmentBookingRepository.countFirstVisitsByMonth(
                fromDate, toDate, BookingStatus.CANCELLED));
    }

    static Map<Integer, Long> toMonthCounts(List<Object[]> rows) {
        Map<Integer, Long> byMonth = new HashMap<>();
        if (rows == null) {
            return byMonth;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 2 || row[0] == null || row[1] == null) {
                continue;
            }
            byMonth.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        return byMonth;
    }

    static int resolveYear(Integer year) {
        int current = LocalDate.now().getYear();
        int resolved = year != null ? year : current;
        if (resolved < 2000 || resolved > current + 1) {
            throw new BadRequestException("Year must be between 2000 and " + (current + 1) + ".");
        }
        return resolved;
    }

}
