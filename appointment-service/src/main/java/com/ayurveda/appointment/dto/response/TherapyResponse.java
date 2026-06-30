package com.ayurveda.appointment.dto.response;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TherapyResponse {

    private UUID id;

    private UUID categoryId;

    private String therapyCode;

    private String therapyName;

    private String description;

    private Boolean active;

}