package com.ayurveda.attendance.controller;

import com.ayurveda.attendance.dto.response.DailyEmployeeAttendanceResponse;
import com.ayurveda.attendance.service.DailyEmployeeAttendanceService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Daily Employee Attendance", description = "Daily attendance from device logs joined with employee master")
@RestController
@RequestMapping("/api/v1/employee-attendances")
@RequiredArgsConstructor
@Validated
public class DailyEmployeeAttendanceController {

    private final DailyEmployeeAttendanceService dailyEmployeeAttendanceService;

    @Operation(
            summary = "Get daily employee attendance",
            description = "Returns all employee master records with that day's check-in, check-out, and punches from device_attendance_logs. Defaults to today when date is omitted.")
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<DailyEmployeeAttendanceResponse>>> getDailyAttendance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(dailyEmployeeAttendanceService.getDailyAttendance(date));
    }

}
