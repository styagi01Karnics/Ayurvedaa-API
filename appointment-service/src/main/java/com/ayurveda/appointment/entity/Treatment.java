package com.ayurveda.appointment.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.ayurveda.appointment.enums.TreatmentStatus;
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
@Table(name = "treatments")
public class Treatment extends BaseEntity {

    @Column(nullable = false)
    private UUID patientId;

    @Column(name = "treatment_plan_id", nullable = false)
    private UUID treatmentPlanId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer totalSessions;

    @Column(nullable = false)
    private Integer completedSessions;

    @Column(nullable = false)
    private Integer remainingSessions;

    @Column(nullable = false)
    private UUID assignedTherapistId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 30)
    private TreatmentStatus treatmentStatus = TreatmentStatus.SCHEDULED;

}
