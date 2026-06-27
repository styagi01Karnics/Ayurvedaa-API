package com.ayurveda.appointment.entity;

import java.util.UUID;

import com.ayurveda.appointment.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_physical_examinations")
public class AppointmentPhysicalExamination extends BaseEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double ibw;

    @Column(nullable = false)
    private Integer pulse;

    @Column(nullable = false, length = 20)
    private String bp;

    @Column(nullable = false)
    private Double temperature;

    @Column(length = 100)
    private String pallor;

    @Column(length = 100)
    private String icterus;

    @Column(length = 100)
    private String cyanosis;

    @Column(length = 100)
    private String lymphNodes;

    @Column(length = 100)
    private String oedema;

    @Column(length = 100)
    private String sensorium;

    @Column(length = 100)
    private String acidityGas;

    @Column(length = 100)
    private String motion;

    @Column(length = 100)
    private String micturition;

}