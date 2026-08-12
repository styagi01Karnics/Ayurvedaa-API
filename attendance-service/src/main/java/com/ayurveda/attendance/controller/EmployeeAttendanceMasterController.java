package com.ayurveda.attendance.controller;

import com.ayurveda.attendance.dto.request.CreateEmployeeAttendanceMasterRequest;
import com.ayurveda.attendance.dto.response.EmployeeAttendanceMasterResponse;
import com.ayurveda.attendance.service.EmployeeAttendanceMasterService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Employee Attendance Master", description = "Employee master APIs for attendance")
@RestController
@RequestMapping("/api/v1/employee-attendance-master")
@RequiredArgsConstructor
@Validated
public class EmployeeAttendanceMasterController {

    private final EmployeeAttendanceMasterService employeeAttendanceMasterService;

    @Operation(summary = "Add employee", description = "Creates a new employee record in employee_attendance_master")
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeAttendanceMasterResponse>> createEmployee(
            @Valid @RequestBody CreateEmployeeAttendanceMasterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeAttendanceMasterService.createEmployee(request));
    }

    @Operation(summary = "Get all employees", description = "Returns all active employee records from employee_attendance_master")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeAttendanceMasterResponse>>> getAllEmployees() {
        return ResponseEntity.ok(employeeAttendanceMasterService.getAllEmployees());
    }

    @Operation(summary = "Get employee by empId", description = "Returns an employee record by business employee ID")
    @GetMapping("/employee/{empId}")
    public ResponseEntity<ApiResponse<EmployeeAttendanceMasterResponse>> getEmployeeByEmpId(
            @PathVariable String empId) {
        return ResponseEntity.ok(employeeAttendanceMasterService.getEmployeeByEmpId(empId));
    }

    @Operation(summary = "Get employee by ID", description = "Returns an employee record by UUID")
    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeAttendanceMasterResponse>> getEmployeeById(
            @PathVariable UUID employeeId) {
        return ResponseEntity.ok(employeeAttendanceMasterService.getEmployeeById(employeeId));
    }

}
