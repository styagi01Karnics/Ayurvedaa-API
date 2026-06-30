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
@Table(name = "appointment_lifestyle_information")
public class AppointmentLifestyleInformation extends BaseEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(length = 100)
    private String dietType;

    @Column(length = 100)
    private String sleepPattern;

    @Column(length = 100)
    private String exerciseHabits;

    @Column(length = 100)
    private String addiction;

}