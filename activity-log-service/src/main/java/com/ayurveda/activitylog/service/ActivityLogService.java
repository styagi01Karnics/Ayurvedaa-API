package com.ayurveda.activitylog.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.activitylog.dto.request.CreateActivityLogRequest;
import com.ayurveda.activitylog.dto.response.ActivityLogResponse;
import com.ayurveda.activitylog.enums.ActivityAction;
import com.ayurveda.common.ApiResponse;

public interface ActivityLogService {

    ApiResponse<ActivityLogResponse> createActivityLog(CreateActivityLogRequest request);

    ApiResponse<ActivityLogResponse> getActivityLogById(UUID id);

    ApiResponse<List<ActivityLogResponse>> getActivityLogs(
            String page, ActivityAction action, String search);

}
