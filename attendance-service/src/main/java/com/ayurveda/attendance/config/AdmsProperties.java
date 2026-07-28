package com.ayurveda.attendance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * ADMS / iClock device settings.
 * When {@code allowed-serial-numbers} is empty, all devices are accepted.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "adms")
public class AdmsProperties {

    /**
     * Device serial numbers (SN) allowed to push data.
     * Leave empty to accept any SN (useful for initial setup).
     */
    private List<String> allowedSerialNumbers = new ArrayList<>();

}
