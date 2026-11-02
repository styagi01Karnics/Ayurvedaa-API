package com.ayurveda.appointment.service;

import com.ayurveda.appointment.dto.request.CreateTreatmentPlanMasterRequest;
import com.ayurveda.appointment.dto.response.TreatmentPlanMasterResponse;
import com.ayurveda.common.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface TreatmentPlanMasterService {

    /** Creates a new treatment plan master record. */
    ApiResponse<TreatmentPlanMasterResponse> create(CreateTreatmentPlanMasterRequest request);

    /** Fetches a treatment plan by ID. */
    ApiResponse<TreatmentPlanMasterResponse> getById(UUID treatmentPlanId);

    /** Lists all non-deleted treatment plans. */
    ApiResponse<List<TreatmentPlanMasterResponse>> getAll();

    /** Lists only active, non-deleted treatment plans. */
    ApiResponse<List<TreatmentPlanMasterResponse>> getActive();

    /**
     * Updates a treatment plan master record by id.
     */
    ApiResponse<TreatmentPlanMasterResponse> updateTreatmentPlanMaster(UUID treatmentPlanId, CreateTreatmentPlanMasterRequest request);

    /**
     * Deactivates a treatment plan master record by id.
     */
    ApiResponse<Void> deleteTreatmentPlanMaster(UUID treatmentPlanId);

}
