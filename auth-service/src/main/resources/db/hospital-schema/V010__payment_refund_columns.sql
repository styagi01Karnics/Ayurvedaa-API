-- PayU refund tracking columns on payments.
ALTER TABLE ${schema}.payments
    ADD COLUMN IF NOT EXISTS refunded_amount NUMERIC(12, 2) NOT NULL DEFAULT 0;

ALTER TABLE ${schema}.payments
    ADD COLUMN IF NOT EXISTS last_refund_request_id VARCHAR(100);

ALTER TABLE ${schema}.payments
    ADD COLUMN IF NOT EXISTS last_refund_token VARCHAR(64);
