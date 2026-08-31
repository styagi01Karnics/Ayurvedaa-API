package com.ayurveda.appointment.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.appointment.enums.FollowUpStatus;

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
public class FollowUpResponse {

    private UUID id;
    private UUID patientId;
    private String patientCode;
    private String patientName;
    private UUID assignedDoctorId;
    private String doctorName;
    private UUID sourceBookingId;
    private UUID visitTypeId;
    private String visitTypeName;
    private LocalDateTime appointmentDate;
    private String schedulingOption;
    private Boolean smsReminderEnabled;
    private FollowUpStatus status;

}
