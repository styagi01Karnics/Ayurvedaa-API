package com.ayurveda.appointment.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemicExaminationResponse {

    private String cardiovascular;

    private String respiratory;

    private String nervous;

    private String abdomenGI;

    private String locomotor;

}
