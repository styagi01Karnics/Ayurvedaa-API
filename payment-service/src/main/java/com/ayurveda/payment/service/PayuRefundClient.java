package com.ayurveda.payment.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.payment.config.PayuCredentials;
import com.ayurveda.payment.config.PayuProperties;
import com.ayurveda.payment.constant.PaymentMessages;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Calls PayU {@code cancel_refund_transaction} (full / partial refund of captured payments).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PayuRefundClient {

    private static final String COMMAND = "cancel_refund_transaction";

    private final PayuProperties payuProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public Result refund(
            PayuCredentials credentials,
            String mihpayid,
            String refundToken,
            BigDecimal amount) {
        if (mihpayid == null || mihpayid.isBlank()) {
            throw new BadRequestException(PaymentMessages.REFUND_REQUIRES_MIHPAYID);
        }
        String amountStr = PayuHashService.formatAmount(amount);
        String hash = PayuHashService.sha512(String.join("|",
                blank(credentials.getMerchantKey()),
                COMMAND,
                blank(mihpayid),
                blank(credentials.getMerchantSalt())));

        Map<String, String> form = new LinkedHashMap<>();
        form.put("key", credentials.getMerchantKey());
        form.put("command", COMMAND);
        form.put("var1", mihpayid.trim());
        form.put("var2", refundToken);
        form.put("var3", amountStr);
        form.put("hash", hash);

        String body = encodeForm(form);
        String url = payuProperties.resolvedPostserviceUrl();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("PayU refund HTTP {} mihpayid={} amount={}", response.statusCode(), mihpayid, amountStr);
            return parse(response.body());
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("PayU refund call failed: {}", ex.getMessage(), ex);
            throw new BadRequestException(PaymentMessages.REFUND_GATEWAY_FAILED + ex.getMessage());
        }
    }

    private Result parse(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            throw new BadRequestException(PaymentMessages.REFUND_GATEWAY_FAILED + "empty response");
        }
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            int status = root.path("status").asInt(0);
            String msg = text(root, "msg");
            String requestId = firstNonBlank(
                    text(root, "request_id"),
                    text(root, "txn_update_id"),
                    text(root.path("details"), "request_id"));
            if (status != 1) {
                throw new BadRequestException(
                        PaymentMessages.REFUND_GATEWAY_FAILED + (msg.isBlank() ? rawBody : msg));
            }
            return new Result(true, msg, requestId, rawBody);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            // Some PayU environments return pipe-delimited text
            if (rawBody.contains("status=1") || rawBody.trim().startsWith("1")) {
                return new Result(true, rawBody, "", rawBody);
            }
            throw new BadRequestException(PaymentMessages.REFUND_GATEWAY_FAILED + rawBody);
        }
    }

    private static String encodeForm(Map<String, String> form) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : form.entrySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(blank(e.getValue()), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private static String text(JsonNode node, String field) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return "";
        }
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? "" : value.asText("").trim();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private static String blank(String value) {
        return value == null ? "" : value.trim();
    }

    public record Result(boolean success, String message, String requestId, String rawBody) {
    }
}
