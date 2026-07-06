package com.ayurveda.appointment.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveAppointmentTherapyRequest {

    @NotNull
    private UUID patientId;

    @NotNull
    private UUID treatmentCategoryId;

    @NotNull
    private List<UUID> therapyIds;

    @NotNull
    private LocalDate scheduleDate;

    @NotNull
    private LocalTime scheduleTime;

    @NotNull
    private Integer sessionDuration;

    @NotNull
    private Integer sessionFrequency;

    @NotNull
    private UUID assignedTherapistId;

    private String therapyInstructions;

}