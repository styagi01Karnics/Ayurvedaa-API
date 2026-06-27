package com.ayurveda.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.UploadAppointmentDocumentRequest;
import com.ayurveda.appointment.dto.response.AppointmentDocumentResponse;
import com.ayurveda.appointment.enums.DocumentType;
import com.ayurveda.appointment.service.AppointmentDocumentService;

import lombok.RequiredArgsConstructor;

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

        UploadAppointmentDocumentRequest request =
                UploadAppointmentDocumentRequest.builder()
                        .bookingId(bookingId)
                        .documentType(documentType)
                        .file(file)
                        .build();

        ApiResponse<AppointmentDocumentResponse> response =
                appointmentDocumentService.uploadDocument(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<List<AppointmentDocumentResponse>>>
            getDocumentsByBookingId(
                    @PathVariable UUID bookingId) {

        ApiResponse<List<AppointmentDocumentResponse>> response =
                appointmentDocumentService
                        .getDocumentsByBookingId(bookingId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable UUID documentId) {

        return appointmentDocumentService.downloadDocument(documentId);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<String>> deleteDocument(
            @PathVariable UUID documentId) {

        ApiResponse<String> response =
                appointmentDocumentService.deleteDocument(documentId);

        return ResponseEntity.ok(response);
    }

}