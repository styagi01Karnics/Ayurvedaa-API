package com.ayurveda.attendance.service;

import com.ayurveda.attendance.dto.request.CreateEmployeeAttendanceMasterRequest;
import com.ayurveda.attendance.dto.response.EmployeeAttendanceMasterResponse;
import com.ayurveda.common.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface EmployeeAttendanceMasterService {

    ApiResponse<EmployeeAttendanceMasterResponse> createEmployee(CreateEmployeeAttendanceMasterRequest request);

    ApiResponse<EmployeeAttendanceMasterResponse> getEmployeeById(UUID employeeId);

    ApiResponse<EmployeeAttendanceMasterResponse> getEmployeeByEmpId(String empId);

    ApiResponse<List<EmployeeAttendanceMasterResponse>> getAllEmployees();

}
