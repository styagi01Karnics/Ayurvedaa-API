package com.ayurveda.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.dto.response.MessageLogResponse;
import com.ayurveda.notification.enums.MessageChannel;
import com.ayurveda.notification.service.MessageHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Messages", description = "Outbound SMS and email message history")
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageHistoryService messageHistoryService;

    @Operation(summary = "List outbound message history",
            description = "Newest first. Optional channel filter (SMS or EMAIL). page is zero-based; size defaults to 20, max 100.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageLogResponse>>> getMessageHistory(
            @RequestParam(required = false) MessageChannel channel,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        return ResponseEntity.ok(messageHistoryService.getMessageHistory(channel, page, size != null ? size : limit));
    }
}
