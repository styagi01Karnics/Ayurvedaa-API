package com.ayurveda.appointment.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.appointment.enums.FollowUpStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreateFollowUpRequest {

    @NotNull(message = "Patient id is required")
    private UUID patientId;

    @NotNull(message = "Assigned doctor id is required")
    private UUID assignedDoctorId;

    /** Optional consult booking that created this follow-up. */
    private UUID sourceBookingId;

    @NotNull(message = "Visit type id is required")
    private UUID visitTypeId;

    @NotNull(message = "Appointment date is required")
    private LocalDateTime appointmentDate;

    @Size(max = 50)
    private String schedulingOption;

    private Boolean smsReminderEnabled;

    /** Optional; defaults to UPCOMING. */
    private FollowUpStatus status;

}
