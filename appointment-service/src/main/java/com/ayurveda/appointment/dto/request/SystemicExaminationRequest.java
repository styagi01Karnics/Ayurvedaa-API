package com.ayurveda.appointment.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemicExaminationRequest {

    private String cardiovascular;

    private String respiratory;

    private String nervous;

    private String abdomenGI;

    private String locomotor;

}