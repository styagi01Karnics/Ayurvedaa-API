package com.ayurveda.attendance.service.impl;

import com.ayurveda.attendance.dto.request.CheckOutRequest;
import com.ayurveda.attendance.dto.request.CreateAttendanceRequest;
import com.ayurveda.attendance.dto.request.UpdateAttendanceStatusRequest;
import com.ayurveda.attendance.dto.response.AttendanceResponse;
import com.ayurveda.attendance.entity.Attendance;
import com.ayurveda.attendance.enums.AttendanceStatus;
import com.ayurveda.attendance.mapper.AttendanceMapper;
import com.ayurveda.attendance.repository.AttendanceRepository;
import com.ayurveda.attendance.service.AttendanceService;
import com.ayurveda.attendance.util.SerialNumberGenerator;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SerialNumberGenerator serialNumberGenerator;

    @Override
    @Transactional
    public ApiResponse<AttendanceResponse> checkIn(CreateAttendanceRequest request) {

        if (request == null) {
            throw new BadRequestException(AppConstants.BAD_REQUEST);
        }

        log.info("Marking attendance for empId: {}", request.getEmpId());

        normalizeRequest(request);

        validateAttendanceRequest(request);

        if (attendanceRepository.existsByEmpIdAndAttendanceDateAndDeletedFalse(
                request.getEmpId(), request.getAttendanceDate())) {

            throw new DuplicateResourceException(AppConstants.ATTENDANCE_ALREADY_MARKED);
        }

        Attendance attendance = Attendance.builder()
                .serialNumber(serialNumberGenerator.generate())
                .empId(request.getEmpId())
                .empName(request.getEmpName())
                .staffType(request.getStaffType())
                .attendanceDate(request.getAttendanceDate())
                .checkInTime(request.getCheckInTime() != null ? request.getCheckInTime() : LocalDateTime.now())
                .status(request.getStatus() != null ? request.getStatus() : AttendanceStatus.PRESENT)
                .build();

        Attendance savedAttendance = attendanceRepository.save(attendance);

        log.info("Attendance marked successfully. Attendance ID: {}, Serial Number: {}",
                savedAttendance.getId(),
                savedAttendance.getSerialNumber());

        return ResponseUtil.success(
                AppConstants.ATTENDANCE_MARKED_SUCCESSFULLY,
                AttendanceMapper.toResponse(savedAttendance)
        );
    }

    private void normalizeRequest(CreateAttendanceRequest request) {
        request.setEmpId(request.getEmpId().trim());
        request.setEmpName(request.getEmpName().trim());
    }

    private void validateAttendanceRequest(CreateAttendanceRequest request) {

        if (!StringUtils.hasText(request.getEmpId())) {
            throw new BadRequestException(AppConstants.EMP_ID_REQUIRED);
        }

        if (request.getAttendanceDate().isAfter(LocalDate.now())) {
            throw new BadRequestException(AppConstants.ATTENDANCE_DATE_IN_FUTURE);
        }
    }

    private void validateAttendanceId(UUID attendanceId) {
        if (attendanceId == null) {
            throw new BadRequestException(AppConstants.ATTENDANCE_ID_REQUIRED);
        }
    }

    @Override
    @Transactional
    public ApiResponse<AttendanceResponse> checkOut(UUID attendanceId, CheckOutRequest request) {

        log.info("Recording check-out for attendanceId: {}", attendanceId);

        validateAttendanceId(attendanceId);

        if (request == null) {
            throw new BadRequestException(AppConstants.BAD_REQUEST);
        }

        Attendance attendance = attendanceRepository.findByIdAndDeletedFalse(attendanceId)
                .orElseThrow(() -> {
                    log.warn("Attendance not found with attendanceId: {}", attendanceId);
                    return new ResourceNotFoundException(AppConstants.ATTENDANCE_NOT_FOUND);
                });

        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException(AppConstants.ATTENDANCE_ALREADY_CHECKED_OUT);
        }

        LocalDateTime checkOutTime = request.getCheckOutTime() != null
                ? request.getCheckOutTime()
                : LocalDateTime.now();

        if (checkOutTime.isBefore(attendance.getCheckInTime())) {
            throw new BadRequestException(AppConstants.INVALID_CHECK_OUT_TIME);
        }

        attendance.setCheckOutTime(checkOutTime);

        Attendance updatedAttendance = attendanceRepository.save(attendance);

        log.info("Check-out recorded successfully. Attendance ID: {}", attendanceId);

        return ResponseUtil.success(
                AppConstants.ATTENDANCE_CHECKED_OUT_SUCCESSFULLY,
                AttendanceMapper.toResponse(updatedAttendance)
        );
    }

    @Override
    @Transactional
    public ApiResponse<AttendanceResponse> updateStatus(UUID attendanceId, UpdateAttendanceStatusRequest request) {

        log.info("Updating attendance status for attendanceId: {}", attendanceId);

        validateAttendanceId(attendanceId);

        if (request == null || request.getStatus() == null) {
            throw new BadRequestException(AppConstants.BAD_REQUEST);
        }

        Attendance attendance = attendanceRepository.findByIdAndDeletedFalse(attendanceId)
                .orElseThrow(() -> {
                    log.warn("Attendance not found with attendanceId: {}", attendanceId);
                    return new ResourceNotFoundException(AppConstants.ATTENDANCE_NOT_FOUND);
                });

        attendance.setStatus(request.getStatus());

        Attendance updatedAttendance = attendanceRepository.save(attendance);

        log.info("Attendance status updated successfully. Attendance ID: {}", attendanceId);

        return ResponseUtil.success(
                AppConstants.ATTENDANCE_STATUS_UPDATED_SUCCESSFULLY,
                AttendanceMapper.toResponse(updatedAttendance)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AttendanceResponse> getAttendanceById(UUID attendanceId) {

        log.info("Fetching attendance details for attendanceId: {}", attendanceId);

        validateAttendanceId(attendanceId);

        Attendance attendance = attendanceRepository.findByIdAndDeletedFalse(attendanceId)
                .orElseThrow(() -> {
                    log.warn("Attendance not found with attendanceId: {}", attendanceId);
                    return new ResourceNotFoundException(AppConstants.ATTENDANCE_NOT_FOUND);
                });

        log.info("Attendance fetched successfully. Attendance ID: {}", attendanceId);

        return ResponseUtil.success(
                AppConstants.ATTENDANCE_FETCHED_SUCCESSFULLY,
                AttendanceMapper.toResponse(attendance)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AttendanceResponse>> getAllAttendances() {

        log.info("Fetching all active attendance records.");

        List<AttendanceResponse> attendances = attendanceRepository.findAllByDeletedFalse()
                .stream()
                .map(AttendanceMapper::toResponse)
                .toList();

        log.info("Successfully fetched {} attendance records.", attendances.size());

        return ResponseUtil.success(
                AppConstants.ATTENDANCES_FETCHED_SUCCESSFULLY,
                attendances
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AttendanceResponse>> getAttendancesByEmpId(String empId) {

        if (!StringUtils.hasText(empId)) {
            throw new BadRequestException(AppConstants.EMP_ID_REQUIRED);
        }

        log.info("Fetching attendance records for empId: {}", empId);

        List<AttendanceResponse> attendances = attendanceRepository
                .findAllByEmpIdAndDeletedFalseOrderByAttendanceDateDesc(empId)
                .stream()
                .map(AttendanceMapper::toResponse)
                .toList();

        log.info("Successfully fetched {} attendance records for empId: {}", attendances.size(), empId);

        return ResponseUtil.success(
                AppConstants.ATTENDANCES_FETCHED_SUCCESSFULLY,
                attendances
        );
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteAttendance(UUID attendanceId) {

        log.info("Received request to delete attendance with ID: {}", attendanceId);

        validateAttendanceId(attendanceId);

        Attendance attendance = attendanceRepository.findByIdAndDeletedFalse(attendanceId)
                .orElseThrow(() -> {
                    log.warn("Attendance not found with ID: {}", attendanceId);
                    return new ResourceNotFoundException(AppConstants.ATTENDANCE_NOT_FOUND);
                });

        attendance.setDeleted(true);

        attendanceRepository.save(attendance);

        log.info("Attendance deleted successfully. Attendance ID: {}", attendanceId);

        return ResponseUtil.success(
                AppConstants.ATTENDANCE_DELETED_SUCCESSFULLY
        );
    }

}
