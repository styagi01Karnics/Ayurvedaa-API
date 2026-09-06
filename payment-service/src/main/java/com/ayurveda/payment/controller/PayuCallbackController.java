package com.ayurveda.payment.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payments / PayU Callback", description = "PayU browser redirects (no JWT)")
@RestController
@RequestMapping("/api/v1/payments/payu")
@RequiredArgsConstructor
@SecurityRequirements
public class PayuCallbackController {

    private final PaymentService paymentService;

    @Operation(summary = "PayU success URL (surl)")
    @RequestMapping(value = "/success", method = {RequestMethod.POST, RequestMethod.GET})
    public void success(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        writeCallback(paymentService.handlePayuCallback(params, true), response);
    }

    @Operation(summary = "PayU failure URL (furl)")
    @RequestMapping(value = "/failure", method = {RequestMethod.POST, RequestMethod.GET})
    public void failure(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        writeCallback(paymentService.handlePayuCallback(params, false), response);
    }

    private void writeCallback(String body, HttpServletResponse response) throws IOException {
        if (body != null && body.startsWith("REDIRECT:")) {
            response.sendRedirect(body.substring("REDIRECT:".length()));
            return;
        }
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body == null ? "" : body);
    }
}
