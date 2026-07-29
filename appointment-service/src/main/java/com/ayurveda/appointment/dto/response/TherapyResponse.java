package com.ayurveda.appointment.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.ayurveda.appointment.enums.TherapyMasterStatus;

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
public class TherapyResponse {

    private UUID id;

    private String name;

    private String therapyName;

    private String therapyCode;

    private UUID categoryId;

    private String categoryName;

    private TherapyMasterStatus status;

    private Integer durationMinutes;

    private BigDecimal price;

    private String description;

}
