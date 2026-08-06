package com.ayurveda.appointment.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.ayurveda.appointment.enums.TreatmentStatus;

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
public class TreatmentResponse {

    private UUID id;
    private UUID patientId;
    private UUID treatmentPlanId;
    private String treatmentPlanName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalSessions;
    private Integer completedSessions;
    private Integer remainingSessions;
    private UUID assignedTherapistId;
    private String assignedTherapistName;
    private TreatmentStatus treatmentStatus;

}
