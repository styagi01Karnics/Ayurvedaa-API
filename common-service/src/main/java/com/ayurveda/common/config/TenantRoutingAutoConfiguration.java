package com.ayurveda.common.config;

import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import com.ayurveda.common.tenant.JwtClaimParser;
import com.ayurveda.common.tenant.JwtTenantProperties;
import com.ayurveda.common.tenant.SchemaMultiTenantConnectionProvider;
import com.ayurveda.common.tenant.TenantAwareDataSource;
import com.ayurveda.common.tenant.TenantIdentifierResolver;
import com.ayurveda.common.tenant.TenantRoutingProperties;
import com.ayurveda.common.tenant.TenantSchemaFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfiguration
@ConditionalOnProperty(prefix = "ayurveda.tenant.routing", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({TenantRoutingProperties.class, JwtTenantProperties.class})
public class TenantRoutingAutoConfiguration {

    @Bean
    public JwtClaimParser jwtClaimParser(JwtTenantProperties jwtTenantProperties) {
        return new JwtClaimParser(jwtTenantProperties.getSecret());
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public FilterRegistrationBean<TenantSchemaFilter> tenantSchemaFilter(
            JwtClaimParser jwtClaimParser,
            TenantRoutingProperties routingProperties,
            ObjectMapper objectMapper) {
        FilterRegistrationBean<TenantSchemaFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TenantSchemaFilter(jwtClaimParser, routingProperties, objectMapper));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        registration.setName("tenantSchemaFilter");
        return registration;
    }

    /**
     * Hibernate SCHEMA multi-tenancy so SQL is bound to {@code hosp_*} (not {@code public.mst_*}).
     */
    @Bean
    public TenantIdentifierResolver tenantIdentifierResolver() {
        return new TenantIdentifierResolver();
    }

    @Bean
    public SchemaMultiTenantConnectionProvider schemaMultiTenantConnectionProvider(DataSource dataSource) {
        return new SchemaMultiTenantConnectionProvider(dataSource);
    }

    /**
     * Clear Hibernate {@code default_schema} so SQL is not frozen as {@code public.*},
     * and register SCHEMA multi-tenancy.
     */
    @Bean
    public HibernatePropertiesCustomizer tenantHibernateSchemaCustomizer(
            SchemaMultiTenantConnectionProvider connectionProvider,
            CurrentTenantIdentifierResolver<String> tenantIdentifierResolver) {
        return (Map<String, Object> hibernateProperties) -> {
            hibernateProperties.remove("hibernate.default_schema");
            hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
            hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
        };
    }

    /**
     * Wrap the primary DataSource so each checkout applies {@code SET search_path}.
     */
    @Bean
    public static BeanPostProcessor tenantAwareDataSourcePostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if ("dataSource".equals(beanName)
                        && bean instanceof DataSource dataSource
                        && !(bean instanceof TenantAwareDataSource)) {
                    return new TenantAwareDataSource(dataSource);
                }
                return bean;
            }
        };
    }

}
