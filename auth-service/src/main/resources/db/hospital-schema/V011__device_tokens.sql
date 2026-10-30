-- notification-service: FCM device tokens per user (device push)
CREATE TABLE IF NOT EXISTS ${schema}.device_tokens (
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted        BOOLEAN DEFAULT FALSE,
    user_id           UUID NOT NULL,
    token             VARCHAR(512) NOT NULL,
    platform          VARCHAR(20)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_device_tokens_token ON ${schema}.device_tokens (token);
CREATE INDEX IF NOT EXISTS idx_device_tokens_user ON ${schema}.device_tokens (user_id);
