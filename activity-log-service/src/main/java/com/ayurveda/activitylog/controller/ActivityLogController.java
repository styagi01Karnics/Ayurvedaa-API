package com.ayurveda.activitylog.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.activitylog.dto.request.CreateActivityLogRequest;
import com.ayurveda.activitylog.dto.response.ActivityLogResponse;
import com.ayurveda.activitylog.enums.ActivityAction;
import com.ayurveda.activitylog.service.ActivityLogService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Activity Logs", description = "System activity log APIs")
@RestController
@RequestMapping("/api/v1/activity-logs")
@RequiredArgsConstructor
@Validated
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @Operation(summary = "Create activity log")
    @PostMapping
    public ResponseEntity<ApiResponse<ActivityLogResponse>> createActivityLog(
            @Valid @RequestBody CreateActivityLogRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityLogService.createActivityLog(request));
    }

    @Operation(
            summary = "List activity logs",
            description = "Supports search (page/action/target/user) and optional page/action filters.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getActivityLogs(
            @RequestParam(required = false) String page,
            @RequestParam(required = false) ActivityAction action,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(activityLogService.getActivityLogs(page, action, search));
    }

    @Operation(summary = "Get activity log by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityLogResponse>> getActivityLogById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(activityLogService.getActivityLogById(id));
    }

}
