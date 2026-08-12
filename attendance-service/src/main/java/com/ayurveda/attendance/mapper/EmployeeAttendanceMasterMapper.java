package com.ayurveda.attendance.mapper;

import com.ayurveda.attendance.dto.response.EmployeeAttendanceMasterResponse;
import com.ayurveda.attendance.entity.EmployeeAttendanceMaster;

public final class EmployeeAttendanceMasterMapper {

    private EmployeeAttendanceMasterMapper() {
    }

    public static EmployeeAttendanceMasterResponse toResponse(EmployeeAttendanceMaster employee) {
        return EmployeeAttendanceMasterResponse.builder()
                .id(employee.getId())
                .empId(employee.getEmpId())
                .empName(employee.getEmpName())
                .staffType(employee.getStaffType())
                .department(employee.getDepartment())
                .shift(employee.getShift())
                .stp(employee.getStp())
                .designation(employee.getDesignation())
                .mobileNumber(employee.getMobileNumber())
                .email(employee.getEmail())
                .status(employee.getStatus())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

}
