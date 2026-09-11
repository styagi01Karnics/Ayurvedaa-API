package com.ayurveda.appointment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayurveda.appointment.client.PatientServiceClient;
import com.ayurveda.appointment.dto.response.DashboardPatientTrendsResponse;
import com.ayurveda.appointment.dto.response.MonthlyNewPatientsClientResponse;
import com.ayurveda.appointment.enums.FollowUpStatus;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.FollowUpRepository;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
class DashboardPatientTrendsServiceImplTest {

    @Mock
    private PatientServiceClient patientServiceClient;

    @Mock
    private FollowUpRepository followUpRepository;

    @Mock
    private AppointmentBookingRepository appointmentBookingRepository;

    @InjectMocks
    private DashboardPatientTrendsServiceImpl service;

    @Test
    void getPatientTrendsMergesNewPatientsAndFollowUpsForAllMonths() {
        int year = LocalDate.now().getYear();
        MonthlyNewPatientsClientResponse patientMonths = MonthlyNewPatientsClientResponse.builder()
                .year(year)
                .months(List.of(
                        MonthlyNewPatientsClientResponse.MonthCount.builder().monthNumber(3).count(5).build(),
                        MonthlyNewPatientsClientResponse.MonthCount.builder().monthNumber(10).count(2).build()))
                .build();
        when(patientServiceClient.getNewPatientsByMonth(year))
                .thenReturn(ApiResponse.success(patientMonths));
        when(followUpRepository.countByAppointmentMonth(any(), any(), eq(FollowUpStatus.CANCELLED)))
                .thenReturn(List.<Object[]>of(new Object[] { 3, 4L }, new Object[] { 6, 1L }));

        DashboardPatientTrendsResponse data = service.getPatientTrends(year).getData();

        assertEquals(year, data.getYear());
        assertEquals(12, data.getPoints().size());
        assertEquals("Jan", data.getPoints().get(0).getMonth());
        assertEquals(5, data.getPoints().get(2).getNewPatients());
        assertEquals(4, data.getPoints().get(2).getFollowUps());
        assertEquals(1, data.getPoints().get(5).getFollowUps());
        assertEquals(7, data.getTotalNewPatients());
        assertEquals(5, data.getTotalFollowUps());
    }

    @Test
    void getPatientTrendsFallsBackToFirstVisitsWhenPatientServiceFails() {
        int year = LocalDate.now().getYear();
        when(patientServiceClient.getNewPatientsByMonth(anyInt()))
                .thenThrow(new RuntimeException("patient-service down"));
        when(appointmentBookingRepository.countFirstVisitsByMonth(any(), any(), any()))
                .thenReturn(List.<Object[]>of(new Object[] { 1, 3L }));
        when(followUpRepository.countByAppointmentMonth(any(), any(), eq(FollowUpStatus.CANCELLED)))
                .thenReturn(List.of());

        DashboardPatientTrendsResponse data = service.getPatientTrends(year).getData();

        assertEquals(3, data.getPoints().get(0).getNewPatients());
        assertEquals(3, data.getTotalNewPatients());
    }

    @Test
    void resolveYearRejectsOutOfRange() {
        assertThrows(BadRequestException.class, () -> service.getPatientTrends(1999));
    }
}
