package com.ayurveda.auth.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Applies ordered DDL scripts into a hospital schema ({@code hosp_*}).
 * <p>
 * Scripts live under {@code classpath:db/hospital-schema/*.sql}, sorted by filename.
 * Placeholder {@code ${schema}} is replaced with the target schema name.
 * Applied scripts are recorded in {@code schema_migrations} so re-runs
 * (including retry-provision) are idempotent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalSchemaMigrator {

    private static final String SCRIPT_PATTERN = "classpath:db/hospital-schema/*.sql";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Runs all hospital-schema scripts against {@code schemaName}, skipping ones already recorded.
     *
     * @return human-readable summary of what was applied
     */
    public String migrate(String schemaName) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources;
        try {
            resources = resolver.getResources(SCRIPT_PATTERN);
        } catch (Exception ex) {
            log.warn("No hospital-schema scripts found under {}: {}", SCRIPT_PATTERN, ex.getMessage());
            return "No hospital-schema SQL scripts found; empty schema " + schemaName + " only.";
        }

        if (resources.length == 0) {
            return "No hospital-schema SQL scripts found; empty schema " + schemaName + " only.";
        }

        Arrays.sort(resources, Comparator.comparing(Resource::getFilename, Comparator.nullsLast(String::compareTo)));

        ensureMigrationBookkeeping(schemaName);
        Set<String> alreadyApplied = loadAppliedScripts(schemaName);

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
            applyScript(schemaName, resource);
            recordApplied(schemaName, filename);
            applied++;
            if (names.length() > 0) {
                names.append(", ");
            }
            names.append(filename);
        }

        if (applied == 0 && skipped == 0) {
            return "No hospital-schema SQL scripts found; empty schema " + schemaName + " only.";
        }
        if (applied == 0) {
            return "Hospital schema " + schemaName + " already up to date (" + skipped + " script(s) previously applied).";
        }

        log.info("Applied {} hospital-schema script(s) to {} (skipped {}): {}", applied, schemaName, skipped, names);
        return "Hospital-schema scripts applied (" + names + ")"
                + (skipped > 0 ? "; skipped " + skipped + " already applied" : "")
                + ". Clinical tables provisioned in " + schemaName + ".";
    }

    private void ensureMigrationBookkeeping(String schemaName) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS %s.schema_baseline (
                    id SMALLINT PRIMARY KEY DEFAULT 1,
                    provisioned_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                    note VARCHAR(255) NOT NULL DEFAULT 'auth-service hospital schema baseline'
                )
                """.formatted(schemaName));
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS %s.schema_migrations (
                    script_name VARCHAR(255) PRIMARY KEY,
                    applied_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
                )
                """.formatted(schemaName));
        jdbcTemplate.update("""
                INSERT INTO %s.schema_baseline (id, note)
                VALUES (1, 'auth-service hospital schema baseline')
                ON CONFLICT (id) DO NOTHING
                """.formatted(schemaName));
    }

    private Set<String> loadAppliedScripts(String schemaName) {
        try {
            return new HashSet<>(jdbcTemplate.queryForList(
                    "SELECT script_name FROM " + schemaName + ".schema_migrations",
                    String.class));
        } catch (Exception ex) {
            log.debug("schema_migrations not readable yet for {}: {}", schemaName, ex.getMessage());
            return new HashSet<>();
        }
    }

    private void recordApplied(String schemaName, String filename) {
        jdbcTemplate.update(
                "INSERT INTO " + schemaName + ".schema_migrations (script_name) VALUES (?) ON CONFLICT DO NOTHING",
                filename);
    }

    private void applyScript(String schemaName, Resource resource) {
        String sql;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            sql = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to read hospital-schema script " + resource.getFilename() + ": " + ex.getMessage(),
                    ex);
        }

        sql = stripSqlComments(sql.replace("${schema}", schemaName));
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
