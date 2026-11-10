package com.ayurveda.notification.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.notification.enums.MessageChannel;

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
public class MessageLogResponse {

    private UUID id;
    private MessageChannel channel;
    private String recipient;
    private String recipientName;
    private String subject;
    private String message;
    /** SENT, FAILED or QUEUED */
    private String status;
    private LocalDateTime createdAt;
}
