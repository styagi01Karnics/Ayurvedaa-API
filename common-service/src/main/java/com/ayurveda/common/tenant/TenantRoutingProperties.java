package com.ayurveda.common.tenant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "ayurveda.tenant.routing")
public class TenantRoutingProperties {

    /**
     * When true, registers JWT tenant filter + search_path DataSource wrapper.
     */
    private boolean enabled = false;

    /**
     * When true, clinical APIs reject JWTs whose {@code schemaName} is missing or {@code public}.
     */
    private boolean requireHospitalSchema = true;

    /**
     * Extra Ant-style paths that skip JWT / schema enforcement (in addition to actuator/swagger/ADMS).
     */
    private List<String> skipPathPatterns = new ArrayList<>();

}
