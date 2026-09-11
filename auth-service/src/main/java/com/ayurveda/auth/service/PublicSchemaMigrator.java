package com.ayurveda.auth.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Applies ordered DDL scripts into {@code public} (platform tables Super Admin can write).
 * Scripts live under {@code classpath:db/public-schema/*.sql}.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class PublicSchemaMigrator implements ApplicationRunner {

    private static final String SCRIPT_PATTERN = "classpath:db/public-schema/*.sql";
    private static final String BOOKKEEPING_TABLE = "public.public_schema_migrations";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Public schema migration: {}", migrate());
    }

    public String migrate() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources;
        try {
            resources = resolver.getResources(SCRIPT_PATTERN);
        } catch (Exception ex) {
            log.warn("No public-schema scripts found under {}: {}", SCRIPT_PATTERN, ex.getMessage());
            return "No public-schema SQL scripts found.";
        }

        if (resources.length == 0) {
            return "No public-schema SQL scripts found.";
        }

        Arrays.sort(resources, Comparator.comparing(Resource::getFilename, Comparator.nullsLast(String::compareTo)));
        ensureMigrationBookkeeping();
        Set<String> alreadyApplied = loadAppliedScripts();

        int applied = 0;
        int skipped = 0;
        StringBuilder names = new StringBuilder();
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null || !filename.endsWith(".sql")) {
                continue;
            }
            if (alreadyApplied.contains(filename)) {
                skipped++;
                continue;
            }
            applyScript(resource);
            recordApplied(filename);
            applied++;
            if (names.length() > 0) {
                names.append(", ");
            }
            names.append(filename);
        }

        if (applied == 0 && skipped == 0) {
            return "No public-schema SQL scripts found.";
        }
        if (applied == 0) {
            return "Public schema already up to date (" + skipped + " script(s) previously applied).";
        }
        return "Public-schema scripts applied (" + names + ")"
                + (skipped > 0 ? "; skipped " + skipped + " already applied" : "") + ".";
    }

    private void ensureMigrationBookkeeping() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS %s (
                    script_name VARCHAR(255) PRIMARY KEY,
                    applied_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
                )
                """.formatted(BOOKKEEPING_TABLE));
    }

    private Set<String> loadAppliedScripts() {
        try {
            return new HashSet<>(jdbcTemplate.queryForList(
                    "SELECT script_name FROM " + BOOKKEEPING_TABLE, String.class));
        } catch (Exception ex) {
            log.debug("public_schema_migrations not readable yet: {}", ex.getMessage());
            return new HashSet<>();
        }
    }

    private void recordApplied(String filename) {
        jdbcTemplate.update(
                "INSERT INTO " + BOOKKEEPING_TABLE + " (script_name) VALUES (?) ON CONFLICT DO NOTHING",
                filename);
    }

    private void applyScript(Resource resource) {
        String sql;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            sql = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to read public-schema script " + resource.getFilename() + ": " + ex.getMessage(),
                    ex);
        }

        sql = stripSqlComments(sql);
        String[] statements = sql.split(";");
        for (String raw : statements) {
            String statement = raw.trim();
            if (!StringUtils.hasText(statement)) {
                continue;
            }
            jdbcTemplate.execute(statement);
        }
    }

    private static String stripSqlComments(String sql) {
        StringBuilder out = new StringBuilder();
        for (String line : sql.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--")) {
                continue;
            }
            out.append(line).append('\n');
        }
        return out.toString();
    }
}
