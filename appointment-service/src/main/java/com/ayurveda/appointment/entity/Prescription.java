package com.ayurveda.appointment.entity;

import java.util.UUID;

import com.ayurveda.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "prescriptions")
public class Prescription extends BaseEntity {

    @Column(nullable = false)
    private UUID patientId;

    /** Optional booking this prescription was generated from. */
    private UUID appointmentBookingId;

    private UUID assignedDoctorId;

    /** Next follow-up: whether setup is required. */
    @Builder.Default
    @Column(nullable = false)
    private Boolean followUpRequired = false;

    @Column(length = 100)
    private String followUpSchedulingOption;

    @Column(columnDefinition = "TEXT")
    private String followUpSuggestions;

    /** Diagnosis text shown on prescription print (e.g. Mental health issue, Migraine). */
    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    /** Free-text notes on prescription ("Add texts if any"). */
    @Column(columnDefinition = "TEXT")
    private String notes;

}
