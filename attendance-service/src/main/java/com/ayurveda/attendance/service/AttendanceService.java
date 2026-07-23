package com.ayurveda.attendance.service;

import com.ayurveda.attendance.dto.request.CheckOutRequest;
import com.ayurveda.attendance.dto.request.CreateAttendanceRequest;
import com.ayurveda.attendance.dto.request.UpdateAttendanceStatusRequest;
import com.ayurveda.attendance.dto.response.AttendanceResponse;
import com.ayurveda.common.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    ApiResponse<AttendanceResponse> checkIn(CreateAttendanceRequest request);

    ApiResponse<AttendanceResponse> checkOut(UUID attendanceId, CheckOutRequest request);

    ApiResponse<AttendanceResponse> updateStatus(UUID attendanceId, UpdateAttendanceStatusRequest request);

    ApiResponse<AttendanceResponse> getAttendanceById(UUID attendanceId);

    ApiResponse<List<AttendanceResponse>> getAllAttendances();

    ApiResponse<List<AttendanceResponse>> getAttendancesByEmpId(String empId);

    ApiResponse<Void> deleteAttendance(UUID attendanceId);

}
