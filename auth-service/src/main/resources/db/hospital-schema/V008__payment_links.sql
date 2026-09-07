-- Shareable invoice payment links (payment-service). Token is {schema}.{uuid}.

CREATE TABLE IF NOT EXISTS ${schema}.payment_links (
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    token           VARCHAR(120) NOT NULL UNIQUE,
    invoice_id      UUID NOT NULL,
    patient_id      UUID,
    amount          NUMERIC(12, 2) NOT NULL,
    invoice_number  VARCHAR(80),
    first_name      VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    phone           VARCHAR(20),
    schema_name     VARCHAR(63),
    tenant_code     VARCHAR(50),
    tenant_id       UUID,
    expires_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status          VARCHAR(20)
);
