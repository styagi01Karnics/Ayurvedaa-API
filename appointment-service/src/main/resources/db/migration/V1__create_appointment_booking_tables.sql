CREATE TABLE IF NOT EXISTS appointment_bookings (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL,
    registration_date DATE NOT NULL,
    assigned_doctor_id UUID NOT NULL,
    workflow_step VARCHAR(50),
    booking_status VARCHAR(50),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_appointment_booking_patient_id ON appointment_bookings (patient_id);

CREATE TABLE IF NOT EXISTS appointment_consultation_types (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    consultation_type VARCHAR(50) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_appointment_consultation_booking_id ON appointment_consultation_types (booking_id);
