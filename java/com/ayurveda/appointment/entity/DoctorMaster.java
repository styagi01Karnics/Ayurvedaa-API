package com.ayurveda.appointment.entity;

import com.ayurveda.appointment.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mst_doctor")
public class DoctorMaster extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String doctorName;

    @Column(nullable = false, unique = true, length = 100)
    private String doctorCode;

    @Column(length = 150)
    private String specialization;

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

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

}