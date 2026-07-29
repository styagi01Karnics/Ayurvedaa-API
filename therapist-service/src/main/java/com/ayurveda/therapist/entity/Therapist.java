package com.ayurveda.therapist.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ayurveda.common.BaseEntity;
import com.ayurveda.therapist.enums.TherapistStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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
@Table(name = "mst_therapist")
public class Therapist extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String therapistName;

    @Column(nullable = false, unique = true, length = 100)
    private String therapistCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TherapistStatus status;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "mst_therapist_assigned_therapies",
            joinColumns = @JoinColumn(name = "therapist_id")
    )
    @Column(name = "therapy_id", nullable = false)
    private List<UUID> assignedTherapyIds = new ArrayList<>();

    @Column(length = 150)
    private String specialization;

    @Column(length = 15)
    private String mobileNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String qualification;

    @Column(length = 100)
    private String therapyRoom;

}
