package com.ayurveda.attendance.dto.response;

import com.ayurveda.attendance.enums.EmployeeStatus;
import com.ayurveda.attendance.enums.StaffType;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class EmployeeAttendanceMasterResponse {

    private UUID id;
    private String empId;
    private String empName;
    private StaffType staffType;
    private String department;
    private String shift;
    private String stp;
    private String designation;
    private String mobileNumber;
    private String email;
    private EmployeeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
