package com.ayurveda.attendance.mapper;

import com.ayurveda.attendance.dto.response.AttendanceResponse;
import com.ayurveda.attendance.entity.Attendance;

public final class AttendanceMapper {

    private AttendanceMapper() {
    }

    public static AttendanceResponse toResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .serialNumber(attendance.getSerialNumber())
                .empId(attendance.getEmpId())
                .empName(attendance.getEmpName())
                .staffType(attendance.getStaffType())
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .status(attendance.getStatus())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }

}
