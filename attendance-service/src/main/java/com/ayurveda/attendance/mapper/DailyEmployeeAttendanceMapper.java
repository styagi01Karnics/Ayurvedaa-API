package com.ayurveda.attendance.mapper;

import com.ayurveda.attendance.dto.response.DailyEmployeeAttendanceResponse;
import com.ayurveda.attendance.dto.response.DailyPunchResponse;
import com.ayurveda.attendance.entity.DeviceAttendanceLog;
import com.ayurveda.attendance.entity.EmployeeAttendanceMaster;
import com.ayurveda.attendance.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class DailyEmployeeAttendanceMapper {

    private DailyEmployeeAttendanceMapper() {
    }

    public static DailyEmployeeAttendanceResponse toResponse(
            EmployeeAttendanceMaster employee,
            LocalDate attendanceDate,
            List<DeviceAttendanceLog> punches) {

        List<DeviceAttendanceLog> orderedPunches = punches == null ? List.of() : punches;
        List<DailyPunchResponse> punchResponses = orderedPunches.stream()
                .map(DailyEmployeeAttendanceMapper::toPunchResponse)
                .toList();

        LocalDateTime checkInTime = orderedPunches.isEmpty() ? null : orderedPunches.get(0).getPunchDateTime();
        LocalDateTime checkOutTime = orderedPunches.size() > 1
                ? orderedPunches.get(orderedPunches.size() - 1).getPunchDateTime()
                : null;

        return DailyEmployeeAttendanceResponse.builder()
                .employeeId(employee.getId())
                .empId(employee.getEmpId())
                .empName(employee.getEmpName())
                .staffType(employee.getStaffType())
                .department(employee.getDepartment())
                .shift(employee.getShift())
                .stp(employee.getStp())
                .designation(employee.getDesignation())
                .mobileNumber(employee.getMobileNumber())
                .email(employee.getEmail())
                .employeeStatus(employee.getStatus())
                .attendanceDate(attendanceDate)
                .checkInTime(checkInTime)
                .checkOutTime(checkOutTime)
                .punchCount(punchResponses.size())
                .punches(punchResponses)
                .attendanceStatus(orderedPunches.isEmpty() ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT)
                .build();
    }

    private static DailyPunchResponse toPunchResponse(DeviceAttendanceLog punch) {
        return DailyPunchResponse.builder()
                .punchTime(punch.getPunchTime())
                .punchDateTime(punch.getPunchDateTime())
                .deviceSerialNumber(punch.getDeviceSerialNumber())
                .build();
    }

}
