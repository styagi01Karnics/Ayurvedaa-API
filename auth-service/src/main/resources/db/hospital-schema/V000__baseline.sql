-- Hospital schema baseline + migration version tracking.
-- Placeholder ${schema} is replaced with the target hosp_* schema name at provision time.

CREATE TABLE IF NOT EXISTS ${schema}.schema_baseline (
    id             SMALLINT PRIMARY KEY DEFAULT 1,
    provisioned_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    note           VARCHAR(255) NOT NULL DEFAULT 'auth-service hospital schema baseline'
);

CREATE TABLE IF NOT EXISTS ${schema}.schema_migrations (
    script_name  VARCHAR(255) PRIMARY KEY,
    applied_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
