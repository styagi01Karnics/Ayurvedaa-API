package com.ayurveda.fileupload.repository;

import com.ayurveda.fileupload.entity.AppointmentDocument;
import com.ayurveda.fileupload.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppointmentDocumentRepository extends JpaRepository<AppointmentDocument, UUID> {

    List<AppointmentDocument> findByPatientId(UUID patientId);

    List<AppointmentDocument> findByPatientIdAndDocumentType(UUID patientId, DocumentType documentType);

}
