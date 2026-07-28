package com.ayurveda.notification.dto.request;

import java.util.UUID;

import com.ayurveda.notification.enums.NotificationPriority;
import com.ayurveda.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreateNotificationRequest {

    @NotNull(message = "Recipient user id is required")
    private UUID recipientUserId;

    @Size(max = 150)
    private String recipientUserName;

    @Size(max = 50)
    private String recipientRole;

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    private NotificationPriority priority;

    private UUID referenceId;

    @Size(max = 50)
    private String referenceType;

}
