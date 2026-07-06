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
public class AppointmentLifestyleInformationResponse {

    private UUID id;

    private UUID patientId;

    private String dietType;

    private String sleepPattern;

    private String exerciseHabits;

    private String addiction;

}