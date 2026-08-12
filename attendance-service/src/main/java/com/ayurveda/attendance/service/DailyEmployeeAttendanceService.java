package com.ayurveda.attendance.service;

import com.ayurveda.attendance.dto.response.DailyEmployeeAttendanceResponse;
import com.ayurveda.common.ApiResponse;

import java.time.LocalDate;
import java.util.List;

public interface DailyEmployeeAttendanceService {

    ApiResponse<List<DailyEmployeeAttendanceResponse>> getDailyAttendance(LocalDate attendanceDate);

}
