package com.ayurveda.appointment.dto.response;

import java.util.UUID;

import com.ayurveda.appointment.enums.DoshaMasterStatus;
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
public class DoshaResponse {

    private UUID id;

    private String name;

    private String elements;

    private String characteristics;

    private DoshaMasterStatus status;

}
