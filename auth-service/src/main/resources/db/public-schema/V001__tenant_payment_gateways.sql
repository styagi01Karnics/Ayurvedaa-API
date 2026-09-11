-- Per-tenant payment gateway credentials (PayU hosted checkout).
-- Super Admin writes these in public so no hospital JWT is required.
-- Do NOT seed live merchant keys, salts, client ids, or client secrets here.

CREATE TABLE IF NOT EXISTS public.tenant_payment_gateways (
    id                     UUID PRIMARY KEY,
    created_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted             BOOLEAN DEFAULT FALSE,
    tenant_code            VARCHAR(50)  NOT NULL UNIQUE,
    provider               VARCHAR(20)  NOT NULL,
    merchant_key           VARCHAR(100) NOT NULL,
    merchant_salt          VARCHAR(255) NOT NULL,
    client_id              VARCHAR(255),
    client_secret          VARCHAR(255),
    mode                   VARCHAR(10)  NOT NULL,
    payment_url            VARCHAR(500),
    enabled                BOOLEAN      NOT NULL DEFAULT TRUE
);
