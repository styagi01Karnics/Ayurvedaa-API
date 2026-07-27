package com.ayurveda.activitylog.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.activitylog.enums.ActivityAction;

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
public class ActivityLogResponse {

    private UUID id;
    private String page;
    private ActivityAction action;
    private String target;
    private String before;
    private String after;
    private LocalDateTime timestamp;
    private UUID performedByUserId;
    private String performedByUserName;
    private String performedByRole;

}
