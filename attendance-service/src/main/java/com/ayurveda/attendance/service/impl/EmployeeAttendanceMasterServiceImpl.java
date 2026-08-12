package com.ayurveda.attendance.service.impl;

import com.ayurveda.attendance.dto.request.CreateEmployeeAttendanceMasterRequest;
import com.ayurveda.attendance.dto.response.EmployeeAttendanceMasterResponse;
import com.ayurveda.attendance.entity.EmployeeAttendanceMaster;
import com.ayurveda.attendance.enums.EmployeeStatus;
import com.ayurveda.attendance.mapper.EmployeeAttendanceMasterMapper;
import com.ayurveda.attendance.repository.EmployeeAttendanceMasterRepository;
import com.ayurveda.attendance.service.EmployeeAttendanceMasterService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.util.ResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeAttendanceMasterServiceImpl implements EmployeeAttendanceMasterService {

    private final EmployeeAttendanceMasterRepository employeeAttendanceMasterRepository;

    @Override
    @Transactional
    public ApiResponse<EmployeeAttendanceMasterResponse> createEmployee(
            CreateEmployeeAttendanceMasterRequest request) {

        if (request == null) {
            throw new BadRequestException(AppConstants.BAD_REQUEST);
        }

        normalizeRequest(request);

        log.info("Creating employee attendance master record for empId: {}", request.getEmpId());

        if (employeeAttendanceMasterRepository.existsByEmpIdAndDeletedFalse(request.getEmpId())) {
            throw new DuplicateResourceException(AppConstants.EMPLOYEE_ALREADY_EXISTS);
        }

        EmployeeAttendanceMaster employee = EmployeeAttendanceMaster.builder()
                .empId(request.getEmpId())
                .empName(request.getEmpName())
                .staffType(request.getStaffType())
                .department(trimToNull(request.getDepartment()))
                .shift(trimToNull(request.getShift()))
                .stp(trimToNull(request.getStp()))
                .designation(trimToNull(request.getDesignation()))
                .mobileNumber(trimToNull(request.getMobileNumber()))
                .email(trimToNull(request.getEmail()))
                .status(request.getStatus() != null ? request.getStatus() : EmployeeStatus.ACTIVE)
                .build();

        EmployeeAttendanceMaster savedEmployee = employeeAttendanceMasterRepository.save(employee);

        log.info("Employee attendance master record created. ID: {}, empId: {}",
                savedEmployee.getId(), savedEmployee.getEmpId());

        return ResponseUtil.success(
                AppConstants.EMPLOYEE_CREATED_SUCCESSFULLY,
                EmployeeAttendanceMasterMapper.toResponse(savedEmployee)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<EmployeeAttendanceMasterResponse> getEmployeeById(UUID employeeId) {
        if (employeeId == null) {
            throw new BadRequestException(AppConstants.EMPLOYEE_ID_REQUIRED);
        }

        log.info("Fetching employee attendance master record for id: {}", employeeId);

        EmployeeAttendanceMaster employee = employeeAttendanceMasterRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.EMPLOYEE_NOT_FOUND));

        return ResponseUtil.success(
                AppConstants.EMPLOYEE_FETCHED_SUCCESSFULLY,
                EmployeeAttendanceMasterMapper.toResponse(employee)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<EmployeeAttendanceMasterResponse> getEmployeeByEmpId(String empId) {
        if (!StringUtils.hasText(empId)) {
            throw new BadRequestException(AppConstants.EMP_ID_REQUIRED);
        }

        log.info("Fetching employee attendance master record for empId: {}", empId);

        EmployeeAttendanceMaster employee = employeeAttendanceMasterRepository
                .findByEmpIdAndDeletedFalse(empId.trim())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.EMPLOYEE_NOT_FOUND));

        return ResponseUtil.success(
                AppConstants.EMPLOYEE_FETCHED_SUCCESSFULLY,
                EmployeeAttendanceMasterMapper.toResponse(employee)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<EmployeeAttendanceMasterResponse>> getAllEmployees() {
        log.info("Fetching all employee attendance master records.");

        List<EmployeeAttendanceMasterResponse> employees = employeeAttendanceMasterRepository.findAllByDeletedFalse()
                .stream()
                .map(EmployeeAttendanceMasterMapper::toResponse)
                .toList();

        log.info("Successfully fetched {} employee attendance master records.", employees.size());

        return ResponseUtil.success(
                AppConstants.EMPLOYEES_FETCHED_SUCCESSFULLY,
                employees
        );
    }

    private void normalizeRequest(CreateEmployeeAttendanceMasterRequest request) {
        if (request.getEmpId() != null) {
            request.setEmpId(request.getEmpId().trim());
        }
        if (request.getEmpName() != null) {
            request.setEmpName(request.getEmpName().trim());
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

}
