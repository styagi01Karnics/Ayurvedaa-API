package com.ayurveda.fileupload.service.impl;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.fileupload.dto.request.UploadAppointmentDocumentRequest;
import com.ayurveda.fileupload.dto.response.AppointmentDocumentResponse;
import com.ayurveda.fileupload.entity.AppointmentDocument;
import com.ayurveda.fileupload.mapper.AppointmentDocumentMapper;
import com.ayurveda.fileupload.repository.AppointmentDocumentRepository;
import com.ayurveda.fileupload.service.AppointmentDocumentService;
import com.ayurveda.fileupload.util.AppMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentDocumentServiceImpl implements AppointmentDocumentService {

    private final AppointmentDocumentRepository appointmentDocumentRepository;
    private final AppointmentDocumentMapper appointmentDocumentMapper;

    @Override
    public ApiResponse<AppointmentDocumentResponse> uploadDocument(UploadAppointmentDocumentRequest request) {
        log.info("Uploading document for patient: {}", request.getPatientId());

        try {
            AppointmentDocument document = AppointmentDocument.builder()
                    .patientId(request.getPatientId())
                    .documentType(request.getDocumentType())
                    .fileName(request.getFile().getOriginalFilename())
                    .fileType(request.getFile().getContentType())
                    .fileSize(request.getFile().getSize())
                    .fileData(request.getFile().getBytes())
                    .build();

            AppointmentDocument savedDocument = appointmentDocumentRepository.save(document);
            AppointmentDocumentResponse response = appointmentDocumentMapper.toResponse(savedDocument);

            log.info("Document uploaded successfully. Document ID: {}, Patient ID: {}",
                    savedDocument.getId(), savedDocument.getPatientId());

            return ApiResponse.success(AppMessages.DOCUMENT_UPLOADED, response);
        } catch (IOException ex) {
            throw new BadRequestException(AppMessages.UNABLE_TO_UPLOAD_DOCUMENT);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentDocumentResponse>> getDocumentsByPatientId(UUID patientId) {
        log.info("Fetching documents for patient: {}", patientId);

        List<AppointmentDocumentResponse> response = appointmentDocumentRepository.findByPatientId(patientId)
                .stream()
                .map(appointmentDocumentMapper::toResponse)
                .toList();

        log.info("Fetched {} documents for patient: {}", response.size(), patientId);
        return ApiResponse.success(AppMessages.DOCUMENTS_FETCHED, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadDocument(UUID documentId) {
        log.info("Downloading document with ID: {}", documentId);

        AppointmentDocument document = appointmentDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.DOCUMENT_NOT_FOUND + documentId));

        ByteArrayResource resource = new ByteArrayResource(document.getFileData());

        log.info("Document downloaded successfully. Document ID: {}, File name: {}",
                documentId, document.getFileName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .contentLength(document.getFileSize())
                .body(resource);
    }

    @Override
    public ApiResponse<String> deleteDocument(UUID documentId) {
        log.info("Received request to delete document with ID: {}", documentId);

        AppointmentDocument document = appointmentDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.DOCUMENT_NOT_FOUND + documentId));

        appointmentDocumentRepository.delete(document);

        log.info("Document deleted successfully. Document ID: {}", documentId);

        return ApiResponse.success(AppMessages.DOCUMENT_DELETED, "Deleted");
    }

}
