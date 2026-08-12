package com.ayurveda.notification.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.dto.request.CreateNotificationRequest;
import com.ayurveda.notification.dto.response.NotificationResponse;
import com.ayurveda.notification.dto.response.UnreadCountResponse;
import com.ayurveda.notification.enums.NotificationType;

public interface NotificationService {

    /** Creates a new notification for a recipient user. */
    ApiResponse<NotificationResponse> createNotification(CreateNotificationRequest request);

    /** Lists notifications for a user, optionally filtered by unread and type. */
    ApiResponse<List<NotificationResponse>> getNotifications(
            UUID userId, Boolean unreadOnly, NotificationType type);

    /** Returns a single active notification by ID. */
    ApiResponse<NotificationResponse> getNotificationById(UUID notificationId);

    /** Marks a notification as read. */
    ApiResponse<NotificationResponse> markAsRead(UUID notificationId);

    /** Marks all unread notifications as read for the given user. */
    ApiResponse<Void> markAllAsRead(UUID userId);

    /** Returns the unread notification count for a user. */
    ApiResponse<UnreadCountResponse> getUnreadCount(UUID userId);

    /** Soft-deletes a notification by ID. */
    ApiResponse<Void> deleteNotification(UUID notificationId);

}
