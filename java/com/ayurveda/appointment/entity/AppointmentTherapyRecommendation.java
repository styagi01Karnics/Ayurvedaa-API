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
@Table(name = "appointment_therapy_recommendations")
public class AppointmentTherapyRecommendation extends BaseEntity {

	@Column(nullable = false)
    private UUID appointmentTherapyId;

    @Column(nullable = false)
    private UUID therapyMasterId;

}