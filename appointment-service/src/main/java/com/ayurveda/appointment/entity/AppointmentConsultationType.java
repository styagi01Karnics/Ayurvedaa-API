package com.ayurveda.appointment.entity;

import com.ayurveda.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "appointment_consultation_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentConsultationType extends BaseEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(name = "consultation_type_id", nullable = false)
    private UUID consultationTypeId;

}
