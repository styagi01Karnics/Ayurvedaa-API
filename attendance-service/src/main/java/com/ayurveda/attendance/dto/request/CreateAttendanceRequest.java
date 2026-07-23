package com.ayurveda.attendance.dto.request;

import com.ayurveda.attendance.enums.AttendanceStatus;
import com.ayurveda.attendance.enums.StaffType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateAttendanceRequest {

    @NotBlank(message = "Employee ID is required")
    @Size(max = 50, message = "Employee ID must not exceed 50 characters")
    private String empId;

    @NotBlank(message = "Employee name is required")
    @Size(max = 150, message = "Employee name must not exceed 150 characters")
    private String empName;

    @NotNull(message = "Staff type is required")
    private StaffType staffType;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private AttendanceStatus status;

}
