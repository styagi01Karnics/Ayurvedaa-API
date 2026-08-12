package com.ayurveda.appointment.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.appointment.enums.FollowUpStatus;
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
@Table(name = "follow_ups")
public class FollowUp extends BaseEntity {

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID assignedDoctorId;

    /** Consult booking that triggered this follow-up (optional). */
    private UUID sourceBookingId;

    @Column(name = "visit_type_id", nullable = false)
    private UUID visitTypeId;

    @Column(nullable = false)
    private LocalDateTime appointmentDate;

    /** Guidance for receptionist, e.g. 7_DAYS, 15_DAYS. */
    @Column(length = 50)
    private String schedulingOption;

    @Builder.Default
    @Column(nullable = false)
    private Boolean smsReminderEnabled = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 30)
    private FollowUpStatus status = FollowUpStatus.UPCOMING;

}
