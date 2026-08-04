package com.ayurveda.activitylog.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.activitylog.constant.ActivityLogMessages;
import com.ayurveda.activitylog.dto.request.CreateActivityLogRequest;
import com.ayurveda.activitylog.dto.response.ActivityLogResponse;
import com.ayurveda.activitylog.entity.ActivityLog;
import com.ayurveda.activitylog.enums.ActivityAction;
import com.ayurveda.activitylog.mapper.ActivityLogMapper;
import com.ayurveda.activitylog.repository.ActivityLogRepository;
import com.ayurveda.activitylog.service.ActivityLogService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;

    @Override
    public ApiResponse<ActivityLogResponse> createActivityLog(CreateActivityLogRequest request) {
        log.info("Creating activity log for page={}, action={}, target={}",
                request.getPage(), request.getAction(), request.getTarget());

        ActivityLog entity = activityLogMapper.toEntity(request);
        if (entity.getActivityTimestamp() == null) {
            entity.setActivityTimestamp(LocalDateTime.now());
        }

        ActivityLog saved = activityLogRepository.save(entity);
        return ApiResponse.success(
                ActivityLogMessages.ACTIVITY_LOG_CREATED_SUCCESSFULLY, activityLogMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<ActivityLogResponse> getActivityLogById(UUID id) {
        log.info("Fetching activity log details for id: {}", id);

        ActivityLog entity = activityLogRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ActivityLogMessages.ACTIVITY_LOG_NOT_FOUND));

        log.info("Activity log fetched successfully. ID: {}", id);

        return ApiResponse.success(activityLogMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ActivityLogResponse>> getActivityLogs(
            String page, ActivityAction action, String search) {

        log.info("Fetching activity logs with page={}, action={}, search={}", page, action, search);

        String pageFilter = StringUtils.hasText(page) ? page.trim() : null;
        String searchFilter = StringUtils.hasText(search) ? search.trim() : null;

        List<ActivityLogResponse> logs = activityLogRepository
                .search(pageFilter, action, searchFilter)
                .stream()
                .map(activityLogMapper::toResponse)
                .toList();

        log.info("Successfully fetched {} activity logs.", logs.size());

        return ApiResponse.success(ActivityLogMessages.ACTIVITY_LOGS_FETCHED_SUCCESSFULLY, logs);
    }

}
