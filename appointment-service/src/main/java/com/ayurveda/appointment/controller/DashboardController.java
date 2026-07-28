package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.appointment.dto.response.DashboardTodaysScheduleResponse;
import com.ayurveda.appointment.service.AppointmentBookingService;
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

}
