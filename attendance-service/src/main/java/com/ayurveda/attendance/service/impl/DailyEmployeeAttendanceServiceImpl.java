package com.ayurveda.attendance.service.impl;

import com.ayurveda.attendance.dto.response.DailyEmployeeAttendanceResponse;
import com.ayurveda.attendance.entity.DeviceAttendanceLog;
import com.ayurveda.attendance.entity.EmployeeAttendanceMaster;
import com.ayurveda.attendance.mapper.DailyEmployeeAttendanceMapper;
import com.ayurveda.attendance.repository.DeviceAttendanceLogRepository;
import com.ayurveda.attendance.repository.EmployeeAttendanceMasterRepository;
import com.ayurveda.attendance.service.DailyEmployeeAttendanceService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.util.ResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyEmployeeAttendanceServiceImpl implements DailyEmployeeAttendanceService {

    private final EmployeeAttendanceMasterRepository employeeAttendanceMasterRepository;
    private final DeviceAttendanceLogRepository deviceAttendanceLogRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<DailyEmployeeAttendanceResponse>> getDailyAttendance(LocalDate attendanceDate) {
        LocalDate date = attendanceDate != null ? attendanceDate : LocalDate.now();

        log.info("Fetching daily employee attendance for date: {}", date);

        List<EmployeeAttendanceMaster> employees = employeeAttendanceMasterRepository.findAllByDeletedFalse();
        Map<String, List<DeviceAttendanceLog>> punchesByEmpId = groupPunchesByEmployeeId(
                deviceAttendanceLogRepository.findAllByPunchDateAndDeletedFalseOrderByPunchDateTimeAsc(date));

        List<DailyEmployeeAttendanceResponse> records = employees.stream()
                .map(employee -> DailyEmployeeAttendanceMapper.toResponse(
                        employee,
                        date,
                        punchesByEmpId.getOrDefault(employee.getEmpId(), List.of())))
                .toList();

        log.info("Fetched {} daily employee attendance records for date: {}", records.size(), date);

        return ResponseUtil.success(AppConstants.DAILY_ATTENDANCE_FETCHED_SUCCESSFULLY, records);
    }

    private Map<String, List<DeviceAttendanceLog>> groupPunchesByEmployeeId(List<DeviceAttendanceLog> punches) {
        Map<String, List<DeviceAttendanceLog>> punchesByEmpId = new LinkedHashMap<>();
        for (DeviceAttendanceLog punch : punches) {
            if (punch.getEmployeeId() == null) {
                continue;
            }
            punchesByEmpId
                    .computeIfAbsent(punch.getEmployeeId().trim(), key -> new ArrayList<>())
                    .add(punch);
        }
        return punchesByEmpId;
    }

}
