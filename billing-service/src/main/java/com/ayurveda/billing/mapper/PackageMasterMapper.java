package com.ayurveda.billing.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.billing.dto.request.CreatePackageMasterRequest;
import com.ayurveda.billing.dto.response.PackageMasterResponse;
import com.ayurveda.billing.entity.PackageMaster;
import com.ayurveda.billing.enums.PackageMasterStatus;

@Component
public class PackageMasterMapper {

    public PackageMaster toEntity(CreatePackageMasterRequest request) {
        PackageMasterStatus status =
                request.getStatus() != null ? request.getStatus() : PackageMasterStatus.ACTIVE;

        PackageMaster entity = PackageMaster.builder()
                .name(request.getName().trim())
                .packagePrice(request.getPackagePrice())
                .status(status)
                .build();
        entity.setDeleted(false);
        return entity;
    }

    public PackageMasterResponse toResponse(PackageMaster entity) {
        return PackageMasterResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .packagePrice(entity.getPackagePrice())
                .status(entity.getStatus())
                .build();
    }

}
