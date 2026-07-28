package com.ayurveda.activitylog.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.activitylog.enums.ActivityAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreateActivityLogRequest {

    @NotBlank(message = "Page is required")
    @Size(max = 100)
    private String page;

    @NotNull(message = "Action is required")
    private ActivityAction action;

    @NotBlank(message = "Target is required")
    @Size(max = 150)
    private String target;

    private String beforeValue;

    private String afterValue;

    private LocalDateTime activityTimestamp;

    private UUID performedByUserId;

    @Size(max = 150)
    private String performedByUserName;

    @Size(max = 50)
    private String performedByRole;

}
