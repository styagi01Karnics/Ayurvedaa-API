-- Per-hospital outbound SMTP (patient emails). Separate from admin login.
-- Password is the mailbox SMTP / Gmail App Password, stored so mail can be sent.
-- Do NOT seed live mailbox passwords here.

CREATE TABLE IF NOT EXISTS public.tenant_mail_settings (
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    tenant_code     VARCHAR(50)  NOT NULL UNIQUE,
    provider        VARCHAR(20)  NOT NULL,
    from_email      VARCHAR(150) NOT NULL,
    smtp_password   VARCHAR(1024) NOT NULL,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE
);
