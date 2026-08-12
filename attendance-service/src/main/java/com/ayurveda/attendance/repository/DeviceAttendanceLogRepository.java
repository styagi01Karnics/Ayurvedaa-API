package com.ayurveda.attendance.repository;

import com.ayurveda.attendance.entity.DeviceAttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeviceAttendanceLogRepository extends JpaRepository<DeviceAttendanceLog, UUID> {

    List<DeviceAttendanceLog> findAllByEmployeeIdOrderByPunchDateTimeDesc(String employeeId);

    List<DeviceAttendanceLog> findAllByDeviceSerialNumberOrderByPunchDateTimeDesc(String deviceSerialNumber);

    @Query("""
            SELECT d FROM DeviceAttendanceLog d
            WHERE d.punchDate = :punchDate
              AND (d.deleted = false OR d.deleted IS NULL)
            ORDER BY d.punchDateTime ASC
            """)
    List<DeviceAttendanceLog> findAllByPunchDateAndDeletedFalseOrderByPunchDateTimeAsc(
            @Param("punchDate") LocalDate punchDate);

    boolean existsByEmployeeIdAndPunchDateTimeAndDeviceSerialNumber(
            String employeeId, LocalDateTime punchDateTime, String deviceSerialNumber);

}
