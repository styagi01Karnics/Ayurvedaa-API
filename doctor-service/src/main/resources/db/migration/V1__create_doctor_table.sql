CREATE TABLE IF NOT EXISTS mst_doctor (
    id UUID PRIMARY KEY,
    doctor_name VARCHAR(150) NOT NULL,
    doctor_code VARCHAR(100) NOT NULL UNIQUE,
    specialization VARCHAR(150),
    mobile_number VARCHAR(15),
    email VARCHAR(100),
    qualification VARCHAR(100),
    department VARCHAR(100),
    consultation_room VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_doctor_code ON mst_doctor (doctor_code);
