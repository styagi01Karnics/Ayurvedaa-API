package com.ayurveda.therapist.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TherapistResponse {

    private UUID id;
    private String therapistName;
    private String therapistCode;
    private String specialization;
    private String mobileNumber;
    private String email;
    private String qualification;
    private String therapyRoom;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
