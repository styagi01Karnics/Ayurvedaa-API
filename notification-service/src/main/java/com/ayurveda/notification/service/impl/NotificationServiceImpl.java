package com.ayurveda.notification.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.notification.constant.NotificationMessages;
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
        return ApiResponse.success(
                NotificationMessages.NOTIFICATION_CREATED_SUCCESSFULLY, notificationMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<NotificationResponse>> getNotifications(
            UUID userId, Boolean unreadOnly, NotificationType type) {

        log.info("Fetching notifications for userId={}, unreadOnly={}, type={}", userId, unreadOnly, type);

        boolean onlyUnread = Boolean.TRUE.equals(unreadOnly);
        List<NotificationResponse> notifications = notificationRepository
                .findForUser(userId, onlyUnread, type)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();

        log.info("Successfully fetched {} notifications for user {}", notifications.size(), userId);

        return ApiResponse.success(NotificationMessages.NOTIFICATIONS_FETCHED_SUCCESSFULLY, notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<NotificationResponse> getNotificationById(UUID notificationId) {
        log.info("Fetching notification details for notificationId: {}", notificationId);

        Notification notification = findActive(notificationId);

        log.info("Notification fetched successfully. Notification ID: {}", notificationId);

        return ApiResponse.success(notificationMapper.toResponse(notification));
    }

    @Override
    public ApiResponse<NotificationResponse> markAsRead(UUID notificationId) {
        log.info("Marking notification as read. Notification ID: {}", notificationId);

        Notification notification = findActive(notificationId);
        if (!Boolean.TRUE.equals(notification.getRead())) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }

        log.info("Notification marked as read successfully. Notification ID: {}", notificationId);

        return ApiResponse.success(
                NotificationMessages.NOTIFICATION_MARKED_AS_READ, notificationMapper.toResponse(notification));
    }

    @Override
    public ApiResponse<Void> markAllAsRead(UUID userId) {
        log.info("Marking all notifications as read for user {}", userId);

        int updated = notificationRepository.markAllReadForUser(userId);

        log.info("Marked {} notifications as read for user {}", updated, userId);

        return ApiResponse.success(NotificationMessages.ALL_NOTIFICATIONS_MARKED_AS_READ, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UnreadCountResponse> getUnreadCount(UUID userId) {
        log.info("Fetching unread notification count for userId: {}", userId);

        long count = notificationRepository.countByRecipientUserIdAndReadFalseAndDeletedFalse(userId);

        log.info("Unread notification count for user {}: {}", userId, count);

        return ApiResponse.success(
                NotificationMessages.UNREAD_COUNT_FETCHED_SUCCESSFULLY,
                UnreadCountResponse.builder().unreadCount(count).build());
    }

    @Override
    public ApiResponse<Void> deleteNotification(UUID notificationId) {
        log.info("Received request to delete notification with ID: {}", notificationId);

        Notification notification = findActive(notificationId);
        notification.setDeleted(true);
        notificationRepository.save(notification);

        log.info("Notification deleted successfully. Notification ID: {}", notificationId);

        return ApiResponse.success(NotificationMessages.NOTIFICATION_DELETED_SUCCESSFULLY, null);
    }

    private Notification findActive(UUID notificationId) {
        return notificationRepository.findByIdAndDeletedFalse(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        NotificationMessages.NOTIFICATION_NOT_FOUND_WITH_ID + notificationId));
    }

}
