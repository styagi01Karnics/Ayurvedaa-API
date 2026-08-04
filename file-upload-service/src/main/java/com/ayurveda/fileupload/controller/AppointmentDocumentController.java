package com.ayurveda.fileupload.controller;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.fileupload.dto.request.UploadAppointmentDocumentRequest;
import com.ayurveda.fileupload.dto.response.AppointmentDocumentResponse;
import com.ayurveda.fileupload.enums.DocumentType;
import com.ayurveda.fileupload.service.AppointmentDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Documents", description = "Patient document upload APIs")
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Validated
public class AppointmentDocumentController {

    private final AppointmentDocumentService appointmentDocumentService;

    @Operation(summary = "Upload a patient document")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<AppointmentDocumentResponse>> uploadDocument(
            @RequestParam UUID patientId,
            @RequestParam DocumentType documentType,
            @RequestParam MultipartFile file) {

        UploadAppointmentDocumentRequest request = UploadAppointmentDocumentRequest.builder()
                .patientId(patientId)
                .documentType(documentType)
                .file(file)
                .build();

        ApiResponse<AppointmentDocumentResponse> response =
                appointmentDocumentService.uploadDocument(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List documents for a patient")
    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<List<AppointmentDocumentResponse>>> getDocumentsByPatientId(
            @PathVariable UUID patientId) {

        ApiResponse<List<AppointmentDocumentResponse>> response =
                appointmentDocumentService.getDocumentsByPatientId(patientId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Download a document")
    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID documentId) {
        return appointmentDocumentService.downloadDocument(documentId);
    }

    @Operation(summary = "Delete a document")
    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<String>> deleteDocument(@PathVariable UUID documentId) {
        ApiResponse<String> response = appointmentDocumentService.deleteDocument(documentId);
        return ResponseEntity.ok(response);
    }

}
