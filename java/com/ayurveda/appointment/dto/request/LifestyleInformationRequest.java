package com.ayurveda.appointment.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LifestyleInformationRequest {

    private String dietType;

    private String sleepPattern;

    private String exerciseHabits;

    private String addiction;

}