package com.ayurveda.appointment.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSummaryResponse {

    private UUID id;
    private String name;
    private String specialization;
    private String status;
    private BigDecimal consultationFees;
    private BigDecimal followUpFees;
    private String availability;

    /** Compatibility helper for older call sites. */
    public String getDoctorName() {
        return name;
    }

}
