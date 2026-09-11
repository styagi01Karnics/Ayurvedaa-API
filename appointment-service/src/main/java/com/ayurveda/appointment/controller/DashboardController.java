package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.appointment.dto.response.DashboardPatientTrendsResponse;
import com.ayurveda.appointment.dto.response.DashboardTodaysScheduleResponse;
import com.ayurveda.appointment.service.AppointmentBookingService;
import com.ayurveda.appointment.service.DashboardPatientTrendsService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(
        name = "Dashboard",
        description = "APIs used on the main Dashboard page (Today's Schedule and related widgets).")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Validated
public class DashboardController {

    private final AppointmentBookingService appointmentBookingService;
    private final DashboardPatientTrendsService dashboardPatientTrendsService;

    @Operation(
            summary = "Dashboard – Today's Schedule card",
            description = """
                    For the Dashboard page "Today's Schedule" widget.

                    Returns:
                    - currentDateTime
                    - ongoingAppointment (IN_CONSULTATION): patient name + service type
                    - nextAppointment (SCHEDULED/RESCHEDULED): patient name + service type
                    - remainingToday: count of today's not-yet-completed appointments

                    Optional doctorId filters the schedule for one doctor.
                    """)
    @GetMapping("/todays-schedule")
    public ResponseEntity<ApiResponse<DashboardTodaysScheduleResponse>> getTodaysSchedule(
            @RequestParam(required = false) UUID doctorId) {

        return ResponseEntity.ok(appointmentBookingService.getDashboardTodaysSchedule(doctorId));
    }

    @Operation(
            summary = "Dashboard – Total Patients chart",
            description = """
                    Monthly New Patients vs Follow Ups for the Dashboard Total Patients widget.

                    - newPatients: patients registered that month (patient-service)
                    - followUps: follow-up visits scheduled/recorded that month
                    - points: 12 months (Jan–Dec), zeros when empty

                    Optional year defaults to the current year.
                    """)
    @GetMapping("/patient-trends")
    public ResponseEntity<ApiResponse<DashboardPatientTrendsResponse>> getPatientTrends(
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(dashboardPatientTrendsService.getPatientTrends(year));
    }

}
