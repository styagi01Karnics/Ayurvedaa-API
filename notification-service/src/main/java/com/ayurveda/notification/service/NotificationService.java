package com.ayurveda.notification.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.dto.request.CreateNotificationRequest;
import com.ayurveda.notification.dto.response.NotificationResponse;
import com.ayurveda.notification.dto.response.UnreadCountResponse;
import com.ayurveda.notification.enums.NotificationType;

public interface NotificationService {

    /** Creates a notification for a user and/or a role. */
    ApiResponse<NotificationResponse> createNotification(CreateNotificationRequest request);

    /** Lists notifications for a user and their role, optionally filtered by unread and type. */
    ApiResponse<List<NotificationResponse>> getNotifications(
            UUID userId, String roleId, Boolean unreadOnly, NotificationType type);

    /** Returns a single active notification by ID. */
    ApiResponse<NotificationResponse> getNotificationById(UUID notificationId);

    /** Marks a notification as read for the given user (role-wide notifications are tracked per user). */
    ApiResponse<NotificationResponse> markAsRead(UUID notificationId, UUID userId, String roleId);

    /** Marks all unread notifications visible to the user (by user ID and role) as read. */
    ApiResponse<Void> markAllAsRead(UUID userId, String roleId);

    /** Returns the unread notification count for a user and their role. */
    ApiResponse<UnreadCountResponse> getUnreadCount(UUID userId, String roleId);

    /** Soft-deletes a notification by ID. */
    ApiResponse<Void> deleteNotification(UUID notificationId);

}
