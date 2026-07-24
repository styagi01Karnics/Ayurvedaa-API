package com.ayurveda.attendance.repository;

import com.ayurveda.attendance.entity.DeviceAttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeviceAttendanceLogRepository extends JpaRepository<DeviceAttendanceLog, UUID> {

    List<DeviceAttendanceLog> findAllByEmployeeIdOrderByPunchDateTimeDesc(String employeeId);

    List<DeviceAttendanceLog> findAllByDeviceSerialNumberOrderByPunchDateTimeDesc(String deviceSerialNumber);

    boolean existsByEmployeeIdAndPunchDateTimeAndDeviceSerialNumber(
            String employeeId, LocalDateTime punchDateTime, String deviceSerialNumber);

}
