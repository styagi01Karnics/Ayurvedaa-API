package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.ayurveda.billing.enums.PackageMasterStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageMasterResponse {

    private UUID id;

    private String name;

    private BigDecimal packagePrice;

    private PackageMasterStatus status;

}
