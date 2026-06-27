package com.ayurveda.appointment.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.ayurveda.appointment.common.BaseEntity;
import com.ayurveda.appointment.enums.TherapyStatus;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_therapies")
public class AppointmentTherapy extends BaseEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private UUID treatmentCategoryId;

    @Column(nullable = false)
    private UUID assignedTherapistId;

    @Column(nullable = false)
    private LocalDate scheduleDate;

    @Column(nullable = false)
    private LocalTime scheduleTime;

    /**
     * Duration of one session (Minutes)
     * Example: 30, 45, 60
     */
    @Column(nullable = false)
    private Integer sessionDuration;

    /**
     * Number of sessions
     * Example: 7 Sessions
     */
    @Column(nullable = false)
    private Integer sessionFrequency;

    @Column(columnDefinition = "TEXT")
    private String therapyInstructions;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private TherapyStatus therapyStatus = TherapyStatus.SCHEDULED;

}