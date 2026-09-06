package com.ayurveda.payment.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ayurveda.payment.config.PayuProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PayuHashService {

    private final PayuProperties payuProperties;

    /**
     * Request hash: sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||SALT)
     */
    public String requestHash(
            String txnId,
            String amount,
            String productInfo,
            String firstName,
            String email,
            String udf1,
            String udf2,
            String udf3,
            String udf4,
            String udf5) {

        String raw = String.join("|",
                blankToEmpty(payuProperties.getMerchantKey()),
                blankToEmpty(txnId),
                blankToEmpty(amount),
                blankToEmpty(productInfo),
                blankToEmpty(firstName),
                blankToEmpty(email),
                blankToEmpty(udf1),
                blankToEmpty(udf2),
                blankToEmpty(udf3),
                blankToEmpty(udf4),
                blankToEmpty(udf5),
                "",
                "",
                "",
                "",
                "",
                blankToEmpty(payuProperties.getMerchantSalt()));
        return sha512(raw);
    }

    /**
     * Response hash:
     * sha512(SALT|status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|amount|txnid|key)
     */
    public boolean verifyResponse(Map<String, String> params) {
        String received = blankToEmpty(first(params, "hash"));
        if (received.isBlank()) {
            return false;
        }
        String raw = String.join("|",
                blankToEmpty(payuProperties.getMerchantSalt()),
                blankToEmpty(first(params, "status")),
                "",
                "",
                "",
                "",
                "",
                blankToEmpty(first(params, "udf5")),
                blankToEmpty(first(params, "udf4")),
                blankToEmpty(first(params, "udf3")),
                blankToEmpty(first(params, "udf2")),
                blankToEmpty(first(params, "udf1")),
                blankToEmpty(first(params, "email")),
                blankToEmpty(first(params, "firstname")),
                blankToEmpty(first(params, "productinfo")),
                blankToEmpty(first(params, "amount")),
                blankToEmpty(first(params, "txnid")),
                blankToEmpty(first(params, "key")));
        return received.equalsIgnoreCase(sha512(raw));
    }

    public static String formatAmount(BigDecimal amount) {
        return amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    public static String sha512(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed).toLowerCase();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-512 is not available", ex);
        }
    }

    private static String first(Map<String, String> params, String key) {
        if (params == null) {
            return "";
        }
        String value = params.get(key);
        if (value != null) {
            return value;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return "";
    }

    private static String blankToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
