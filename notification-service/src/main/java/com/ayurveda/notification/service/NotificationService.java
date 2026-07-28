package com.ayurveda.notification.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.dto.request.CreateNotificationRequest;
import com.ayurveda.notification.dto.response.NotificationResponse;
import com.ayurveda.notification.dto.response.UnreadCountResponse;
import com.ayurveda.notification.enums.NotificationType;

public interface NotificationService {

    ApiResponse<NotificationResponse> createNotification(CreateNotificationRequest request);

    ApiResponse<List<NotificationResponse>> getNotifications(
            UUID userId, Boolean unreadOnly, NotificationType type);

    ApiResponse<NotificationResponse> getNotificationById(UUID notificationId);

    ApiResponse<NotificationResponse> markAsRead(UUID notificationId);

    ApiResponse<Void> markAllAsRead(UUID userId);

    ApiResponse<UnreadCountResponse> getUnreadCount(UUID userId);

    ApiResponse<Void> deleteNotification(UUID notificationId);

}
