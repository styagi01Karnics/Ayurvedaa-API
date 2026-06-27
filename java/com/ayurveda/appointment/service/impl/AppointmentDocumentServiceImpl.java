package com.ayurveda.appointment.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.UploadAppointmentDocumentRequest;
import com.ayurveda.appointment.dto.response.AppointmentDocumentResponse;
import com.ayurveda.appointment.entity.AppointmentDocument;
import com.ayurveda.appointment.exception.ResourceNotFoundException;
import com.ayurveda.appointment.mapper.AppointmentDocumentMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentDocumentRepository;
import com.ayurveda.appointment.service.AppointmentDocumentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentDocumentServiceImpl
        implements AppointmentDocumentService {

    private final AppointmentDocumentRepository appointmentDocumentRepository;

    private final AppointmentBookingRepository appointmentBookingRepository;

    private final AppointmentDocumentMapper appointmentDocumentMapper;

    @Override
    public ApiResponse<AppointmentDocumentResponse> uploadDocument(
            UploadAppointmentDocumentRequest request) {

        log.info("Uploading document for booking : {}",
                request.getBookingId());

        appointmentBookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        Constants.APPOINTMENT_NOT_FOUND
                                + request.getBookingId()));

        try {

            AppointmentDocument document = AppointmentDocument.builder()
                    .bookingId(request.getBookingId())
                    .documentType(request.getDocumentType())
                    .fileName(request.getFile().getOriginalFilename())
                    .fileType(request.getFile().getContentType())
                    .fileSize(request.getFile().getSize())
                    .fileData(request.getFile().getBytes())
                    .build();

            AppointmentDocument savedDocument =
                    appointmentDocumentRepository.save(document);

            AppointmentDocumentResponse response =
                    appointmentDocumentMapper.toResponse(savedDocument);

            return ApiResponse.success(
                    Constants.DOCUMENT_UPLOADED,
                    response);

        } catch (Exception ex) {

            throw new RuntimeException("Unable to upload document.", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentDocumentResponse>>
            getDocumentsByBookingId(UUID bookingId) {

        log.info("Fetching documents for booking : {}", bookingId);

        List<AppointmentDocumentResponse> response =
                appointmentDocumentRepository.findByBookingId(bookingId)
                        .stream()
                        .map(appointmentDocumentMapper::toResponse)
                        .collect(Collectors.toList());

        return ApiResponse.success(
                Constants.DOCUMENTS_FETCHED,
                response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadDocument(UUID documentId) {

        AppointmentDocument document =
                appointmentDocumentRepository.findById(documentId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                Constants.DOCUMENT_NOT_FOUND + documentId));

        ByteArrayResource resource =
                new ByteArrayResource(document.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + document.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .contentLength(document.getFileSize())
                .body(resource);
    }

    @Override
    public ApiResponse<String> deleteDocument(UUID documentId) {

        AppointmentDocument document =
                appointmentDocumentRepository.findById(documentId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                Constants.DOCUMENT_NOT_FOUND + documentId));

        appointmentDocumentRepository.delete(document);

        return ApiResponse.success(
                Constants.DOCUMENT_DELETED,
                "Deleted");
    }

}