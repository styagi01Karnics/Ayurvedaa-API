package com.ayurveda.appointment.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalExaminationRequest {

    private Double weight;

    private Double height;

    private Double ibw;

    private Integer pulse;

    private String bloodPressure;

    private Double temperature;

    private String pallor;

    private String icterus;

    private String cyanosis;

    private String lymphNodes;

    private String oedema;

    private String sensorium;

    private String acidityGas;

    private String motion;

    private String micturition;

}