package com.ayurveda.notification.service;

import java.util.List;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.dto.response.MessageLogResponse;
import com.ayurveda.notification.enums.MessageChannel;

public interface MessageHistoryService {

    ApiResponse<List<MessageLogResponse>> getMessageHistory(MessageChannel channel, Integer page, Integer size);
}
