package com.ayurveda.appointment.dto.response;

import java.util.UUID;

import com.ayurveda.appointment.enums.DocumentType;

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
public class AppointmentDocumentResponse {

    private UUID id;

    private UUID bookingId;

    private DocumentType documentType;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String downloadUrl;

}
