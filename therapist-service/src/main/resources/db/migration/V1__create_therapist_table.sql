CREATE TABLE IF NOT EXISTS mst_therapist (
    id UUID PRIMARY KEY,
    therapist_name VARCHAR(150) NOT NULL,
    therapist_code VARCHAR(100) NOT NULL UNIQUE,
    specialization VARCHAR(150),
    mobile_number VARCHAR(15),
    email VARCHAR(100),
    qualification VARCHAR(100),
    therapy_room VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_therapist_code ON mst_therapist (therapist_code);
