package com.ayurveda.common.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Caps JDBC pools in the JAR so deploys without docker-compose env vars cannot
 * open Hikari's default 10 connections per service (12 services × 10 = 120).
 */
@AutoConfiguration(before = DataSourceAutoConfiguration.class)
@ConditionalOnClass(HikariDataSource.class)
public class HikariPoolAutoConfiguration {

    static final int HIKARI_DEFAULT_MAX_POOL = 10;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static BeanPostProcessor ayurvedaHikariPoolPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof HikariDataSource hikari) {
                    applyDefaults(hikari);
                }
                return bean;
            }
        };
    }

    static void applyDefaults(HikariDataSource hikari) {
        if (hikari.getMaximumPoolSize() == HIKARI_DEFAULT_MAX_POOL) {
            hikari.setMaximumPoolSize(5);
        }
        if (hikari.getMinimumIdle() < 0 || hikari.getMinimumIdle() == HIKARI_DEFAULT_MAX_POOL) {
            hikari.setMinimumIdle(1);
        }
        if (hikari.getLeakDetectionThreshold() == 0) {
            hikari.setLeakDetectionThreshold(30_000);
        }
    }
}
