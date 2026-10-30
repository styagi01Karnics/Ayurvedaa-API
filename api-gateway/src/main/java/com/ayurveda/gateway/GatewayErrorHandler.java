package com.ayurveda.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Returns the same {@code ApiResponse} envelope as the backend services, with a meaningful
 * status when a backend is down (503) or too slow (504), instead of a generic 500.
 */
@Component
@Order(-2)
public class GatewayErrorHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayErrorHandler.class);

    static final String UNAVAILABLE_MESSAGE = "Service is temporarily unavailable. Please try again later.";
    static final String TIMEOUT_MESSAGE = "Downstream service did not respond in time. Please try again later.";

    private final ObjectMapper objectMapper;

    public GatewayErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status;
        String message;
        if (ex instanceof ResponseStatusException rse && HttpStatus.resolve(rse.getStatusCode().value()) != null) {
            status = HttpStatus.resolve(rse.getStatusCode().value());
            message = rse.getReason() != null ? rse.getReason() : status.getReasonPhrase();
        } else if (isTimeout(ex)) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            message = TIMEOUT_MESSAGE;
        } else if (isUnreachable(ex)) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = UNAVAILABLE_MESSAGE;
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = UNAVAILABLE_MESSAGE;
        }

        if (status.is5xxServerError()) {
            log.error("Gateway error for {} {}: {}", exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath(), ex.toString());
        } else {
            log.warn("Gateway rejected {} {} with {}", exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath(), status.value());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("status", status.value());
        body.put("message", message);
        body.put("data", null);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory factory = exchange.getResponse().bufferFactory();
        DataBuffer buffer = factory.wrap(write(body).getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private boolean isTimeout(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof TimeoutException) {
                return true;
            }
            if (current.getClass().getName().equals("io.netty.handler.timeout.ReadTimeoutException")
                    || current.getClass().getName().equals("io.netty.handler.timeout.WriteTimeoutException")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isUnreachable(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof ConnectException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private String write(Map<String, Object> body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            return "{\"success\":false,\"status\":503,\"message\":\"" + UNAVAILABLE_MESSAGE + "\",\"data\":null}";
        }
    }
}
