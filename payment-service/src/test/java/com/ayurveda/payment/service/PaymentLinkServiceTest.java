package com.ayurveda.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.notification.EmailNotificationPublisher;
import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.payment.client.BillingServiceClient;
import com.ayurveda.payment.config.PaymentLinkProperties;
import com.ayurveda.payment.constant.PaymentMessages;
import com.ayurveda.payment.dto.client.InvoiceClientResponse;
import com.ayurveda.payment.dto.request.CreatePaymentLinkRequest;
import com.ayurveda.payment.dto.response.PaymentResponse;
import com.ayurveda.payment.repository.PaymentLinkRepository;

@ExtendWith(MockitoExtension.class)
class PaymentLinkServiceTest {

    @Mock
    private PaymentLinkRepository paymentLinkRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private PaymentLinkProperties paymentLinkProperties;
    @Mock
    private EmailNotificationPublisher emailNotificationPublisher;
    @Mock
    private BillingServiceClient billingServiceClient;

    @InjectMocks
    private PaymentLinkService paymentLinkService;

    private UUID invoiceId;

    @BeforeEach
    void setUp() {
        invoiceId = UUID.randomUUID();
        TenantContext.set(UUID.randomUUID(), "GAN-DL", "hosp_gan_dl");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void createRejectsAmountGreaterThanLeft() {
        when(billingServiceClient.getInvoiceById(invoiceId)).thenReturn(
                ApiResponse.success("ok", InvoiceClientResponse.builder()
                        .id(invoiceId)
                        .leftAmount(new BigDecimal("100.00"))
                        .build()));

        CreatePaymentLinkRequest request = baseRequest(new BigDecimal("150.00"));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> paymentLinkService.create(request));

        assertTrue(ex.getMessage().contains(PaymentMessages.PAYMENT_LINK_AMOUNT_EXCEEDS_LEFT));
        assertTrue(ex.getMessage().contains("100.00"));
        verify(paymentLinkRepository, never()).save(any());
    }

    @Test
    void createAllowsAmountEqualToLeft() {
        stubInvoiceLeft(new BigDecimal("75.50"));
        stubSaveNewLink();
        when(paymentLinkProperties.getPayPageBaseUrl()).thenReturn("http://127.0.0.1:8112");

        CreatePaymentLinkRequest request = baseRequest(new BigDecimal("75.50"));
        var response = paymentLinkService.create(request);

        assertEquals(PaymentMessages.PAYMENT_LINK_CREATED, response.getMessage());
        assertEquals(new BigDecimal("75.50"), response.getData().getAmount());
        assertFalse(response.getData().isUpiQr());
        assertTrue(response.getData().getQrPayload().startsWith("http://"));
        verify(paymentService, never()).initiateUpiQr(any(), anyString());
    }

    @Test
    void createWithUpiQrSetsUpiPayload() {
        stubInvoiceLeft(new BigDecimal("10.00"));
        stubSaveNewLink();
        when(paymentLinkProperties.getPayPageBaseUrl()).thenReturn("http://127.0.0.1:8112");
        when(paymentService.initiateUpiQr(any(), eq("127.0.0.1"))).thenReturn(
                ApiResponse.success(
                        PaymentMessages.UPI_QR_CREATED,
                        UpiQrInitResult.builder()
                                .payment(PaymentResponse.builder()
                                        .payuTxnId("PTESTQR10")
                                        .amount(new BigDecimal("10.00"))
                                        .build())
                                .qrPayload("upi://pay?pa=merchant@payu&am=10.00&cu=INR&tn=Test")
                                .payuPaymentId("22095")
                                .build()));

        CreatePaymentLinkRequest request = baseRequest(new BigDecimal("10.00"));
        request.setUpiQr(true);
        var response = paymentLinkService.create(request);

        assertEquals(PaymentMessages.UPI_QR_CREATED, response.getMessage());
        assertTrue(response.getData().isUpiQr());
        assertTrue(response.getData().getQrPayload().startsWith("upi://pay?"));
        assertTrue(response.getData().getQrPayload().contains("am=10.00"));
        assertTrue(response.getData().getPayUrl().contains("/pay/"));
        verify(paymentService).initiateUpiQr(any(), eq("127.0.0.1"));
    }

    private void stubInvoiceLeft(BigDecimal left) {
        when(billingServiceClient.getInvoiceById(invoiceId)).thenReturn(
                ApiResponse.success("ok", InvoiceClientResponse.builder()
                        .id(invoiceId)
                        .leftAmount(left)
                        .build()));
    }

    private void stubSaveNewLink() {
        when(paymentLinkRepository.findFirstByInvoiceIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(
                invoiceId, "OPEN")).thenReturn(java.util.Optional.empty());
        when(paymentLinkRepository.findByInvoiceIdAndStatusAndDeletedFalse(invoiceId, "OPEN"))
                .thenReturn(java.util.List.of());
        when(paymentLinkRepository.save(any())).thenAnswer(invocation -> {
            var link = invocation.getArgument(0, com.ayurveda.payment.entity.PaymentLink.class);
            if (link.getId() == null) {
                link.setId(UUID.randomUUID());
            }
            return link;
        });
    }

    private CreatePaymentLinkRequest baseRequest(BigDecimal amount) {
        CreatePaymentLinkRequest request = new CreatePaymentLinkRequest();
        request.setInvoiceId(invoiceId);
        request.setAmount(amount);
        request.setInvoiceNumber("GAN-DL-INV-00001");
        request.setFirstName("Ravi");
        request.setEmail("ravi@example.com");
        request.setPhone("9999999999");
        request.setSendEmail(false);
        return request;
    }
}
