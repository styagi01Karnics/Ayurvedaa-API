package com.ayurveda.payment.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.kafka.PaymentEvent;
import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.common.tenant.TenantSchemaNames;
import com.ayurveda.common.util.BusinessCodeGenerator;
import com.ayurveda.common.util.BusinessCodeTypes;
import com.ayurveda.payment.config.PayuCredentials;
import com.ayurveda.payment.config.PayuProperties;
import com.ayurveda.payment.constant.PaymentMessages;
import com.ayurveda.payment.dto.request.InitiatePaymentRequest;
import com.ayurveda.payment.dto.request.RefundPaymentRequest;
import com.ayurveda.payment.dto.response.PaymentResponse;
import com.ayurveda.payment.entity.PaymentTransaction;
import com.ayurveda.payment.enums.PaymentGateway;
import com.ayurveda.payment.enums.PaymentStatus;
import com.ayurveda.payment.kafka.PaymentEventPublisher;
import com.ayurveda.payment.mapper.PaymentMapper;
import com.ayurveda.payment.repository.PaymentRepository;
import com.ayurveda.payment.service.PaymentFailureEmailService;
import com.ayurveda.payment.service.PaymentSuccessEmailService;
import com.ayurveda.payment.service.PaymentRefundEmailService;
import com.ayurveda.payment.service.PaymentService;
import com.ayurveda.payment.service.PayuDynamicQrClient;
import com.ayurveda.payment.service.PayuGatewayResolver;
import com.ayurveda.payment.service.PayuHashService;
import com.ayurveda.payment.service.PayuRefundClient;
import com.ayurveda.payment.service.UpiQrInitResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PayuHashService payuHashService;
    private final PayuProperties payuProperties;
    private final PayuGatewayResolver payuGatewayResolver;
    private final PayuCallbackProcessor payuCallbackProcessor;
    private final ObjectProvider<PaymentEventPublisher> paymentEventPublisher;
    private final PaymentFailureEmailService paymentFailureEmailService;
    private final PaymentSuccessEmailService paymentSuccessEmailService;
    private final PayuRefundClient payuRefundClient;
    private final PaymentRefundEmailService paymentRefundEmailService;
    private final PayuDynamicQrClient payuDynamicQrClient;

    @Override
    @Transactional
    public ApiResponse<PaymentResponse> initiate(InitiatePaymentRequest request) {
        String schemaName = TenantContext.getSchemaName();
        String tenantCode = TenantContext.getTenantCode();
        PayuCredentials payu = payuGatewayResolver.requireForTenant(tenantCode);

        String productInfo = blankToDefault(request.getProductInfo(), "Hospital payment");
        String paymentCode = BusinessCodeGenerator.next(
                BusinessCodeTypes.PAYMENT,
                paymentRepository.findCodesByPrefix(BusinessCodeGenerator.prefix(BusinessCodeTypes.PAYMENT)));
        String txnId = toPayuTxnId(paymentCode);
        String amount = PayuHashService.formatAmount(request.getAmount());

        String tenantId = TenantContext.getTenantId() != null ? TenantContext.getTenantId().toString() : "";
        String invoiceUdf = request.getInvoiceId() != null ? request.getInvoiceId().toString() : "";

        PaymentTransaction payment = PaymentTransaction.builder()
                .paymentCode(paymentCode)
                .payuTxnId(txnId)
                .amount(request.getAmount())
                .productInfo(productInfo)
                .firstName(request.getFirstName().trim())
                .email(request.getEmail().trim())
                .phone(request.getPhone().trim())
                .invoiceId(request.getInvoiceId())
                .patientId(request.getPatientId())
                .gateway(PaymentGateway.PAYU)
                .status(PaymentStatus.INITIATED)
                .schemaName(schemaName)
                .tenantCode(tenantCode)
                .frontendSuccessUrl(trimToNull(request.getFrontendSuccessUrl()))
                .frontendFailureUrl(trimToNull(request.getFrontendFailureUrl()))
                .initiatedByUserId(TenantContext.getUserId())
                .build();

        PaymentTransaction saved = paymentRepository.save(payment);

        String udf5 = saved.getId().toString();
        String hash = payuHashService.requestHash(
                payu.getMerchantKey(),
                payu.getMerchantSalt(),
                txnId,
                amount,
                productInfo,
                saved.getFirstName(),
                saved.getEmail(),
                blankToEmpty(schemaName),
                blankToEmpty(tenantCode),
                invoiceUdf,
                tenantId,
                udf5);
        saved.setRequestHash(hash);

        Map<String, String> payuParams = new LinkedHashMap<>();
        payuParams.put("key", payu.getMerchantKey());
        payuParams.put("txnid", txnId);
        payuParams.put("amount", amount);
        payuParams.put("productinfo", productInfo);
        payuParams.put("firstname", saved.getFirstName());
        payuParams.put("email", saved.getEmail());
        payuParams.put("phone", saved.getPhone());
        payuParams.put("surl", payuProperties.getSuccessUrl());
        payuParams.put("furl", payuProperties.getFailureUrl());
        payuParams.put("udf1", blankToEmpty(schemaName));
        payuParams.put("udf2", blankToEmpty(tenantCode));
        payuParams.put("udf3", invoiceUdf);
        payuParams.put("udf4", tenantId);
        payuParams.put("udf5", udf5);
        payuParams.put("hash", hash);

        log.info(
                "PayU payment initiated. paymentId={}, txnid={}, amount={}, tenant={}, legacyFallback={}",
                saved.getId(),
                txnId,
                amount,
                tenantCode,
                payu.isLegacyFallback());
        return ApiResponse.success(
                PaymentMessages.PAYMENT_INITIATED,
                paymentMapper.toResponse(saved, payu.resolvedPaymentUrl(), payuParams));
    }

    @Override
    @Transactional
    public ApiResponse<UpiQrInitResult> initiateUpiQr(InitiatePaymentRequest request, String clientIp) {
        ApiResponse<PaymentResponse> initiated = initiate(request);
        PaymentResponse payment = initiated.getData();
        PaymentTransaction saved = requireById(payment.getId());
        PayuCredentials payu = payuGatewayResolver.requireForTenant(TenantContext.getTenantCode());

        String schemaName = blankToEmpty(TenantContext.getSchemaName());
        String tenantCode = blankToEmpty(TenantContext.getTenantCode());
        String tenantId = TenantContext.getTenantId() != null ? TenantContext.getTenantId().toString() : "";
        String invoiceUdf = request.getInvoiceId() != null ? request.getInvoiceId().toString() : "";
        String udf5 = saved.getId().toString();

        PayuDynamicQrClient.Result qr = payuDynamicQrClient.createUpiQr(
                payu,
                saved.getPayuTxnId(),
                saved.getAmount(),
                saved.getProductInfo(),
                saved.getFirstName(),
                saved.getEmail(),
                saved.getPhone(),
                schemaName,
                tenantCode,
                invoiceUdf,
                tenantId,
                udf5,
                saved.getRequestHash(),
                clientIp,
                "Ayurvedaa-POS-QR");

        saved.setStatus(PaymentStatus.PENDING);
        saved.setPaymentMode("UPI_QR");
        paymentRepository.save(saved);

        log.info(
                "PayU UPI QR created. paymentId={} txnid={} payuPaymentId={}",
                saved.getId(),
                saved.getPayuTxnId(),
                qr.payuPaymentId());

        return ApiResponse.success(
                PaymentMessages.UPI_QR_CREATED,
                UpiQrInitResult.builder()
                        .payment(paymentMapper.toResponse(saved))
                        .qrPayload(qr.qrString())
                        .payuPaymentId(qr.payuPaymentId())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PaymentResponse>> list(UUID invoiceId) {
        List<PaymentTransaction> payments = invoiceId == null
                ? paymentRepository.findByDeletedFalseOrderByCreatedAtDesc()
                : paymentRepository.findByInvoiceIdAndDeletedFalseOrderByCreatedAtDesc(invoiceId);
        return ApiResponse.success(
                PaymentMessages.PAYMENTS_FETCHED,
                payments.stream().map(paymentMapper::toResponse).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PaymentResponse> getById(UUID paymentId) {
        return ApiResponse.success(
                PaymentMessages.PAYMENT_FETCHED,
                paymentMapper.toResponse(requireById(paymentId)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PaymentResponse> getByTxnId(String txnId) {
        PaymentTransaction payment = paymentRepository.findByPayuTxnIdAndDeletedFalse(txnId)
                .or(() -> paymentRepository.findByPaymentCodeAndDeletedFalse(txnId))
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_NOT_FOUND_WITH_TXN + txnId));
        return ApiResponse.success(PaymentMessages.PAYMENT_FETCHED, paymentMapper.toResponse(payment));
    }

    @Override
    @Transactional
    public ApiResponse<PaymentResponse> refund(UUID paymentId, RefundPaymentRequest request) {
        PaymentTransaction payment = requireById(paymentId);
        PaymentStatus status = payment.getStatus();
        if (status != PaymentStatus.SUCCESS && status != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new BadRequestException(PaymentMessages.REFUND_NOT_ALLOWED);
        }
        if (!StringUtils.hasText(payment.getMihpayid())) {
            throw new BadRequestException(PaymentMessages.REFUND_REQUIRES_MIHPAYID);
        }

        BigDecimal alreadyRefunded = payment.getRefundedAmount() != null
                ? payment.getRefundedAmount()
                : BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal remaining = payment.getAmount()
                .subtract(alreadyRefunded)
                .setScale(2, java.math.RoundingMode.HALF_UP);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(PaymentMessages.NOTHING_TO_REFUND);
        }

        BigDecimal refundAmount = request != null && request.getAmount() != null
                ? request.getAmount().setScale(2, java.math.RoundingMode.HALF_UP)
                : remaining;
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(PaymentMessages.NOTHING_TO_REFUND);
        }
        if (refundAmount.compareTo(remaining) > 0) {
            throw new BadRequestException(
                    PaymentMessages.REFUND_EXCEEDS_AMOUNT + remaining.toPlainString());
        }

        PayuCredentials payu = payuGatewayResolver.requireForTenant(payment.getTenantCode());
        String refundToken = UUID.randomUUID().toString().replace("-", "");
        PayuRefundClient.Result result = payuRefundClient.refund(
                payu, payment.getMihpayid(), refundToken, refundAmount);

        BigDecimal newRefunded = alreadyRefunded.add(refundAmount);
        payment.setRefundedAmount(newRefunded);
        payment.setLastRefundToken(refundToken);
        payment.setLastRefundRequestId(StringUtils.hasText(result.requestId())
                ? result.requestId()
                : refundToken);
        payment.setStatus(newRefunded.compareTo(payment.getAmount()) >= 0
                ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIALLY_REFUNDED);

        PaymentTransaction saved = paymentRepository.save(payment);

        PaymentEvent event = toEvent(saved);
        event.setAmount(refundAmount);
        event.setRefundRequestId(saved.getLastRefundRequestId());
        paymentEventPublisher.ifAvailable(publisher -> publisher.publish(event));

        paymentRefundEmailService.notifyRefund(
                saved,
                refundAmount,
                request != null ? request.getReason() : null);

        return ApiResponse.success(PaymentMessages.REFUND_INITIATED, paymentMapper.toResponse(saved));
    }

    @Override
    public String handlePayuCallback(Map<String, String> params, boolean successEndpoint) {
        String schemaName = value(params, "udf1");
        String tenantCode = value(params, "udf2");
        String tenantIdRaw = value(params, "udf4");

        PayuCredentials payu = payuGatewayResolver.findForTenant(tenantCode).orElse(null);
        if (payu == null || !payuHashService.verifyResponse(payu.getMerchantSalt(), params)) {
            log.warn("PayU callback hash mismatch. txnid={}", value(params, "txnid"));
            return htmlPage("Payment verification failed", PaymentMessages.INVALID_PAYU_HASH, false);
        }
        if (!TenantSchemaNames.isHospitalSchema(schemaName) || tenantCode.isBlank()) {
            log.warn("PayU callback missing hospital context. txnid={}", value(params, "txnid"));
            return htmlPage("Payment error", PaymentMessages.INVALID_CALLBACK_TENANT, false);
        }

        UUID tenantId = null;
        if (!tenantIdRaw.isBlank()) {
            try {
                tenantId = UUID.fromString(tenantIdRaw);
            } catch (IllegalArgumentException ignored) {
                // tenant id is optional for search_path routing
            }
        }

        TenantContext.set(tenantId, tenantCode, schemaName);
        try {
            PayuCallbackProcessor.CallbackResult result = payuCallbackProcessor.process(params, successEndpoint);
            paymentEventPublisher.ifAvailable(publisher -> publisher.publish(toEvent(result.payment())));
            paymentFailureEmailService.notifyIfNeeded(
                    result.payment(), result.previousStatus(), result.status());
            paymentSuccessEmailService.notifyIfNeeded(
                    result.payment(), result.previousStatus(), result.status());
            if (result.redirectUrl() != null) {
                return "REDIRECT:" + result.redirectUrl();
            }
            boolean ok = result.status() == PaymentStatus.SUCCESS;
            return htmlPage(
                    ok ? "Payment successful" : "Payment failed",
                    ok ? "You can close this window and return to Ayurvedaa." : "Payment was not completed.",
                    ok);
        } catch (ResourceNotFoundException ex) {
            log.warn("PayU callback payment missing. txnid={}", value(params, "txnid"));
            return htmlPage("Payment error", ex.getMessage(), false);
        } finally {
            TenantContext.clear();
        }
    }

    private PaymentTransaction requireById(UUID paymentId) {
        return paymentRepository.findByIdAndDeletedFalse(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_NOT_FOUND));
    }

    private PaymentEvent toEvent(PaymentTransaction payment) {
        if (payment == null) {
            return null;
        }
        return PaymentEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(PaymentEvent.TYPE_UPDATED)
                .occurredAt(java.time.Instant.now())
                .tenantId(TenantContext.getTenantId())
                .tenantCode(payment.getTenantCode())
                .schemaName(payment.getSchemaName())
                .paymentId(payment.getId())
                .paymentCode(payment.getPaymentCode())
                .payuTxnId(payment.getPayuTxnId())
                .invoiceId(payment.getInvoiceId())
                .patientId(payment.getPatientId())
                .initiatedByUserId(payment.getInitiatedByUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                .paymentMode(payment.getPaymentMode())
                .mihpayid(payment.getMihpayid())
                .firstName(payment.getFirstName())
                .email(payment.getEmail())
                .build();
    }

    static String toPayuTxnId(String paymentCode) {
        if (paymentCode != null && paymentCode.length() <= 25) {
            return paymentCode;
        }
        String compact = "P" + Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        return compact.length() <= 25 ? compact : compact.substring(0, 25);
    }

    private static String htmlPage(String title, String message, boolean success) {
        String color = success ? "#0f7b4c" : "#b42318";
        return """
                <!DOCTYPE html>
                <html><head><meta charset="UTF-8"><title>%s</title></head>
                <body style="font-family:sans-serif;padding:40px;text-align:center">
                <h2 style="color:%s">%s</h2>
                <p>%s</p>
                </body></html>
                """.formatted(title, color, title, message);
    }

    private static String value(Map<String, String> params, String key) {
        if (params == null || key == null) {
            return "";
        }
        String value = params.get(key);
        if (value != null) {
            return value.trim();
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key) && entry.getValue() != null) {
                return entry.getValue().trim();
            }
        }
        return "";
    }

    private static String blankToDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static String blankToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

}
