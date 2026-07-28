package com.ayurveda.notification.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.notification.enums.NotificationPriority;
import com.ayurveda.notification.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;
    private UUID recipientUserId;
    private String recipientUserName;
    private String recipientRole;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationPriority priority;
    private UUID referenceId;
    private String referenceType;
    private Boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

}
