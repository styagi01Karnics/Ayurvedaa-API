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
@Table(name = "appointment_ayurvedic_assessments")
public class AppointmentAyurvedicAssessment extends BaseEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(length = 100)
    private String doshaType;

    @Column(columnDefinition = "TEXT")
    private String bodyConstitution;

    @Column(columnDefinition = "TEXT")
    private String currentImbalances;

}