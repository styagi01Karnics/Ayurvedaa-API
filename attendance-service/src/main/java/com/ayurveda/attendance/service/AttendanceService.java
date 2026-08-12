package com.ayurveda.attendance.service;

import com.ayurveda.attendance.dto.request.CheckOutRequest;
import com.ayurveda.attendance.dto.request.CreateAttendanceRequest;
import com.ayurveda.attendance.dto.request.UpdateAttendanceStatusRequest;
import com.ayurveda.attendance.dto.response.AttendanceResponse;
import com.ayurveda.common.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    /** Records an employee check-in. */
    ApiResponse<AttendanceResponse> checkIn(CreateAttendanceRequest request);

    /** Records an employee check-out for the given attendance. */
    ApiResponse<AttendanceResponse> checkOut(UUID attendanceId, CheckOutRequest request);

    /** Updates the status of an attendance record. */
    ApiResponse<AttendanceResponse> updateStatus(UUID attendanceId, UpdateAttendanceStatusRequest request);

    /** Returns an attendance record by ID. */
    ApiResponse<AttendanceResponse> getAttendanceById(UUID attendanceId);

    /** Lists all attendance records for the current tenant. */
    ApiResponse<List<AttendanceResponse>> getAllAttendances();

    /** Lists attendance records for the given employee ID. */
    ApiResponse<List<AttendanceResponse>> getAttendancesByEmpId(String empId);

    /** Soft-deletes an attendance record. */
    ApiResponse<Void> deleteAttendance(UUID attendanceId);

}
