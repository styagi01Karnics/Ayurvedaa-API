package com.ayurveda.attendance.entity;

import com.ayurveda.attendance.enums.AttendanceStatus;
import com.ayurveda.attendance.enums.StaffType;
import com.ayurveda.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attendances")
public class Attendance extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String serialNumber;

    @Column(nullable = false)
    private String empId;

    @Column(nullable = false, length = 150)
    private String empName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffType staffType;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

}
