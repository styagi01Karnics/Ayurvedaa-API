package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentDocument;
import com.ayurveda.appointment.enums.DocumentType;

@Repository
public interface AppointmentDocumentRepository
        extends JpaRepository<AppointmentDocument, UUID> {

    List<AppointmentDocument> findByBookingId(UUID bookingId);

    List<AppointmentDocument> findByBookingIdAndDocumentType(
            UUID bookingId,
            DocumentType documentType);

}