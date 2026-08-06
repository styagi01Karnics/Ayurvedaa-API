package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateTreatmentPlanMasterRequest;
import com.ayurveda.appointment.dto.response.TreatmentPlanMasterResponse;
import com.ayurveda.appointment.entity.TreatmentPlanMaster;
import com.ayurveda.appointment.enums.TreatmentPlanMasterStatus;

@Component
public class TreatmentPlanMasterMapper {

    public TreatmentPlanMaster toEntity(CreateTreatmentPlanMasterRequest request) {
        TreatmentPlanMasterStatus status =
                request.getStatus() != null ? request.getStatus() : TreatmentPlanMasterStatus.ACTIVE;

        TreatmentPlanMaster entity = TreatmentPlanMaster.builder()
                .name(request.getName().trim())
                .status(status)
                .build();
        entity.setDeleted(false);
        return entity;
    }

    public TreatmentPlanMasterResponse toResponse(TreatmentPlanMaster entity) {
        return TreatmentPlanMasterResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(entity.getStatus())
                .build();
    }

}
