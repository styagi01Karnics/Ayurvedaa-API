package com.ayurveda.appointment.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
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
public class CreateAppointmentPhysicalExaminationRequest {

    @NotNull(message = "Booking Id is required")
    private UUID bookingId;

    @NotNull(message = "Weight is required")
    private Double weight;

    @NotNull(message = "Height is required")
    private Double height;

    @NotNull(message = "IBW is required")
    private Double ibw;

    @NotNull(message = "Pulse is required")
    private Integer pulse;

    @NotNull(message = "Blood Pressure is required")
    private String bp;

    @NotNull(message = "Temperature is required")
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