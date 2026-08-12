package com.ayurveda.fileupload.dto.request;

import com.ayurveda.fileupload.enums.DocumentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadAppointmentDocumentRequest {

    @NotNull(message = "Patient Id is required")
    private UUID patientId;

    @NotNull(message = "Document Type is required")
    private DocumentType documentType;

    @NotNull(message = "File is required")
    private MultipartFile file;

}
