package com.ayurveda.appointment.service;

import com.ayurveda.appointment.dto.request.CreateConsultationTypeRequest;
import com.ayurveda.appointment.dto.response.ConsultationTypeResponse;
import com.ayurveda.common.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface ConsultationTypeMasterService {

    /** Creates a new consultation type master record. */
    ApiResponse<ConsultationTypeResponse> create(CreateConsultationTypeRequest request);

    /** Fetches a consultation type by ID. */
    ApiResponse<ConsultationTypeResponse> getById(UUID consultationTypeId);

    /** Lists all non-deleted consultation types. */
    ApiResponse<List<ConsultationTypeResponse>> getAll();

    /** Lists only active, non-deleted consultation types. */
    ApiResponse<List<ConsultationTypeResponse>> getActive();

    /**
     * Updates a consultation type by id.
     */
    ApiResponse<ConsultationTypeResponse> updateConsultationType(UUID consultationTypeId, CreateConsultationTypeRequest request);

    /**
     * Deactivates a consultation type by id.
     */
    ApiResponse<Void> deleteConsultationType(UUID consultationTypeId);

}
