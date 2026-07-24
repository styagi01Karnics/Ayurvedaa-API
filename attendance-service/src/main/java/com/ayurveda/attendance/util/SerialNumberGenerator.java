package com.ayurveda.attendance.util;

import com.ayurveda.attendance.repository.AttendanceRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class SerialNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AttendanceRepository attendanceRepository;

    public synchronized String generate() {
        String prefix = "ATT-" + LocalDate.now().format(DATE_FORMAT) + "-";
        long sequence = attendanceRepository.countBySerialNumberStartingWith(prefix) + 1;

        String candidate = buildSerialNumber(prefix, sequence);
        while (attendanceRepository.existsBySerialNumber(candidate)) {
            sequence++;
            candidate = buildSerialNumber(prefix, sequence);
        }

        return candidate;
    }

    private String buildSerialNumber(String prefix, long sequence) {
        return prefix + String.format("%04d", sequence);
    }

}
