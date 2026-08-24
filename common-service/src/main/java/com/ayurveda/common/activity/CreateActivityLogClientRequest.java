package com.ayurveda.common.activity;

import java.time.LocalDateTime;
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
public class CreateActivityLogClientRequest {

    private String page;
    private ActivityActionType action;
    private String target;
    private String beforeValue;
    private String afterValue;
    private LocalDateTime activityTimestamp;
    private UUID performedByUserId;
    private String performedByUserName;
    private String performedByRole;

}
