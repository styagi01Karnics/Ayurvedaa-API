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
@Table(name = "appointment_ayurvedic_assessments")
public class AppointmentAyurvedicAssessment extends BaseEntity {

    @Column(nullable = false)
    private UUID patientId;

    @Column(name = "dosha_id", nullable = false)
    private UUID doshaId;

    @Column(columnDefinition = "TEXT")
    private String bodyConstitution;

    @Column(columnDefinition = "TEXT")
    private String currentImbalances;

}