package com.ayurveda.appointment.entity;

import java.util.UUID;

import com.ayurveda.appointment.common.BaseEntity;
import com.ayurveda.appointment.enums.DocumentType;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_documents")
public class AppointmentDocument extends BaseEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable =false)
    private String fileType;

    @Column(nullable =false)
    private Long fileSize;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

}