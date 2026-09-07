package com.ayurveda.payment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendPaymentLinkEmailRequest {

    /** Optional override. Defaults to the email stored on the payment link. */
    @Email
    @Size(max = 150)
    private String email;
}
