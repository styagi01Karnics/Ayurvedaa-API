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
@Table(name = "appointment_treatment_plans")
public class AppointmentTreatmentPlan extends BaseEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(columnDefinition = "TEXT")
    private String investigationAndPlanSuggested;

    @Column(columnDefinition = "TEXT")
    private String planTaken;

}