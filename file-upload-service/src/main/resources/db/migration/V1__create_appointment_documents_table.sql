CREATE TABLE IF NOT EXISTS appointment_documents (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(150) NOT NULL,
    file_size BIGINT NOT NULL,
    file_data BYTEA NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_appointment_documents_booking_id ON appointment_documents (booking_id);
