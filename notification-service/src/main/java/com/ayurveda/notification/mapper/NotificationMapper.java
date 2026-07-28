package com.ayurveda.notification.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.notification.dto.request.CreateNotificationRequest;
import com.ayurveda.notification.dto.response.NotificationResponse;
import com.ayurveda.notification.entity.Notification;
import com.ayurveda.notification.enums.NotificationPriority;

@Component
public class NotificationMapper {

    public Notification toEntity(CreateNotificationRequest request) {
        return Notification.builder()
                .recipientUserId(request.getRecipientUserId())
                .recipientUserName(request.getRecipientUserName())
                .recipientRole(request.getRecipientRole())
                .title(request.getTitle().trim())
                .message(request.getMessage().trim())
                .type(request.getType())
                .priority(request.getPriority() != null ? request.getPriority() : NotificationPriority.MEDIUM)
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .read(false)
                .build();
    }

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipientUserId(notification.getRecipientUserId())
                .recipientUserName(notification.getRecipientUserName())
                .recipientRole(notification.getRecipientRole())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .priority(notification.getPriority())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .read(notification.getRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
