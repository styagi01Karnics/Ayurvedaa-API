package com.ayurveda.notification.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.notification.dto.request.CreateNotificationRequest;
import com.ayurveda.notification.dto.response.NotificationResponse;
import com.ayurveda.notification.dto.response.UnreadCountResponse;
import com.ayurveda.notification.entity.Notification;
import com.ayurveda.notification.enums.NotificationType;
import com.ayurveda.notification.mapper.NotificationMapper;
import com.ayurveda.notification.repository.NotificationRepository;
import com.ayurveda.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public ApiResponse<NotificationResponse> createNotification(CreateNotificationRequest request) {
        log.info("Creating {} notification for user {}", request.getType(), request.getRecipientUserId());

        Notification saved = notificationRepository.save(notificationMapper.toEntity(request));
        return ApiResponse.success("Notification created successfully.", notificationMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<NotificationResponse>> getNotifications(
            UUID userId, Boolean unreadOnly, NotificationType type) {

        boolean onlyUnread = Boolean.TRUE.equals(unreadOnly);
        List<NotificationResponse> notifications = notificationRepository
                .findForUser(userId, onlyUnread, type)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();

        return ApiResponse.success("Notifications fetched successfully.", notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<NotificationResponse> getNotificationById(UUID notificationId) {
        return ApiResponse.success(notificationMapper.toResponse(findActive(notificationId)));
    }

    @Override
    public ApiResponse<NotificationResponse> markAsRead(UUID notificationId) {
        Notification notification = findActive(notificationId);
        if (!Boolean.TRUE.equals(notification.getRead())) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }
        return ApiResponse.success("Notification marked as read.", notificationMapper.toResponse(notification));
    }

    @Override
    public ApiResponse<Void> markAllAsRead(UUID userId) {
        int updated = notificationRepository.markAllReadForUser(userId);
        log.info("Marked {} notifications as read for user {}", updated, userId);
        return ApiResponse.success("All notifications marked as read.", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UnreadCountResponse> getUnreadCount(UUID userId) {
        long count = notificationRepository.countByRecipientUserIdAndReadFalseAndDeletedFalse(userId);
        return ApiResponse.success(
                "Unread count fetched successfully.",
                UnreadCountResponse.builder().unreadCount(count).build());
    }

    @Override
    public ApiResponse<Void> deleteNotification(UUID notificationId) {
        Notification notification = findActive(notificationId);
        notification.setDeleted(true);
        notificationRepository.save(notification);
        return ApiResponse.success("Notification deleted successfully.", null);
    }

    private Notification findActive(UUID notificationId) {
        return notificationRepository.findByIdAndDeletedFalse(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + notificationId));
    }

}
