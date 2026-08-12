package com.ayurveda.attendance.entity;

import com.ayurveda.attendance.enums.EmployeeStatus;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_attendance_master")
public class EmployeeAttendanceMaster extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String empId;

    @Column(nullable = false, length = 150)
    private String empName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StaffType staffType;

    @Column(length = 100)
    private String department;

    @Column(length = 50)
    private String shift;

    @Column(name = "stp", length = 100)
    private String stp;

    @Column(length = 100)
    private String designation;

    @Column(length = 15)
    private String mobileNumber;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeStatus status;

}
