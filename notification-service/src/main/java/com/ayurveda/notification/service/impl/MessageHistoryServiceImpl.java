package com.ayurveda.notification.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.constant.NotificationMessages;
import com.ayurveda.notification.dto.response.MessageLogResponse;
import com.ayurveda.notification.entity.EmailDeliveryLog;
import com.ayurveda.notification.enums.MessageChannel;
import com.ayurveda.notification.repository.EmailDeliveryLogRepository;
import com.ayurveda.notification.service.MessageHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Outbound message history. Only email attempts are persisted today, so SMS history is always empty
 * and the email body is not stored (the subject is returned as the message text).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHistoryServiceImpl implements MessageHistoryService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final EmailDeliveryLogRepository emailDeliveryLogRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<MessageLogResponse>> getMessageHistory(
            MessageChannel channel, Integer page, Integer size) {

        int pageIndex = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        log.info("Fetching message history channel={} page={} size={}", channel, pageIndex, pageSize);

        if (channel == MessageChannel.SMS) {
            return ApiResponse.success(NotificationMessages.MESSAGE_HISTORY_FETCHED_SUCCESSFULLY, List.of());
        }

        List<MessageLogResponse> rows = emailDeliveryLogRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(pageIndex, pageSize))
                .stream()
                .map(MessageHistoryServiceImpl::toEmailRow)
                .toList();

        return ApiResponse.success(NotificationMessages.MESSAGE_HISTORY_FETCHED_SUCCESSFULLY, rows);
    }

    private static MessageLogResponse toEmailRow(EmailDeliveryLog entry) {
        return MessageLogResponse.builder()
                .id(entry.getId())
                .channel(MessageChannel.EMAIL)
                .recipient(entry.getRecipientEmail())
                .subject(entry.getSubject())
                .message(entry.getSubject())
                .status(toStatus(entry.getDeliveryStatus()))
                .createdAt(entry.getCreatedAt())
                .build();
    }

    private static String toStatus(String deliveryStatus) {
        if ("SENT".equals(deliveryStatus)) {
            return "SENT";
        }
        if ("FAILED".equals(deliveryStatus) || "SKIPPED".equals(deliveryStatus)) {
            return "FAILED";
        }
        return "QUEUED";
    }
}
