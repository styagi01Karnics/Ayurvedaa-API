package com.ayurveda.attendance.dto.response;

import com.ayurveda.attendance.enums.AttendanceStatus;
import com.ayurveda.attendance.enums.StaffType;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AttendanceResponse {

    private UUID id;
    private String serialNumber;
    private String empId;
    private String empName;
    private StaffType staffType;
    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private AttendanceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
