package com.ayurveda.payment.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.payment.dto.response.PaymentLinkResponse;
import com.ayurveda.payment.dto.response.PaymentResponse;
import com.ayurveda.payment.service.PaymentLinkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/** Patient-facing PayU checkout page. No login. */
@Tag(name = "Payments / Pay page")
@RestController
@RequiredArgsConstructor
@SecurityRequirements
public class PublicPayPageController {

    private final PaymentLinkService paymentLinkService;

    @Operation(summary = "Open payment link in the browser (no login)")
    @GetMapping(value = "/pay/{token:.+}", produces = MediaType.TEXT_HTML_VALUE)
    public void view(@PathVariable String token, HttpServletResponse response) throws IOException {
        paymentLinkService.bindTenantFromToken(token);
        try {
            PaymentLinkResponse link = paymentLinkService.getPublic(token, null).getData();
            writeHtml(response, payPage(link, token));
        } catch (Exception ex) {
            writeHtml(response, errorPage(ex.getMessage()));
        } finally {
            TenantContext.clear();
        }
    }

    @Operation(summary = "Start PayU hosted checkout from the pay page (no login)")
    @PostMapping(value = "/pay/{token:.+}", produces = MediaType.TEXT_HTML_VALUE)
    public void pay(@PathVariable String token, HttpServletResponse response) throws IOException {
        paymentLinkService.bindTenantFromToken(token);
        try {
            PaymentResponse payment = paymentLinkService.initiatePublic(token, null, null).getData();
            writeHtml(response, payuAutoSubmit(payment));
        } catch (Exception ex) {
            writeHtml(response, errorPage(ex.getMessage()));
        } finally {
            TenantContext.clear();
        }
    }

    private static void writeHtml(HttpServletResponse response, String body) throws IOException {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);
    }

    private static String payPage(PaymentLinkResponse link, String token) {
        String invoice = link.getInvoiceNumber() != null ? link.getInvoiceNumber() : "";
        String amount = link.getAmount() != null ? link.getAmount().toPlainString() : "";
        return """
                <!DOCTYPE html>
                <html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Pay invoice %s</title></head>
                <body style="font-family:sans-serif;max-width:420px;margin:40px auto;padding:0 16px">
                <h2>Pay your bill</h2>
                <p>Hello %s,</p>
                <p>Invoice <strong>%s</strong></p>
                <p style="font-size:28px;margin:24px 0">INR %s</p>
                <form method="post" action="/pay/%s">
                  <button type="submit" style="width:100%%;padding:14px;font-size:16px;background:#0f7b4c;color:#fff;border:0;border-radius:8px">
                    Pay with PayU
                  </button>
                </form>
                <p style="color:#666;margin-top:24px;font-size:13px">You will be redirected to PayU to complete payment.</p>
                </body></html>
                """.formatted(
                esc(invoice),
                esc(link.getFirstName()),
                esc(invoice),
                esc(amount),
                esc(token));
    }

    private static String payuAutoSubmit(PaymentResponse payment) {
        StringBuilder inputs = new StringBuilder();
        if (payment.getPayuParams() != null) {
            for (Map.Entry<String, String> entry : payment.getPayuParams().entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                inputs.append("<input type=\"hidden\" name=\"")
                        .append(esc(entry.getKey()))
                        .append("\" value=\"")
                        .append(esc(entry.getValue()))
                        .append("\">\n");
            }
        }
        return """
                <!DOCTYPE html>
                <html><head><meta charset="UTF-8"><title>Redirecting to PayU</title></head>
                <body style="font-family:sans-serif;padding:40px;text-align:center">
                <p>Redirecting to PayU…</p>
                <form id="payu" method="post" action="%s">
                %s
                </form>
                <script>document.getElementById('payu').submit();</script>
                </body></html>
                """.formatted(esc(payment.getActionUrl()), inputs);
    }

    private static String errorPage(String message) {
        String text = message == null || message.isBlank() ? "This payment link is not available." : message;
        return """
                <!DOCTYPE html>
                <html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Payment link</title></head>
                <body style="font-family:sans-serif;padding:40px;text-align:center">
                <h2 style="color:#b42318">Unable to open payment</h2>
                <p>%s</p>
                </body></html>
                """.formatted(esc(text));
    }

    private static String esc(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
