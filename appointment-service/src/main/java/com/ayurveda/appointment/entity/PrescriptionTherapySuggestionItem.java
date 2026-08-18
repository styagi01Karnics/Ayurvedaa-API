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
@Table(name = "prescription_therapy_suggestion_items")
public class PrescriptionTherapySuggestionItem extends BaseEntity {

    @Column(nullable = false)
    private UUID therapySuggestionId;

    @Column(nullable = false)
    private UUID therapyMasterId;

}
