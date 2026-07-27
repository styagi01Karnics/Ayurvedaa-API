package com.ayurveda.therapist.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ayurveda.therapist.enums.TherapistStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TherapistResponse {

    private Integer serialNo;
    private UUID id;
    private String name;
    private String therapistName;
    private String therapistCode;
    private TherapistStatus status;
    private List<String> assignedTherapies;
    private String specialization;
    private String mobileNumber;
    private String email;
    private String qualification;
    private String therapyRoom;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
