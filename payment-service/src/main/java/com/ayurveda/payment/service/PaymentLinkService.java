package com.ayurveda.payment.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;
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
import com.ayurveda.payment.enums.PaymentLinkStatus;
import com.ayurveda.payment.repository.PaymentLinkRepository;

import feign.FeignException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentLinkService {

    /** Active window after create / share. */
    private static final int LINK_TTL_MINUTES = 15;
    private static final List<String> ACTIVE_STATUSES =
            List.of(PaymentLinkStatus.SHARED, PaymentLinkStatus.OPEN);
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
        expireOverdueLinks();
        assertAmountWithinLeft(request.getInvoiceId(), request.getAmount());

        PaymentLink existing = paymentLinkRepository
                .findFirstByInvoiceIdAndStatusInAndDeletedFalseOrderByCreatedAtDesc(
                        request.getInvoiceId(), ACTIVE_STATUSES)
                .filter(link -> !isExpired(link)
                        && link.getAmount().compareTo(request.getAmount()) == 0)
                .orElse(null);

        PaymentLink saved;
        if (existing != null) {
            existing.setExpiresAt(LocalDateTime.now().plusMinutes(LINK_TTL_MINUTES));
            saved = paymentLinkRepository.save(existing);
        } else {
            paymentLinkRepository
                    .findByInvoiceIdAndStatusInAndDeletedFalse(request.getInvoiceId(), ACTIVE_STATUSES)
                    .forEach(open -> open.setStatus(PaymentLinkStatus.SUPERSEDED));

            String schema = TenantContext.getSchemaName();
            String token = schema + "." + UUID.randomUUID().toString().replace("-", "");
            LocalDateTime now = LocalDateTime.now();

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
                    .expiresAt(now.plusMinutes(LINK_TTL_MINUTES))
                    .status(PaymentLinkStatus.SHARED)
                    .sharedAt(Boolean.TRUE.equals(request.getSendEmail()) ? now : null)
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
            if (saved.getSharedAt() == null) {
                saved.setSharedAt(LocalDateTime.now());
                saved = paymentLinkRepository.save(saved);
            }
        }

        boolean hasUpi = saved.getUpiQrPayload() != null && !saved.getUpiQrPayload().isBlank();
        String message = emailed
                ? PaymentMessages.PAYMENT_LINK_EMAILED
                : (hasUpi ? PaymentMessages.UPI_QR_CREATED : PaymentMessages.PAYMENT_LINK_CREATED);
        return ApiResponse.success(message, toResponse(saved, request.getPayPageBaseUrl(), emailed));
    }

    @Transactional
    public ApiResponse<PagedResponse<PaymentLinkResponse>> list(
            int page, int size, String status, UUID patientId, UUID invoiceId, String search) {
        requireHospitalSchema();
        expireOverdueLinks();

        String statusFilter = normalizeListStatus(status);
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : Math.min(size, 100);
        PageRequest pageable = PageRequest.of(safePage, safeSize);

        String searchTerm = search != null && !search.isBlank() ? search.trim() : null;
        Specification<PaymentLink> spec = buildListSpec(statusFilter, patientId, invoiceId, searchTerm);
        Page<PaymentLink> result = paymentLinkRepository.findAll(spec, pageable);

        Page<PaymentLinkResponse> mapped = result.map(link -> toResponse(link, null, link.getSharedAt() != null));
        return ApiResponse.success(PaymentMessages.PAYMENT_LINKS_FETCHED, PagedResponse.of(mapped));
    }

    private static Specification<PaymentLink> buildListSpec(
            String statusFilter, UUID patientId, UUID invoiceId, String searchTerm) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("deleted")));
            if (statusFilter != null) {
                if (PaymentLinkStatus.SHARED.equals(statusFilter)) {
                    predicates.add(root.get("status").in(PaymentLinkStatus.SHARED, PaymentLinkStatus.OPEN));
                } else {
                    predicates.add(cb.equal(cb.upper(root.get("status")), statusFilter));
                }
            }
            if (patientId != null) {
                predicates.add(cb.equal(root.get("patientId"), patientId));
            }
            if (invoiceId != null) {
                predicates.add(cb.equal(root.get("invoiceId"), invoiceId));
            }
            if (searchTerm != null) {
                String pattern = "%" + searchTerm.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("phone"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("invoiceNumber"), "")), pattern)));
            }
            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Transactional
    public ApiResponse<PaymentLinkResponse> getById(UUID id) {
        requireHospitalSchema();
        expireOverdueLinks();
        PaymentLink link = paymentLinkRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_LINK_NOT_FOUND));
        return ApiResponse.success(
                PaymentMessages.PAYMENT_LINK_FETCHED,
                toResponse(link, null, link.getSharedAt() != null));
    }

    @Transactional
    public ApiResponse<PaymentLinkResponse> getOpenByInvoice(UUID invoiceId) {
        requireHospitalSchema();
        expireOverdueLinks();
        PaymentLink link = paymentLinkRepository
                .findFirstByInvoiceIdAndStatusInAndDeletedFalseOrderByCreatedAtDesc(
                        invoiceId, ACTIVE_STATUSES)
                .filter(open -> !isExpired(open))
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_LINK_NOT_FOUND));
        return ApiResponse.success(
                PaymentMessages.PAYMENT_LINK_FETCHED,
                toResponse(link, null, link.getSharedAt() != null));
    }

    @Transactional
    public ApiResponse<PaymentLinkResponse> email(UUID id, String overrideEmail) {
        requireHospitalSchema();
        expireOverdueLinks();
        PaymentLink link = paymentLinkRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_LINK_NOT_FOUND));
        assertUsable(link);
        String to = overrideEmail != null && !overrideEmail.isBlank() ? overrideEmail.trim() : link.getEmail();
        emailLink(link, null, to);
        if (link.getSharedAt() == null) {
            link.setSharedAt(LocalDateTime.now());
        }
        link.setStatus(PaymentLinkStatus.SHARED);
        paymentLinkRepository.save(link);
        return ApiResponse.success(PaymentMessages.PAYMENT_LINK_EMAILED, toResponse(link, null, true));
    }

    @Transactional
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
        expireOverdueLinks();
        PaymentLink link = paymentLinkRepository.findByTokenAndDeletedFalse(token)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMessages.PAYMENT_LINK_NOT_FOUND));
        assertUsable(link);
        return link;
    }

    private void assertUsable(PaymentLink link) {
        if (PaymentLinkStatus.PAID.equalsIgnoreCase(blank(link.getStatus()))) {
            throw new BadRequestException(PaymentMessages.PAYMENT_LINK_ALREADY_PAID);
        }
        if (PaymentLinkStatus.EXPIRED.equalsIgnoreCase(blank(link.getStatus())) || isExpired(link)) {
            if (PaymentLinkStatus.isActiveUnpaid(link.getStatus())) {
                link.setStatus(PaymentLinkStatus.EXPIRED);
                paymentLinkRepository.save(link);
            }
            throw new BadRequestException(PaymentMessages.PAYMENT_LINK_EXPIRED);
        }
        if (!PaymentLinkStatus.isActiveUnpaid(link.getStatus())) {
            throw new BadRequestException(PaymentMessages.PAYMENT_LINK_INVALID);
        }
    }

    @Transactional
    public void expireOverdueLinks() {
        List<PaymentLink> overdue = paymentLinkRepository.findByStatusInAndExpiresAtBeforeAndDeletedFalse(
                ACTIVE_STATUSES, LocalDateTime.now());
        if (overdue.isEmpty()) {
            return;
        }
        overdue.forEach(link -> link.setStatus(PaymentLinkStatus.EXPIRED));
        paymentLinkRepository.saveAll(overdue);
        log.debug("Marked {} payment link(s) EXPIRED", overdue.size());
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

                This link is valid for 15 minutes and expires on %s.

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
        String effective = effectiveStatus(link);
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
                .createdAt(link.getCreatedAt())
                .sharedAt(link.getSharedAt())
                .expiresAt(link.getExpiresAt())
                .status(effective)
                .emailSent(emailSent || link.getSharedAt() != null)
                .upiQr(upiQr)
                .secondsRemaining(secondsRemaining(link, effective))
                .build();
    }

    private static String effectiveStatus(PaymentLink link) {
        String raw = blank(link.getStatus()).toUpperCase();
        if (PaymentLinkStatus.PAID.equals(raw)) {
            return PaymentLinkStatus.PAID;
        }
        if (PaymentLinkStatus.SUPERSEDED.equals(raw)) {
            return PaymentLinkStatus.SUPERSEDED;
        }
        if (PaymentLinkStatus.EXPIRED.equals(raw) || isExpired(link)) {
            return PaymentLinkStatus.EXPIRED;
        }
        if (PaymentLinkStatus.isActiveUnpaid(raw)) {
            return PaymentLinkStatus.SHARED;
        }
        return raw.isBlank() ? PaymentLinkStatus.SHARED : raw;
    }

    private static long secondsRemaining(PaymentLink link, String effectiveStatus) {
        if (!PaymentLinkStatus.SHARED.equals(effectiveStatus) || link.getExpiresAt() == null) {
            return 0L;
        }
        long seconds = Duration.between(LocalDateTime.now(), link.getExpiresAt()).getSeconds();
        return Math.max(0L, seconds);
    }

    private static String normalizeListStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String s = status.trim().toUpperCase();
        if (PaymentLinkStatus.OPEN.equals(s)) {
            return PaymentLinkStatus.SHARED;
        }
        return s;
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
        return link.getExpiresAt() != null && !link.getExpiresAt().isAfter(LocalDateTime.now());
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

    private static String blank(String value) {
        return value == null ? "" : value.trim();
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
