package com.ayurveda.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springdoc.core.customizers.OpenApiCustomizer;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
public class OpenApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenAPI ayurvedaaOpenApi(
            @Value("${spring.application.name:ayurveda-service}") String serviceName) {

        String title = formatServiceTitle(serviceName);

        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description("Ayurvedaa API documentation for " + title)
                        .version("v1")
                        .contact(new Contact()
                                .name("Ayurvedaa")
                                .email("support@ayurvedaa.com"))
                        .license(new License()
                                .name("Proprietary")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public OpenApiCustomizer bearerAuthCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            openApi.getComponents().addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                            .name("bearerAuth")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT"));
            openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        };
    }

    private String formatServiceTitle(String serviceName) {
        if (serviceName == null || serviceName.isBlank()) {
            return "Ayurvedaa Service API";
        }
        String[] parts = serviceName.split("-");
        StringBuilder builder = new StringBuilder("Ayurvedaa ");
        for (String part : parts) {
            if (part.isBlank() || "service".equalsIgnoreCase(part)) {
                continue;
            }
            builder.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(' ');
        }
        builder.append("API");
        return builder.toString().trim();
    }

}
