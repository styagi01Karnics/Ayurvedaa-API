package com.ayurveda.fileupload.mapper;

import com.ayurveda.fileupload.dto.response.AppointmentDocumentResponse;
import com.ayurveda.fileupload.entity.AppointmentDocument;
import org.springframework.stereotype.Component;

@Component
public class AppointmentDocumentMapper {

    public AppointmentDocumentResponse toResponse(AppointmentDocument entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentDocumentResponse.builder()
                .id(entity.getId())
                .bookingId(entity.getBookingId())
                .documentType(entity.getDocumentType())
                .fileName(entity.getFileName())
                .fileType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .downloadUrl("/api/v1/documents/" + entity.getId() + "/download")
                .build();
    }

}
