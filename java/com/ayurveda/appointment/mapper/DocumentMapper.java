package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.response.DocumentResponse;
import com.ayurveda.appointment.entity.AppointmentDocument;

@Component
public class DocumentMapper {

    public DocumentResponse toResponse(AppointmentDocument entity) {

        if (entity == null) {
            return null;
        }

        return DocumentResponse.builder()
                .documentId(entity.getId())
                .documentType(entity.getDocumentType() == null
                        ? null
                        : entity.getDocumentType().name())
                .fileName(entity.getFileName())
                .contentType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .build();
    }
}