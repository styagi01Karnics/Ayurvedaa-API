package com.ayurveda.attendance.controller;

import com.ayurveda.attendance.dto.request.CheckOutRequest;
import com.ayurveda.attendance.dto.request.CreateAttendanceRequest;
import com.ayurveda.attendance.dto.request.UpdateAttendanceStatusRequest;
import com.ayurveda.attendance.dto.response.AttendanceResponse;
import com.ayurveda.attendance.service.AttendanceService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Attendance Management", description = "Manual attendance management APIs")
@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
@Validated
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "Check in employee")
    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(
            @Valid @RequestBody CreateAttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.checkIn(request));
    }

    @Operation(summary = "Check out employee")
    @PutMapping("/{attendanceId}/check-out")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(
            @PathVariable UUID attendanceId,
            @RequestBody(required = false) CheckOutRequest request) {
        return ResponseEntity.ok(attendanceService.checkOut(attendanceId,
                request != null ? request : new CheckOutRequest()));
    }

    @Operation(summary = "Update attendance status")
    @PutMapping("/{attendanceId}/status")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateStatus(
            @PathVariable UUID attendanceId,
            @Valid @RequestBody UpdateAttendanceStatusRequest request) {
        return ResponseEntity.ok(attendanceService.updateStatus(attendanceId, request));
    }

    @Operation(summary = "Get attendance by ID")
    @GetMapping("/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendanceById(
            @PathVariable UUID attendanceId) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(attendanceId));
    }

    @Operation(summary = "List all attendances")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAllAttendances() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @Operation(summary = "List attendances by employee ID")
    @GetMapping("/employee/{empId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendancesByEmpId(
            @PathVariable String empId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByEmpId(empId));
    }

    @Operation(summary = "Delete attendance")
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable UUID attendanceId) {
        return ResponseEntity.ok(attendanceService.deleteAttendance(attendanceId));
    }

}
