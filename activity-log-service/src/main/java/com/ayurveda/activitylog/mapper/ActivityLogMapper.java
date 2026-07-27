package com.ayurveda.activitylog.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.activitylog.dto.request.CreateActivityLogRequest;
import com.ayurveda.activitylog.dto.response.ActivityLogResponse;
import com.ayurveda.activitylog.entity.ActivityLog;

@Component
public class ActivityLogMapper {

    public ActivityLog toEntity(CreateActivityLogRequest request) {
        return ActivityLog.builder()
                .page(request.getPage().trim())
                .action(request.getAction())
                .target(request.getTarget().trim())
                .beforeValue(blankToNull(request.getBeforeValue()))
                .afterValue(blankToNull(request.getAfterValue()))
                .activityTimestamp(request.getActivityTimestamp())
                .performedByUserId(request.getPerformedByUserId())
                .performedByUserName(blankToNull(request.getPerformedByUserName()))
                .performedByRole(blankToNull(request.getPerformedByRole()))
                .build();
    }

    public ActivityLogResponse toResponse(ActivityLog entity) {
        return ActivityLogResponse.builder()
                .id(entity.getId())
                .page(entity.getPage())
                .action(entity.getAction())
                .target(entity.getTarget())
                .before(entity.getBeforeValue() == null ? "-" : entity.getBeforeValue())
                .after(entity.getAfterValue() == null ? "-" : entity.getAfterValue())
                .timestamp(entity.getActivityTimestamp())
                .performedByUserId(entity.getPerformedByUserId())
                .performedByUserName(entity.getPerformedByUserName())
                .performedByRole(entity.getPerformedByRole())
                .build();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank() || "-".equals(value.trim())) {
            return null;
        }
        return value.trim();
    }

}
