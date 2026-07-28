package com.ayurveda.common.exception;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.constant.AppConstants;

import feign.FeignException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FeignExceptionHandler {

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(FeignException.NotFound ex) {
        log.warn("Downstream service returned 404: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(
                        HttpStatus.NOT_FOUND.value(),
                        extractMessage(ex, AppConstants.DOWNSTREAM_RESOURCE_NOT_FOUND)));
    }

    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(FeignException.BadRequest ex) {
        log.warn("Downstream service returned 400: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        HttpStatus.BAD_REQUEST.value(),
                        extractMessage(ex, AppConstants.INVALID_DOWNSTREAM_REQUEST)));
    }

    @ExceptionHandler(FeignException.Conflict.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(FeignException.Conflict ex) {
        log.warn("Downstream service returned 409: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(
                        HttpStatus.CONFLICT.value(),
                        extractMessage(ex, AppConstants.DOWNSTREAM_CONFLICT)));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Object>> handleFeign(FeignException ex) {
        log.error("Downstream service call failed with status {}", ex.status(), ex);

        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null || status.is2xxSuccessful()) {
            status = HttpStatus.BAD_GATEWAY;
        }

        String message = status.is5xxServerError() || status == HttpStatus.BAD_GATEWAY
                ? AppConstants.DOWNSTREAM_UNAVAILABLE
                : extractMessage(ex, AppConstants.DOWNSTREAM_REQUEST_FAILED);

        return ResponseEntity.status(status)
                .body(ApiResponse.failure(status.value(), message));
    }

    private String extractMessage(FeignException ex, String fallback) {
        String body = ex.contentUTF8();
        if (!StringUtils.hasText(body) || !body.contains("\"message\"")) {
            return fallback;
        }

        int start = body.indexOf("\"message\"");
        int colon = body.indexOf(':', start);
        int firstQuote = body.indexOf('"', colon + 1);
        int secondQuote = body.indexOf('"', firstQuote + 1);
        if (firstQuote >= 0 && secondQuote > firstQuote) {
            String message = body.substring(firstQuote + 1, secondQuote).trim();
            if (StringUtils.hasText(message)) {
                return message;
            }
        }
        return fallback;
    }

}
