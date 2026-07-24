package com.ayurveda.attendance.entity;

import com.ayurveda.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Stores raw punch logs pushed by biometric (eSSL/ZKTeco ADMS) devices
 * to the {@code /cdata.aspx} endpoint.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_attendance_logs")
public class DeviceAttendanceLog extends BaseEntity {

    @Column(name = "device_serial_number", length = 50)
    private String deviceSerialNumber;

    @Column(name = "table_name", length = 50)
    private String tableName;

    @Column(name = "employee_id", nullable = false, length = 50)
    private String employeeId;

    @Column(name = "punch_date")
    private LocalDate punchDate;

    @Column(name = "punch_time")
    private LocalTime punchTime;

    @Column(name = "punch_date_time")
    private LocalDateTime punchDateTime;

    @Column(name = "raw_punch_date", length = 50)
    private String rawPunchDate;

    @Column(name = "raw_punch_time", length = 50)
    private String rawPunchTime;

    @Column(name = "raw_line", length = 500)
    private String rawLine;

}
