package com.ayurveda.appointment.dto.request;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.ayurveda.appointment.enums.DocumentType;

import jakarta.validation.constraints.NotNull;
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
public class UploadAppointmentDocumentRequest {

    @NotNull(message = "Booking Id is required")
    private UUID bookingId;

    @NotNull(message = "Document Type is required")
    private DocumentType documentType;

    @NotNull(message = "File is required")
    private MultipartFile file;

}