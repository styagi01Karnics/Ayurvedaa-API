package com.ayurveda.doctor.entity;

import java.math.BigDecimal;

import com.ayurveda.common.BaseEntity;
import com.ayurveda.doctor.enums.DoctorStatus;
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
@Table(name = "mst_doctor")
public class Doctor extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String doctorName;

    @Column(nullable = false, unique = true, length = 100)
    private String doctorCode;

    @Column(length = 150)
    private String specialization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DoctorStatus status;

    @Column(precision = 12, scale = 2)
    private BigDecimal consultationFees;

    @Column(precision = 12, scale = 2)
    private BigDecimal followUpFees;

    @Column(length = 255)
    private String availability;

    @Column(length = 15)
    private String mobileNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String qualification;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String consultationRoom;

}
