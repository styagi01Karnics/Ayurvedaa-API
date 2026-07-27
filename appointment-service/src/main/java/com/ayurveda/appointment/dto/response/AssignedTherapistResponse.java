package com.ayurveda.appointment.dto.response;

import java.util.List;
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
public class AssignedTherapistResponse {

    private UUID therapistId;

    private String therapistName;

    private String therapistCode;

    private String mobileNumber;

    private List<UUID> therapyIds;

    private List<String> therapyNames;

}
