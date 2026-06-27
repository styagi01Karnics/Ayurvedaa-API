package com.ayurveda.fileupload.controller;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.fileupload.dto.request.UploadAppointmentDocumentRequest;
import com.ayurveda.fileupload.dto.response.AppointmentDocumentResponse;
import com.ayurveda.fileupload.enums.DocumentType;
import com.ayurveda.fileupload.service.AppointmentDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Validated
public class AppointmentDocumentController {

    private final AppointmentDocumentService appointmentDocumentService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<AppointmentDocumentResponse>> uploadDocument(
            @RequestParam UUID bookingId,
            @RequestParam DocumentType documentType,
            @RequestParam MultipartFile file) {

        UploadAppointmentDocumentRequest request = UploadAppointmentDocumentRequest.builder()
                .bookingId(bookingId)
                .documentType(documentType)
                .file(file)
                .build();

        ApiResponse<AppointmentDocumentResponse> response =
                appointmentDocumentService.uploadDocument(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<List<AppointmentDocumentResponse>>> getDocumentsByBookingId(
            @PathVariable UUID bookingId) {

        ApiResponse<List<AppointmentDocumentResponse>> response =
                appointmentDocumentService.getDocumentsByBookingId(bookingId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID documentId) {
        return appointmentDocumentService.downloadDocument(documentId);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<String>> deleteDocument(@PathVariable UUID documentId) {
        ApiResponse<String> response = appointmentDocumentService.deleteDocument(documentId);
        return ResponseEntity.ok(response);
    }

}
