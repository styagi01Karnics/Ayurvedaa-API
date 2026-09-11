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
 * PayU Dynamic BQR (UPI QR) — returns {@code upi://pay?...&am=...} for on-screen QR.
 * Requires DBQR enabled on the merchant (contact PayU if pg=DBQR is rejected).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PayuDynamicQrClient {

    private final PayuProperties payuProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public Result createUpiQr(
            PayuCredentials credentials,
            String txnId,
            BigDecimal amount,
            String productInfo,
            String firstName,
            String email,
            String phone,
            String udf1,
            String udf2,
            String udf3,
            String udf4,
            String udf5,
            String hash,
            String clientIp,
            String deviceInfo) {

        String amountStr = PayuHashService.formatAmount(amount);
        Map<String, String> form = new LinkedHashMap<>();
        form.put("key", credentials.getMerchantKey());
        form.put("txnid", txnId);
        form.put("amount", amountStr);
        form.put("productinfo", blank(productInfo, "Hospital payment"));
        form.put("firstname", blank(firstName, "Patient"));
        form.put("email", blank(email, "patient@example.com"));
        form.put("phone", blank(phone, "9999999999"));
        form.put("surl", payuProperties.getSuccessUrl());
        form.put("furl", payuProperties.getFailureUrl());
        form.put("pg", "DBQR");
        form.put("bankcode", "UPIDBQR");
        form.put("txn_s2s_flow", "4");
        form.put("s2s_client_ip", blank(clientIp, "127.0.0.1"));
        form.put("s2s_device_info", blank(deviceInfo, "Ayurvedaa-POS"));
        form.put("expiry_time", "1800");
        form.put("udf1", blank(udf1, ""));
        form.put("udf2", blank(udf2, ""));
        form.put("udf3", blank(udf3, ""));
        form.put("udf4", blank(udf4, ""));
        form.put("udf5", blank(udf5, ""));
        form.put("hash", hash);

        String body = encodeForm(form);
        String url = credentials.resolvedPaymentUrl();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(45))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("PayU DBQR HTTP {} txnid={} amount={}", response.statusCode(), txnId, amountStr);
            return parse(response.body(), amountStr);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("PayU DBQR call failed: {}", ex.getMessage(), ex);
            throw new BadRequestException(PaymentMessages.UPI_QR_GATEWAY_FAILED + ex.getMessage());
        }
    }

    private Result parse(String rawBody, String amountStr) {
        if (rawBody == null || rawBody.isBlank()) {
            throw new BadRequestException(PaymentMessages.UPI_QR_GATEWAY_FAILED + "empty response");
        }
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            String qrString = firstNonBlank(
                    text(root.path("result"), "qrString"),
                    text(root.path("result"), "qr_string"),
                    text(root, "qrString"));
            if (qrString.isBlank()) {
                String msg = firstNonBlank(
                        text(root.path("metaData"), "message"),
                        text(root, "message"),
                        text(root, "error"),
                        rawBody);
                throw new BadRequestException(PaymentMessages.UPI_QR_GATEWAY_FAILED + msg);
            }
            qrString = qrString.replace("\n", "").replace("\r", "").trim();
            if (!qrString.startsWith("upi://")) {
                throw new BadRequestException(PaymentMessages.UPI_QR_GATEWAY_FAILED + "invalid qrString");
            }
            String paymentId = text(root.path("result"), "paymentId");
            return new Result(qrString, paymentId, amountStr, rawBody);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BadRequestException(PaymentMessages.UPI_QR_GATEWAY_FAILED + rawBody);
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
                    .append(URLEncoder.encode(e.getValue() == null ? "" : e.getValue(), StandardCharsets.UTF_8));
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

    private static String blank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    public record Result(String qrString, String payuPaymentId, String amount, String rawBody) {
    }
}
