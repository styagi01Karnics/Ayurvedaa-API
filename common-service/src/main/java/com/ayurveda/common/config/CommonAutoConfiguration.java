package com.ayurveda.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@AutoConfiguration
@EnableJpaAuditing
@ComponentScan(basePackages = "com.ayurveda.common")
public class CommonAutoConfiguration {
}
