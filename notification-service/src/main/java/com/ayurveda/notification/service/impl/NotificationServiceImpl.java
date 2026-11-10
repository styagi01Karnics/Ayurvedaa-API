package com.ayurveda.notification.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.notification.constant.NotificationMessages;
import com.ayurveda.notification.dto.request.CreateNotificationRequest;
import com.ayurveda.notification.dto.response.NotificationResponse;
import com.ayurveda.notification.dto.response.UnreadCountResponse;
import com.ayurveda.notification.entity.Notification;
import com.ayurveda.notification.entity.NotificationRead;
import com.ayurveda.notification.enums.NotificationType;
import com.ayurveda.notification.mapper.NotificationMapper;
import com.ayurveda.notification.push.PushNotificationDispatcher;
import com.ayurveda.notification.repository.NotificationReadRepository;
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
    private final NotificationReadRepository notificationReadRepository;
    private final NotificationMapper notificationMapper;
    private final PushNotificationDispatcher pushNotificationDispatcher;

    @Override
    public ApiResponse<NotificationResponse> createNotification(CreateNotificationRequest request) {
        log.info("Creating {} notification for user {} / role {}",
                request.getType(), request.getRecipientUserId(), request.getRecipientRole());

        if (request.getRecipientUserId() == null && isBlank(request.getRecipientRole())) {
            throw new BadRequestException(NotificationMessages.RECIPIENT_USER_OR_ROLE_REQUIRED);
        }

        Notification saved = notificationRepository.save(notificationMapper.toEntity(request));
        pushNotificationDispatcher.dispatch(saved);
        return ApiResponse.success(
                NotificationMessages.NOTIFICATION_CREATED_SUCCESSFULLY, notificationMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<NotificationResponse>> getNotifications(
            UUID userId, String roleId, Boolean unreadOnly, NotificationType type) {

        log.info("Fetching notifications for userId={}, roleId={}, unreadOnly={}, type={}",
                userId, roleId, unreadOnly, type);

        boolean onlyUnread = Boolean.TRUE.equals(unreadOnly);
        Set<UUID> readRoleNotifications = notificationReadRepository.findReadNotificationIdsByUserId(userId);
        List<NotificationResponse> notifications = notificationRepository
                .findForRecipient(userId, roleId, type)
                .stream()
                .map(n -> toResponse(n, readRoleNotifications))
                .filter(r -> !onlyUnread || !Boolean.TRUE.equals(r.getRead()))
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
    public ApiResponse<NotificationResponse> markAsRead(UUID notificationId, UUID userId, String roleId) {
        log.info("Marking notification as read. Notification ID: {}, userId: {}", notificationId, userId);

        Notification notification = findActive(notificationId);
        NotificationResponse response;
        if (notification.getRecipientUserId() == null) {
            if (userId == null) {
                throw new BadRequestException(NotificationMessages.USER_ID_REQUIRED_FOR_ROLE_NOTIFICATION);
            }
            if (!Boolean.TRUE.equals(notification.getRead())) {
                Set<UUID> readIds = notificationReadRepository.findReadNotificationIdsByUserId(userId);
                if (!readIds.contains(notificationId)) {
                    notificationReadRepository.save(NotificationRead.builder()
                            .notificationId(notificationId)
                            .userId(userId)
                            .readAt(LocalDateTime.now())
                            .build());
                }
            }
            response = toResponse(notification, Set.of(notificationId));
        } else {
            if (!Boolean.TRUE.equals(notification.getRead())) {
                notification.setRead(true);
                notification.setReadAt(LocalDateTime.now());
                notification = notificationRepository.save(notification);
            }
            response = notificationMapper.toResponse(notification);
        }

        log.info("Notification marked as read successfully. Notification ID: {}", notificationId);

        return ApiResponse.success(NotificationMessages.NOTIFICATION_MARKED_AS_READ, response);
    }

    @Override
    public ApiResponse<Void> markAllAsRead(UUID userId, String roleId) {
        log.info("Marking all notifications as read for user {} / role {}", userId, roleId);

        int updated = notificationRepository.markAllReadForUser(userId);

        if (!isBlank(roleId)) {
            Set<UUID> readIds = notificationReadRepository.findReadNotificationIdsByUserId(userId);
            notificationRepository.findForRecipient(userId, roleId, null).stream()
                    .filter(n -> n.getRecipientUserId() == null && !readIds.contains(n.getId()))
                    .forEach(n -> {
                        notificationReadRepository.save(NotificationRead.builder()
                                .notificationId(n.getId())
                                .userId(userId)
                                .readAt(LocalDateTime.now())
                                .build());
                    });
        }

        log.info("Marked {} user notifications as read for user {}", updated, userId);

        return ApiResponse.success(NotificationMessages.ALL_NOTIFICATIONS_MARKED_AS_READ, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UnreadCountResponse> getUnreadCount(UUID userId, String roleId) {
        log.info("Fetching unread notification count for userId: {}, roleId: {}", userId, roleId);

        Set<UUID> readIds = notificationReadRepository.findReadNotificationIdsByUserId(userId);
        long count = notificationRepository.findForRecipient(userId, roleId, null).stream()
                .filter(n -> !isRead(n, readIds))
                .count();

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

    private NotificationResponse toResponse(Notification notification, Set<UUID> readRoleNotificationIds) {
        NotificationResponse response = notificationMapper.toResponse(notification);
        response.setRead(isRead(notification, readRoleNotificationIds));
        return response;
    }

    /** Role-wide notifications track read state per user; targeted ones use the row flag. */
    private boolean isRead(Notification notification, Set<UUID> readRoleNotificationIds) {
        if (notification.getRecipientUserId() == null) {
            return readRoleNotificationIds.contains(notification.getId());
        }
        return Boolean.TRUE.equals(notification.getRead());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Notification findActive(UUID notificationId) {
        return notificationRepository.findByIdAndDeletedFalse(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        NotificationMessages.NOTIFICATION_NOT_FOUND_WITH_ID + notificationId));
    }

}
