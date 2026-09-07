package com.ayurveda.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariDataSource;

class HikariPoolAutoConfigurationTest {

    @Test
    void capsHikariDefaultPoolOfTen() {
        try (HikariDataSource hikari = new HikariDataSource()) {
            hikari.setJdbcUrl("jdbc:postgresql://localhost:5432/ayurveda_db");
            hikari.setUsername("ayurveda");
            hikari.setPassword("secret");
            HikariPoolAutoConfiguration.applyDefaults(hikari);
            assertEquals(5, hikari.getMaximumPoolSize());
            assertEquals(1, hikari.getMinimumIdle());
            assertEquals(30_000, hikari.getLeakDetectionThreshold());
        }
    }

    @Test
    void doesNotOverrideExplicitCustomMax() {
        try (HikariDataSource hikari = new HikariDataSource()) {
            hikari.setJdbcUrl("jdbc:postgresql://localhost:5432/ayurveda_db");
            hikari.setUsername("ayurveda");
            hikari.setPassword("secret");
            hikari.setMaximumPoolSize(8);
            hikari.setMinimumIdle(2);
            HikariPoolAutoConfiguration.applyDefaults(hikari);
            assertEquals(8, hikari.getMaximumPoolSize());
            assertEquals(2, hikari.getMinimumIdle());
        }
    }
}
