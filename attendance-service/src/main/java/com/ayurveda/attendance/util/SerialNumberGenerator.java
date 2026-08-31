package com.ayurveda.attendance.util;

import org.springframework.stereotype.Component;

import com.ayurveda.attendance.entity.Attendance;
import com.ayurveda.attendance.repository.AttendanceRepository;
import com.ayurveda.common.util.BusinessCodeGenerator;
import com.ayurveda.common.util.BusinessCodeTypes;

import lombok.RequiredArgsConstructor;

/**
 * Attendance serial numbers use the unified business-code format
 * {@code {tenantCode}-ATT-{#####}}. ADMS device SN is unrelated (device allowlist / punch logs).
 */
@Component
@RequiredArgsConstructor
public class SerialNumberGenerator {

    private final AttendanceRepository attendanceRepository;

    public synchronized String generate() {
        String prefix = BusinessCodeGenerator.prefix(BusinessCodeTypes.ATTENDANCE);
        return BusinessCodeGenerator.next(
                BusinessCodeTypes.ATTENDANCE,
                attendanceRepository.findBySerialNumberStartingWith(prefix).stream()
                        .map(Attendance::getSerialNumber)
                        .toList());
    }

}
