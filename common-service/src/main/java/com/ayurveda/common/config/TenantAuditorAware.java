package com.ayurveda.common.config;

import com.ayurveda.common.tenant.TenantContext;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TenantAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(TenantContext.getUserCode());
    }

}
