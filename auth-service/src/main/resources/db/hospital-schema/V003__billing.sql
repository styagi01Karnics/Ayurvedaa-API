-- Billing tables from billing-service entities.

CREATE TABLE IF NOT EXISTS ${schema}.billings (
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted       BOOLEAN DEFAULT FALSE,
    patient_id       UUID NOT NULL,
    patient_name     VARCHAR(150) NOT NULL,
    contact_number   VARCHAR(20),
    billing_date     DATE NOT NULL,
    status           VARCHAR(20) NOT NULL,
    invoice_id       UUID,
    invoice_number   VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS ${schema}.billing_service_items (
    id                  UUID PRIMARY KEY,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted          BOOLEAN DEFAULT FALSE,
    billing_id          UUID NOT NULL,
    service_type        VARCHAR(100),
    service_fees        NUMERIC(12, 2),
    package_master_id   UUID,
    package_type        VARCHAR(100),
    package_charges     NUMERIC(12, 2)
);

CREATE TABLE IF NOT EXISTS ${schema}.billing_invoices (
    id                 UUID PRIMARY KEY,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted         BOOLEAN DEFAULT FALSE,
    invoice_number     VARCHAR(50) NOT NULL UNIQUE,
    patient_id         UUID NOT NULL,
    patient_name       VARCHAR(150) NOT NULL,
    contact_number     VARCHAR(20),
    invoice_date       DATE NOT NULL,
    visit_type         VARCHAR(30),
    service_fees       NUMERIC(12, 2),
    package_master_id  UUID,
    package_type       VARCHAR(100),
    package_charges    NUMERIC(12, 2),
    subtotal           NUMERIC(12, 2) NOT NULL,
    discount           NUMERIC(12, 2) NOT NULL DEFAULT 0,
    tax_enabled        BOOLEAN NOT NULL DEFAULT FALSE,
    cgst_percent       NUMERIC(5, 2),
    cgst_amount        NUMERIC(12, 2),
    sgst_percent       NUMERIC(5, 2),
    sgst_amount        NUMERIC(12, 2),
    total_amount       NUMERIC(12, 2) NOT NULL,
    paid_amount        NUMERIC(12, 2) NOT NULL DEFAULT 0,
    left_amount        NUMERIC(12, 2) NOT NULL,
    status             VARCHAR(20) NOT NULL,
    bill_sections      VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.billing_invoice_items (
    id                        UUID PRIMARY KEY,
    created_at                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted                BOOLEAN DEFAULT FALSE,
    invoice_id                UUID NOT NULL,
    item_type                 VARCHAR(30) NOT NULL,
    item_name                 VARCHAR(150) NOT NULL,
    quantity                  INTEGER NOT NULL,
    unit_price                NUMERIC(12, 2) NOT NULL,
    amount                    NUMERIC(12, 2) NOT NULL,
    medicine_id               UUID,
    assigned_therapist_id     UUID,
    assigned_therapist_name   VARCHAR(150),
    schedule_date             DATE,
    schedule_time             TIME,
    session_duration          INTEGER,
    session_frequency         INTEGER,
    CONSTRAINT fk_billing_invoice_items_invoice
        FOREIGN KEY (invoice_id) REFERENCES ${schema}.billing_invoices (id)
);

CREATE TABLE IF NOT EXISTS ${schema}.billing_invoice_payments (
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted       BOOLEAN DEFAULT FALSE,
    invoice_id       UUID NOT NULL,
    amount_paid      NUMERIC(12, 2) NOT NULL,
    payment_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    payment_method   VARCHAR(50),
    remarks          VARCHAR(255),
    CONSTRAINT fk_billing_invoice_payments_invoice
        FOREIGN KEY (invoice_id) REFERENCES ${schema}.billing_invoices (id)
);

CREATE TABLE IF NOT EXISTS ${schema}.patient_packages (
    id                  UUID PRIMARY KEY,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted          BOOLEAN DEFAULT FALSE,
    patient_id          UUID NOT NULL,
    package_master_id   UUID NOT NULL,
    validity            DATE NOT NULL,
    status              VARCHAR(30) NOT NULL,
    discount_applied    NUMERIC(12, 2) NOT NULL
);
