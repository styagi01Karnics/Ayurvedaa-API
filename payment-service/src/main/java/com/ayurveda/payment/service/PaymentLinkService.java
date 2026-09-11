package com.ayurveda.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.notification.EmailNotificationPublisher;
import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.common.tenant.TenantSchemaNames;
import com.ayurveda.payment.client.BillingServiceClient;
import com.ayurveda.payment.config.PaymentLinkProperties;
import com.ayurveda.payment.constant.PaymentMessages;
import com.ayurveda.payment.dto.client.InvoiceClientResponse;
import com.ayurveda.payment.dto.request.CreatePaymentLinkRequest;
import com.ayurveda.payment.dto.request.InitiatePaymentRequest;
import com.ayurveda.payment.dto.response.PaymentLinkResponse;
import com.ayurveda.payment.dto.response.PaymentResponse;
import com.ayurveda.payment.entity.PaymentLink;
import com.ayurveda.payment.repository.PaymentLinkRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentLinkService {

    private static final int LINK_DAYS = 7;
    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_SUPERSEDED = "SUPERSEDED";
    private static final DateTimeFormatter EXPIRY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final PaymentLinkRepository paymentLinkRepository;
    private final PaymentService paymentService;
    private final PaymentLinkProperties paymentLinkProperties;
    private final EmailNotificationPublisher emailNotificationPublisher;
    private final BillingServiceClient billingServiceClient;

    @Transactional
    public ApiResponse<PaymentLinkResponse> create(CreatePaymentLinkRequest request) {
        requireHospitalSchema();
        assertAmountWithinLeft(request.getInvoiceId(), request.getAmount());

        PaymentLink existing = paymentLinkRepository
                .findFirstByInvoiceIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(
                        request.getInvoiceId(), STATUS_OPEN)
                .filter(link -> !isExpired(link)
                        && link.getAmount().compareTo(request.getAmount()) == 0)
                .orElse(null);

        PaymentLink saved;
        if (existing != null) {
            saved = existing;
        } else {
            paymentLinkRepository
                    .findByInvoiceIdAndStatusAndDeletedFalse(request.getInvoiceId(), STATUS_OPEN)
                    .forEach(open -> open.setStatus(STATUS_SUPERSEDED));

            String schema = TenantContext.getSchemaName();
            String token = schema + "." + UUID.randomUUID().toString().replace("-", "");

            PaymentLink link = PaymentLink.builder()
                    .token(token)
                    .invoiceId(request.getInvoiceId())
                    .patientId(request.getPatientId())
                    .amount(request.getAmount())
                    .invoiceNumber(request.getInvoiceNumber())
                    .firstName(request.getFirstName().trim())
                    .email(request.getEmail().trim())
                    .phone(request.getPhone().trim())
                    .schemaName(schema)
                    .tenantCode(TenantContext.getTenantCode())
                    .tenantId(TenantContext.getTenantId())
                    .expiresAt(LocalDateTime.now().plusDays(LINK_DAYS))
                    .status(STATUS_OPEN)
                    .build();
            saved = paymentLinkRepository.save(link);
        }

        if (Boolean.TRUE.equals(request.getUpiQr())
                && (saved.getUpiQrPayload() == null || saved.getUpiQrPayload().isBlank())) {
            attachUpiQr(saved, request);
            saved = paymentLinkRepository.save(saved);
        }

        boolean emailed = false;
        if (Boolean.TRUE.equals(request.getSendEmail())) {
            emailed = emailLink(saved, request.getPayPageBaseUrl(), saved.getEmail());
        }

        boolean hasUpi = saved.getUpiQrPayload() != null && !saved.getUpiQrPayload().isBlank();
        String message = emailed
                ? PaymentMessages.PAYMENT_LINK_EMAILED
                : (hasUpi ? PaymentMessages.UPI_QR_CREATED : PaymentMessages.PAYMENT_LINK_CREATED);
        return ApiResponse.success(message, toResponse(saved, request.getPayPageBaseUrl(), emailed));
    }

    @Transactional(readOnly = true)
    public ApiResponse<PaymentLinkResponse> getById(UUID id) {
        requireHospitalSchema();
        PaymentLink link = paymentLinkRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_LINK_NOT_FOUND));
        return ApiResponse.success(PaymentMessages.PAYMENT_LINK_FETCHED, toResponse(link, null, false));
    }

    @Transactional(readOnly = true)
    public ApiResponse<PaymentLinkResponse> getOpenByInvoice(UUID invoiceId) {
        requireHospitalSchema();
        PaymentLink link = paymentLinkRepository
                .findFirstByInvoiceIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(invoiceId, STATUS_OPEN)
                .filter(open -> !isExpired(open))
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_LINK_NOT_FOUND));
        return ApiResponse.success(PaymentMessages.PAYMENT_LINK_FETCHED, toResponse(link, null, false));
    }

    @Transactional
    public ApiResponse<PaymentLinkResponse> email(UUID id, String overrideEmail) {
        requireHospitalSchema();
        PaymentLink link = paymentLinkRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_LINK_NOT_FOUND));
        assertUsable(link);
        String to = overrideEmail != null && !overrideEmail.isBlank() ? overrideEmail.trim() : link.getEmail();
        emailLink(link, null, to);
        return ApiResponse.success(PaymentMessages.PAYMENT_LINK_EMAILED, toResponse(link, null, true));
    }

    @Transactional(readOnly = true)
    public ApiResponse<PaymentLinkResponse> getPublic(String token, String payPageBaseUrl) {
        PaymentLink link = loadPublic(token);
        return ApiResponse.success(PaymentMessages.PAYMENT_LINK_FETCHED, toResponse(link, payPageBaseUrl, false));
    }

    @Transactional
    public ApiResponse<PaymentResponse> initiatePublic(
            String token, String frontendSuccessUrl, String frontendFailureUrl) {
        PaymentLink link = loadPublic(token);
        TenantContext.set(link.getTenantId(), link.getTenantCode(), link.getSchemaName());
        InitiatePaymentRequest request = InitiatePaymentRequest.builder()
                .amount(link.getAmount())
                .productInfo("Invoice " + (link.getInvoiceNumber() != null
                        ? link.getInvoiceNumber()
                        : link.getInvoiceId()))
                .firstName(link.getFirstName())
                .email(link.getEmail())
                .phone(link.getPhone())
                .invoiceId(link.getInvoiceId())
                .patientId(link.getPatientId())
                .frontendSuccessUrl(frontendSuccessUrl)
                .frontendFailureUrl(frontendFailureUrl)
                .build();
        return paymentService.initiate(request);
    }

    public void bindTenantFromToken(String token) {
        TenantContext.set(null, null, schemaFromToken(token));
    }

    private PaymentLink loadPublic(String token) {
        if (token == null || token.isBlank() || !token.contains(".")) {
            throw new BadRequestException(PaymentMessages.PAYMENT_LINK_INVALID);
        }
        PaymentLink link = paymentLinkRepository.findByTokenAndDeletedFalse(token)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_LINK_NOT_FOUND));
        assertUsable(link);
        return link;
    }

    private void assertUsable(PaymentLink link) {
        if (STATUS_PAID.equalsIgnoreCase(link.getStatus())) {
            throw new BadRequestException(PaymentMessages.PAYMENT_LINK_ALREADY_PAID);
        }
        if (isExpired(link)) {
            throw new BadRequestException(PaymentMessages.PAYMENT_LINK_EXPIRED);
        }
        if (!STATUS_OPEN.equalsIgnoreCase(link.getStatus())) {
            throw new BadRequestException(PaymentMessages.PAYMENT_LINK_INVALID);
        }
    }

    private boolean emailLink(PaymentLink link, String payPageBaseUrl, String to) {
        PaymentLinkResponse view = toResponse(link, payPageBaseUrl, false);
        String invoiceLabel = link.getInvoiceNumber() != null
                ? link.getInvoiceNumber()
                : String.valueOf(link.getInvoiceId());
        String expiry = link.getExpiresAt() != null ? link.getExpiresAt().format(EXPIRY_FORMAT) : "";
        String body = """
                Hello %s,

                Please pay the remaining amount of INR %s for invoice %s.

                Pay here:
                %s

                This link expires on %s.

                If you already paid at the clinic, you can ignore this email.
                """.formatted(
                link.getFirstName(),
                link.getAmount().toPlainString(),
                invoiceLabel,
                view.getPayUrl(),
                expiry);
        emailNotificationPublisher.sendEmail(
                to,
                "Payment link for invoice " + invoiceLabel,
                body,
                link.getTenantCode());
        return true;
    }

    private PaymentLinkResponse toResponse(PaymentLink link, String payPageBaseUrl, boolean emailSent) {
        String payUrl = resolvePayPageBase(payPageBaseUrl) + "/pay/" + link.getToken();
        boolean upiQr = link.getUpiQrPayload() != null && !link.getUpiQrPayload().isBlank();
        return PaymentLinkResponse.builder()
                .id(link.getId())
                .token(link.getToken())
                .payUrl(payUrl)
                .qrPayload(upiQr ? link.getUpiQrPayload() : payUrl)
                .invoiceId(link.getInvoiceId())
                .invoiceNumber(link.getInvoiceNumber())
                .patientId(link.getPatientId())
                .amount(link.getAmount())
                .firstName(link.getFirstName())
                .email(link.getEmail())
                .phone(link.getPhone())
                .expiresAt(link.getExpiresAt())
                .status(link.getStatus())
                .emailSent(emailSent)
                .upiQr(upiQr)
                .build();
    }

    private void attachUpiQr(PaymentLink link, CreatePaymentLinkRequest request) {
        InitiatePaymentRequest initiate = InitiatePaymentRequest.builder()
                .amount(link.getAmount())
                .productInfo("Invoice " + (link.getInvoiceNumber() != null
                        ? link.getInvoiceNumber()
                        : link.getInvoiceId()))
                .firstName(link.getFirstName())
                .email(link.getEmail())
                .phone(link.getPhone())
                .invoiceId(link.getInvoiceId())
                .patientId(link.getPatientId())
                .build();
        UpiQrInitResult result = paymentService.initiateUpiQr(initiate, "127.0.0.1").getData();
        link.setUpiQrPayload(result.getQrPayload());
        if (result.getPayment() != null) {
            link.setPayuTxnId(result.getPayment().getPayuTxnId());
        }
    }

    private String resolvePayPageBase(String payPageBaseUrl) {
        String base = trimSlash(payPageBaseUrl);
        if (base == null || base.isBlank()) {
            base = trimSlash(paymentLinkProperties.getPayPageBaseUrl());
        }
        if (base == null || base.isBlank()) {
            return "http://45.195.229.15:8112";
        }
        return base;
    }

    private void assertAmountWithinLeft(UUID invoiceId, BigDecimal amount) {
        InvoiceClientResponse invoice = loadInvoice(invoiceId);
        BigDecimal left = invoice.getLeftAmount() != null ? invoice.getLeftAmount() : BigDecimal.ZERO;
        if (amount.compareTo(left) > 0) {
            throw new BadRequestException(
                    PaymentMessages.PAYMENT_LINK_AMOUNT_EXCEEDS_LEFT + left.toPlainString());
        }
    }

    private InvoiceClientResponse loadInvoice(UUID invoiceId) {
        try {
            ApiResponse<InvoiceClientResponse> response = billingServiceClient.getInvoiceById(invoiceId);
            if (response == null || response.getData() == null) {
                throw new ResourceNotFoundException(PaymentMessages.INVOICE_NOT_FOUND);
            }
            return response.getData();
        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(PaymentMessages.INVOICE_NOT_FOUND);
        } catch (FeignException ex) {
            log.error("Billing invoice lookup failed for {}: {}", invoiceId, ex.getMessage());
            throw new BadRequestException(PaymentMessages.INVOICE_LOOKUP_FAILED);
        }
    }

    private static void requireHospitalSchema() {
        if (!TenantSchemaNames.isHospitalSchema(TenantContext.getSchemaName())) {
            throw new BadRequestException(PaymentMessages.INVALID_CALLBACK_TENANT);
        }
    }

    private static boolean isExpired(PaymentLink link) {
        return link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private static String schemaFromToken(String token) {
        if (token == null || !token.contains(".")) {
            throw new BadRequestException(PaymentMessages.PAYMENT_LINK_INVALID);
        }
        String schema = token.substring(0, token.indexOf('.'));
        if (!TenantSchemaNames.isHospitalSchema(schema)) {
            throw new BadRequestException(PaymentMessages.PAYMENT_LINK_INVALID);
        }
        return schema;
    }

    private static String trimSlash(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
