-- Run as PostgreSQL superuser on database ayurveda_db
-- Example: psql -h 103.174.103.250 -U postgres -d ayurveda_db -f 00_setup_ayurveda_schema.sql

ALTER SCHEMA ayurveda OWNER TO ayurveda;

GRANT USAGE, CREATE ON SCHEMA ayurveda TO ayurveda;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ayurveda TO ayurveda;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ayurveda TO ayurveda;
ALTER DEFAULT PRIVILEGES IN SCHEMA ayurveda GRANT ALL ON TABLES TO ayurveda;
ALTER DEFAULT PRIVILEGES IN SCHEMA ayurveda GRANT ALL ON SEQUENCES TO ayurveda;

-- Move microservice tables from public into ayurveda (if they exist from earlier runs)
DO $$
DECLARE
    t text;
    tables text[] := ARRAY[
        'mst_patient', 'mst_doctor', 'mst_therapist', 'mst_treatment_category', 'mst_therapy',
        'appointment_bookings', 'appointment_consultation_types', 'appointment_therapies',
        'appointment_therapy_recommendations', 'appointment_ayurvedic_assessments',
        'appointment_physical_examinations', 'appointment_medical_histories',
        'appointment_lifestyle_information', 'appointment_systemic_examinations',
        'appointment_treatment_plans', 'appointment_documents'
    ];
BEGIN
    FOREACH t IN ARRAY tables LOOP
        IF EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'public' AND table_name = t
        ) AND NOT EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'ayurveda' AND table_name = t
        ) THEN
            EXECUTE format('ALTER TABLE public.%I SET SCHEMA ayurveda', t);
        END IF;
    END LOOP;
END $$;

SET search_path TO ayurveda;

-- Patient
CREATE TABLE IF NOT EXISTS mst_patient (
    id UUID PRIMARY KEY,
    patient_code VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    mobile_number VARCHAR(15),
    date_of_birth DATE,
    gender VARCHAR(20),
    address TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_patient_code ON mst_patient (patient_code);

ALTER TABLE mst_patient
    ADD COLUMN IF NOT EXISTS age INTEGER,
    ADD COLUMN IF NOT EXISTS preferred_language VARCHAR(50),
    ADD COLUMN IF NOT EXISTS state VARCHAR(100),
    ADD COLUMN IF NOT EXISTS city VARCHAR(100),
    ADD COLUMN IF NOT EXISTS emergency_contact_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS emergency_relationship VARCHAR(50),
    ADD COLUMN IF NOT EXISTS emergency_phone_number VARCHAR(15),
    ADD COLUMN IF NOT EXISTS id_proof_type VARCHAR(30),
    ADD COLUMN IF NOT EXISTS id_proof_number VARCHAR(50),
    ADD COLUMN IF NOT EXISTS occupation VARCHAR(100),
    ADD COLUMN IF NOT EXISTS insurance_details VARCHAR(255);

-- Doctor
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

-- Therapist
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

-- Appointment booking
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

-- Appointment domain
CREATE TABLE IF NOT EXISTS mst_treatment_category (
    id UUID PRIMARY KEY,
    category_code VARCHAR(20) NOT NULL UNIQUE,
    category_name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS mst_therapy (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL,
    therapy_code VARCHAR(20) NOT NULL UNIQUE,
    therapy_name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment_therapies (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    treatment_category_id UUID NOT NULL,
    assigned_therapist_id UUID NOT NULL,
    schedule_date DATE NOT NULL,
    schedule_time TIME NOT NULL,
    session_duration INTEGER NOT NULL,
    session_frequency INTEGER NOT NULL,
    therapy_instructions TEXT,
    remarks TEXT,
    therapy_status VARCHAR(50) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment_therapy_recommendations (
    id UUID PRIMARY KEY,
    appointment_therapy_id UUID NOT NULL,
    therapy_master_id UUID NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment_ayurvedic_assessments (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    dosha_type VARCHAR(100),
    body_constitution TEXT,
    current_imbalances TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment_physical_examinations (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    height DOUBLE PRECISION NOT NULL,
    ibw DOUBLE PRECISION NOT NULL,
    pulse INTEGER NOT NULL,
    bp VARCHAR(20) NOT NULL,
    temperature DOUBLE PRECISION NOT NULL,
    pallor VARCHAR(100),
    icterus VARCHAR(100),
    cyanosis VARCHAR(100),
    lymph_nodes VARCHAR(100),
    oedema VARCHAR(100),
    sensorium VARCHAR(100),
    acidity_gas VARCHAR(100),
    motion VARCHAR(100),
    micturition VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment_medical_histories (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    past_medical_conditions TEXT,
    past_surgeries TEXT,
    current_medications TEXT,
    allergies TEXT,
    family_history TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment_lifestyle_information (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    diet_type VARCHAR(100),
    sleep_pattern VARCHAR(100),
    exercise_habits VARCHAR(100),
    addiction VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment_systemic_examinations (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    cardiovascular VARCHAR(1000),
    respiratory VARCHAR(1000),
    nervous VARCHAR(1000),
    abdomen_gi VARCHAR(1000),
    locomotor VARCHAR(1000),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment_treatment_plans (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    investigation_and_plan_suggested TEXT,
    plan_taken TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_appointment_therapies_booking_id ON appointment_therapies (booking_id);
CREATE INDEX IF NOT EXISTS idx_mst_therapy_category_id ON mst_therapy (category_id);

-- Documents
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
