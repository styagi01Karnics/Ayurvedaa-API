package com.ayurveda.activitylog.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.activitylog.dto.request.CreateActivityLogRequest;
import com.ayurveda.activitylog.dto.response.ActivityLogResponse;
import com.ayurveda.activitylog.enums.ActivityAction;
import com.ayurveda.common.ApiResponse;

public interface ActivityLogService {

    /** Creates a new activity log entry. */
    ApiResponse<ActivityLogResponse> createActivityLog(CreateActivityLogRequest request);

    /** Returns a single active activity log by ID. */
    ApiResponse<ActivityLogResponse> getActivityLogById(UUID id);

    /** Lists activity logs filtered by page, action, and optional search text. */
    ApiResponse<List<ActivityLogResponse>> getActivityLogs(
            String page, ActivityAction action, String search);

}
