package com.ayurveda.appointment.dto.response;

import java.util.UUID;

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
public class AppointmentPhysicalExaminationResponse {

    private UUID id;

    private UUID bookingId;

    private Double weight;

    private Double height;

    private Double ibw;

    private Integer pulse;

    private String bp;

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