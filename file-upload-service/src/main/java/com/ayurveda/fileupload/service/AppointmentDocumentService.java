package com.ayurveda.fileupload.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.fileupload.dto.request.UploadAppointmentDocumentRequest;
import com.ayurveda.fileupload.dto.response.AppointmentDocumentResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface AppointmentDocumentService {

    /** Uploads a document and associates it with a patient. */
    ApiResponse<AppointmentDocumentResponse> uploadDocument(UploadAppointmentDocumentRequest request);

    /** Lists all documents linked to a patient. */
    ApiResponse<List<AppointmentDocumentResponse>> getDocumentsByPatientId(UUID patientId);

    /** Downloads a document by ID as a binary attachment. */
    ResponseEntity<Resource> downloadDocument(UUID documentId);

    /** Permanently deletes a document by ID. */
    ApiResponse<String> deleteDocument(UUID documentId);

}
