package com.ayurveda.attendance.dto.response;

import com.ayurveda.attendance.enums.AttendanceStatus;
import com.ayurveda.attendance.enums.EmployeeStatus;
import com.ayurveda.attendance.enums.StaffType;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class DailyEmployeeAttendanceResponse {

    private UUID employeeId;
    private String empId;
    private String empName;
    private StaffType staffType;
    private String department;
    private String shift;
    private String stp;
    private String designation;
    private String mobileNumber;
    private String email;
    private EmployeeStatus employeeStatus;

    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer punchCount;
    private List<DailyPunchResponse> punches;
    private AttendanceStatus attendanceStatus;

}
