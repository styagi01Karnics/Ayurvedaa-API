package com.ayurveda.attendance.service.impl;

import com.ayurveda.attendance.entity.DeviceAttendanceLog;
import com.ayurveda.attendance.repository.DeviceAttendanceLogRepository;
import com.ayurveda.attendance.service.DeviceAttendanceLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceAttendanceLogServiceImpl implements DeviceAttendanceLogService {

    private static final DateTimeFormatter[] DATE_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    private static final DateTimeFormatter[] TIME_FORMATS = {
            DateTimeFormatter.ofPattern("HH:mm:ss"),
            DateTimeFormatter.ofPattern("HH:mm")
    };

    private final DeviceAttendanceLogRepository deviceAttendanceLogRepository;

    @Override
    @Transactional
    public void savePunchLog(String serialNumber, String table, String employeeId, String punchDate,
            String punchTime, String rawLine) {

        if (!StringUtils.hasText(employeeId)) {
            log.warn("Skipping device punch log with blank employeeId. Raw line: {}", rawLine);
            return;
        }

        LocalDate parsedDate = parseDate(punchDate);
        LocalTime parsedTime = parseTime(punchTime);
        LocalDateTime punchDateTime = (parsedDate != null && parsedTime != null)
                ? LocalDateTime.of(parsedDate, parsedTime)
                : null;

        if (punchDateTime != null
                && deviceAttendanceLogRepository.existsByEmployeeIdAndPunchDateTimeAndDeviceSerialNumber(
                        employeeId, punchDateTime, serialNumber)) {
            log.info("Duplicate punch log ignored for empId: {} at {}", employeeId, punchDateTime);
            return;
        }

        DeviceAttendanceLog deviceAttendanceLog = DeviceAttendanceLog.builder()
                .deviceSerialNumber(serialNumber)
                .tableName(table)
                .employeeId(employeeId)
                .punchDate(parsedDate)
                .punchTime(parsedTime)
                .punchDateTime(punchDateTime)
                .rawPunchDate(punchDate)
                .rawPunchTime(punchTime)
                .rawLine(rawLine)
                .build();

        deviceAttendanceLogRepository.save(deviceAttendanceLog);

        log.info("Device punch log saved for empId: {}, deviceSerialNumber: {}", employeeId, serialNumber);
    }

    private LocalDate parseDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }
        log.warn("Unable to parse punch date: {}", value);
        return null;
    }

    private LocalTime parseTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        for (DateTimeFormatter formatter : TIME_FORMATS) {
            try {
                return LocalTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }
        log.warn("Unable to parse punch time: {}", value);
        return null;
    }

}
