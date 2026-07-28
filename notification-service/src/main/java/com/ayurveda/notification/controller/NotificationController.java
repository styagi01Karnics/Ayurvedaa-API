package com.ayurveda.notification.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.dto.request.CreateNotificationRequest;
import com.ayurveda.notification.dto.response.NotificationResponse;
import com.ayurveda.notification.dto.response.UnreadCountResponse;
import com.ayurveda.notification.enums.NotificationType;
import com.ayurveda.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notifications", description = "In-app notification APIs")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Create notification")
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.createNotification(request));
    }

    @Operation(
            summary = "List notifications for a user",
            description = "Filter by unreadOnly and type (APPOINTMENT, BILLING, MEDICINE, THERAPY, SYSTEM, GENERAL).")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @RequestParam UUID userId,
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(required = false) NotificationType type) {

        return ResponseEntity.ok(notificationService.getNotifications(userId, unreadOnly, type));
    }

    @Operation(summary = "Unread notification count")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(@RequestParam UUID userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "Get notification by id")
    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(
            @PathVariable UUID notificationId) {

        return ResponseEntity.ok(notificationService.getNotificationById(notificationId));
    }

    @Operation(summary = "Mark notification as read")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable UUID notificationId) {

        return ResponseEntity.ok(notificationService.markAsRead(notificationId));
    }

    @Operation(summary = "Mark all notifications as read for a user")
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@RequestParam UUID userId) {
        return ResponseEntity.ok(notificationService.markAllAsRead(userId));
    }

    @Operation(summary = "Soft delete notification")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }

}
