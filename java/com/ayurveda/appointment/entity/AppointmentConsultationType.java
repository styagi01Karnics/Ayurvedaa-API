package com.ayurveda.appointment.entity;

import com.ayurveda.appointment.common.BaseEntity;
import com.ayurveda.appointment.enums.ConsultationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="appointment_consultation_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentConsultationType extends BaseEntity{

    @Column(nullable=false)
    private UUID bookingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ConsultationType consultationType;

}