package com.ayurveda.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import com.ayurveda.common.security.HttpsEnforcementFilter;
import com.ayurveda.common.security.HttpsEnforcementProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfiguration
@EnableConfigurationProperties(HttpsEnforcementProperties.class)
public class HttpsEnforcementAutoConfiguration {

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(prefix = "ayurveda.security", name = "require-https", havingValue = "true")
    public FilterRegistrationBean<HttpsEnforcementFilter> httpsEnforcementFilter(ObjectMapper objectMapper) {
        FilterRegistrationBean<HttpsEnforcementFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new HttpsEnforcementFilter(objectMapper));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 5);
        registration.setName("httpsEnforcementFilter");
        return registration;
    }
}
