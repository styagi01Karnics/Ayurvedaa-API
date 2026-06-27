package com.ayurveda.fileupload.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.fileupload.dto.request.UploadAppointmentDocumentRequest;
import com.ayurveda.fileupload.dto.response.AppointmentDocumentResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface AppointmentDocumentService {

    ApiResponse<AppointmentDocumentResponse> uploadDocument(UploadAppointmentDocumentRequest request);

    ApiResponse<List<AppointmentDocumentResponse>> getDocumentsByBookingId(UUID bookingId);

    ResponseEntity<Resource> downloadDocument(UUID documentId);

    ApiResponse<String> deleteDocument(UUID documentId);

}
