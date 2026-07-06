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
    patient_id UUID NOT NULL,
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
    patient_id UUID NOT NULL,
    dosha_type VARCHAR(100),
    body_constitution TEXT,
    current_imbalances TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment_physical_examinations (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL,
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
    patient_id UUID NOT NULL,
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
    patient_id UUID NOT NULL,
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
    patient_id UUID NOT NULL,
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
    patient_id UUID NOT NULL,
    investigation_and_plan_suggested TEXT,
    plan_taken TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_appointment_therapies_patient_id ON appointment_therapies (patient_id);
CREATE INDEX IF NOT EXISTS idx_mst_therapy_category_id ON mst_therapy (category_id);
