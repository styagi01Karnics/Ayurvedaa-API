-- Appointment / clinical visit tables from appointment-service entities.

CREATE TABLE IF NOT EXISTS ${schema}.appointment_bookings (
    id                   UUID PRIMARY KEY,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted           BOOLEAN DEFAULT FALSE,
    patient_id           UUID NOT NULL,
    registration_date    DATE NOT NULL,
    slot_time            TIME,
    assigned_doctor_id   UUID NOT NULL,
    booking_status       VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ${schema}.appointment_consultation_types (
    id                     UUID PRIMARY KEY,
    created_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted             BOOLEAN DEFAULT FALSE,
    booking_id             UUID NOT NULL,
    consultation_type_id   UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.treatments (
    id                      UUID PRIMARY KEY,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted              BOOLEAN DEFAULT FALSE,
    patient_id              UUID NOT NULL,
    treatment_plan_id       UUID NOT NULL,
    start_date              DATE NOT NULL,
    end_date                DATE NOT NULL,
    total_sessions          INTEGER NOT NULL,
    completed_sessions      INTEGER NOT NULL,
    remaining_sessions      INTEGER NOT NULL,
    assigned_therapist_id   UUID NOT NULL,
    treatment_status        VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.follow_ups (
    id                       UUID PRIMARY KEY,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted               BOOLEAN DEFAULT FALSE,
    patient_id               UUID NOT NULL,
    assigned_doctor_id       UUID NOT NULL,
    source_booking_id        UUID,
    visit_type_id            UUID NOT NULL,
    appointment_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    scheduling_option        VARCHAR(50),
    sms_reminder_enabled     BOOLEAN NOT NULL DEFAULT FALSE,
    status                   VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.appointment_therapies (
    id                      UUID PRIMARY KEY,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted              BOOLEAN DEFAULT FALSE,
    patient_id              UUID NOT NULL,
    treatment_category_id   UUID NOT NULL,
    assigned_therapist_id   UUID NOT NULL,
    schedule_date           DATE NOT NULL,
    schedule_time           TIME NOT NULL,
    session_duration        INTEGER NOT NULL,
    session_frequency       INTEGER NOT NULL,
    therapy_instructions    TEXT,
    remarks                 TEXT,
    therapy_status          VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.appointment_therapy_recommendations (
    id                      UUID PRIMARY KEY,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted              BOOLEAN DEFAULT FALSE,
    appointment_therapy_id  UUID NOT NULL,
    therapy_master_id       UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.prescriptions (
    id                           UUID PRIMARY KEY,
    created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted                   BOOLEAN DEFAULT FALSE,
    patient_id                   UUID NOT NULL,
    appointment_booking_id       UUID,
    assigned_doctor_id           UUID,
    follow_up_required           BOOLEAN NOT NULL DEFAULT FALSE,
    follow_up_scheduling_option  VARCHAR(100),
    follow_up_suggestions        TEXT,
    diagnosis                    TEXT,
    notes                        TEXT
);

CREATE TABLE IF NOT EXISTS ${schema}.prescription_medicines (
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted       BOOLEAN DEFAULT FALSE,
    prescription_id  UUID NOT NULL,
    medicine_id      UUID,
    medicine_name    VARCHAR(255) NOT NULL,
    dosage           VARCHAR(100),
    frequency        VARCHAR(100),
    duration         VARCHAR(100),
    notes            VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS ${schema}.prescription_therapy_suggestions (
    id                    UUID PRIMARY KEY,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted            BOOLEAN DEFAULT FALSE,
    prescription_id       UUID NOT NULL,
    therapy_category_id   UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.prescription_therapy_suggestion_items (
    id                      UUID PRIMARY KEY,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted              BOOLEAN DEFAULT FALSE,
    therapy_suggestion_id   UUID NOT NULL,
    therapy_master_id       UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.appointment_medical_histories (
    id                       UUID PRIMARY KEY,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted               BOOLEAN DEFAULT FALSE,
    patient_id               UUID NOT NULL,
    past_medical_conditions  TEXT,
    past_surgeries           TEXT,
    current_medications      TEXT,
    allergies                TEXT,
    family_history           TEXT
);

CREATE TABLE IF NOT EXISTS ${schema}.appointment_physical_examinations (
    id            UUID PRIMARY KEY,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted    BOOLEAN DEFAULT FALSE,
    patient_id    UUID NOT NULL,
    weight        DOUBLE PRECISION NOT NULL,
    height        DOUBLE PRECISION NOT NULL,
    ibw           DOUBLE PRECISION NOT NULL,
    pulse         INTEGER NOT NULL,
    bp            VARCHAR(20) NOT NULL,
    temperature   DOUBLE PRECISION NOT NULL,
    pallor        VARCHAR(100),
    icterus       VARCHAR(100),
    cyanosis      VARCHAR(100),
    lymph_nodes   VARCHAR(100),
    oedema        VARCHAR(100),
    sensorium     VARCHAR(100),
    acidity_gas   VARCHAR(100),
    motion        VARCHAR(100),
    micturition   VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS ${schema}.appointment_systemic_examinations (
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    patient_id      UUID NOT NULL,
    cardiovascular  VARCHAR(1000),
    respiratory     VARCHAR(1000),
    nervous         VARCHAR(1000),
    abdomen_gi      VARCHAR(1000),
    locomotor       VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS ${schema}.appointment_ayurvedic_assessments (
    id                   UUID PRIMARY KEY,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted           BOOLEAN DEFAULT FALSE,
    patient_id           UUID NOT NULL,
    dosha_id             UUID NOT NULL,
    body_constitution    TEXT,
    current_imbalances   TEXT
);

CREATE TABLE IF NOT EXISTS ${schema}.appointment_lifestyle_information (
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted       BOOLEAN DEFAULT FALSE,
    patient_id       UUID NOT NULL,
    diet_type        VARCHAR(100),
    sleep_pattern    VARCHAR(100),
    exercise_habits  VARCHAR(100),
    addiction        VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS ${schema}.appointment_treatment_plans (
    id                                 UUID PRIMARY KEY,
    created_at                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted                         BOOLEAN DEFAULT FALSE,
    patient_id                         UUID NOT NULL,
    investigation_and_plan_suggested   TEXT,
    plan_taken                         TEXT
);
