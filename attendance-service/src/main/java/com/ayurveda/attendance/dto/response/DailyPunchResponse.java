package com.ayurveda.attendance.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class DailyPunchResponse {

    private LocalTime punchTime;
    private LocalDateTime punchDateTime;
    private String deviceSerialNumber;

}
