package com.ayurveda.notification.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.common.BaseEntity;
import com.ayurveda.notification.enums.NotificationPriority;
import com.ayurveda.notification.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private UUID recipientUserId;

    @Column(length = 150)
    private String recipientUserName;

    @Column(length = 50)
    private String recipientRole;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    private UUID referenceId;

    @Column(length = 50)
    private String referenceType;

    @Builder.Default
    @Column(nullable = false)
    private Boolean read = false;

    private LocalDateTime readAt;

}
