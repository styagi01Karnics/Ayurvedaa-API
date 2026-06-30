package com.ayurveda.appointment.entity;

import java.util.UUID;

import com.ayurveda.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_systemic_examinations")
public class AppointmentSystemicExamination extends BaseEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(length = 1000)
    private String cardiovascular;

    @Column(length = 1000)
    private String respiratory;

    @Column(length = 1000)
    private String nervous;

    @Column(length = 1000)
    private String abdomenGi;

    @Column(length = 1000)
    private String locomotor;

}