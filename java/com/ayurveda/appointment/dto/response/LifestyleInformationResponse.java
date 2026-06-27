package com.ayurveda.appointment.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LifestyleInformationResponse {

    private String dietType;

    private String sleepPattern;

    private String exerciseHabits;

    private String addiction;

}
