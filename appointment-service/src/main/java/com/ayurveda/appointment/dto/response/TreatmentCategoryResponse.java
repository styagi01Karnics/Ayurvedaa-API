package com.ayurveda.appointment.dto.response;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentCategoryResponse {

    private UUID id;
    
    private String categoryCode;

    private String categoryName;

    private String description;

    private Boolean active;

}