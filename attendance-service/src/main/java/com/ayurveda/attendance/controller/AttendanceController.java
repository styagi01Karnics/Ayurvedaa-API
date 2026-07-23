package com.ayurveda.attendance.controller;

import com.ayurveda.attendance.dto.request.CheckOutRequest;
import com.ayurveda.attendance.dto.request.CreateAttendanceRequest;
import com.ayurveda.attendance.dto.request.UpdateAttendanceStatusRequest;
import com.ayurveda.attendance.dto.response.AttendanceResponse;
import com.ayurveda.attendance.service.AttendanceService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.exception.BadRequestException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/attendances")
@Tag(name = "Attendance Management", description = "Attendance Management APIs")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "Mark Check-In")
    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(
            @Valid @RequestBody CreateAttendanceRequest request) {

        log.info("Received request to mark attendance check-in.");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attendanceService.checkIn(request));
    }

    @Operation(summary = "Mark Check-Out")
    @PutMapping("/check-out/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(
            @PathVariable @NotNull(message = "Attendance ID is required") UUID attendanceId,
            @Valid @RequestBody CheckOutRequest request) {

        log.info("Received request to mark attendance check-out. Attendance ID: {}", attendanceId);

        validateAttendanceId(attendanceId);

        return ResponseEntity.ok(attendanceService.checkOut(attendanceId, request));
    }

    @Operation(summary = "Update Attendance Status")
    @PutMapping("/update-status/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateStatus(
            @PathVariable @NotNull(message = "Attendance ID is required") UUID attendanceId,
            @Valid @RequestBody UpdateAttendanceStatusRequest request) {

        log.info("Received request to update attendance status. Attendance ID: {}", attendanceId);

        validateAttendanceId(attendanceId);

        return ResponseEntity.ok(attendanceService.updateStatus(attendanceId, request));
    }

    @Operation(summary = "Get Attendance By ID")
    @GetMapping("/get-attendance/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendanceById(
            @PathVariable @NotNull(message = "Attendance ID is required") UUID attendanceId) {

        log.info("Received request to fetch attendance: {}", attendanceId);

        validateAttendanceId(attendanceId);

        return ResponseEntity.ok(attendanceService.getAttendanceById(attendanceId));
    }

    @Operation(summary = "Get All Attendances")
    @GetMapping("/get-all-attendances")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAllAttendances() {

        log.info("Received request to fetch all attendance records.");

        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @Operation(summary = "Get Attendances By Employee ID")
    @GetMapping("/get-by-emp/{empId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendancesByEmpId(
            @PathVariable @NotBlank(message = "Employee ID must not be blank") String empId) {

        log.info("Received request to fetch attendance records for empId: {}", empId);

        if (!StringUtils.hasText(empId)) {
            throw new BadRequestException(AppConstants.EMP_ID_REQUIRED);
        }

        return ResponseEntity.ok(attendanceService.getAttendancesByEmpId(empId));
    }

    @Operation(
            summary = "Delete Attendance",
            description = "Soft delete attendance record by attendance ID."
    )
    @DeleteMapping("/delete-attendance/{attendanceId}")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(
            @PathVariable @NotNull(message = "Attendance ID is required") UUID attendanceId) {

        log.info("Received request to delete attendance. Attendance ID: {}", attendanceId);

        validateAttendanceId(attendanceId);

        return ResponseEntity.ok(
                attendanceService.deleteAttendance(attendanceId)
        );
    }

    private void validateAttendanceId(UUID attendanceId) {
        if (attendanceId == null) {
            throw new BadRequestException(AppConstants.ATTENDANCE_ID_REQUIRED);
        }
    }

}
