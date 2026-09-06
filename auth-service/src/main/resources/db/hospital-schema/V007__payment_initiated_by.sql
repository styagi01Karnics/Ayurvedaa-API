-- PayU initiator for Kafka notifications.

ALTER TABLE ${schema}.payments
    ADD COLUMN IF NOT EXISTS initiated_by_user_id UUID;
