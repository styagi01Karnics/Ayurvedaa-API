package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.UploadAppointmentDocumentRequest;
import com.ayurveda.appointment.dto.response.AppointmentDocumentResponse;

public interface AppointmentDocumentService {

    ApiResponse<AppointmentDocumentResponse> uploadDocument(
            UploadAppointmentDocumentRequest request);

    ApiResponse<List<AppointmentDocumentResponse>> getDocumentsByBookingId(
            UUID bookingId);

    ResponseEntity<Resource> downloadDocument(
            UUID documentId);

    ApiResponse<String> deleteDocument(
            UUID documentId);

}