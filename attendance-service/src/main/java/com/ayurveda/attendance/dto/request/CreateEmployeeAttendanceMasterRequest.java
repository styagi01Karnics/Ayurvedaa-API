package com.ayurveda.attendance.dto.request;

import com.ayurveda.attendance.enums.EmployeeStatus;
import com.ayurveda.attendance.enums.StaffType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmployeeAttendanceMasterRequest {

    @NotBlank(message = "Employee ID is required")
    @Size(max = 50, message = "Employee ID must not exceed 50 characters")
    private String empId;

    @NotBlank(message = "Employee name is required")
    @Size(max = 150, message = "Employee name must not exceed 150 characters")
    private String empName;

    @NotNull(message = "Staff type is required")
    private StaffType staffType;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    @Size(max = 50, message = "Shift must not exceed 50 characters")
    private String shift;

    @Size(max = 100, message = "STP must not exceed 100 characters")
    private String stp;

    @Size(max = 100, message = "Designation must not exceed 100 characters")
    private String designation;

    @Size(max = 15, message = "Mobile number must not exceed 15 characters")
    private String mobileNumber;

    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    private EmployeeStatus status;

}
