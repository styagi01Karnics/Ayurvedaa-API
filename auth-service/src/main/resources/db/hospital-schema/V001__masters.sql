-- Master / inventory tables derived from patient, doctor, therapist, medicine,
-- and appointment-service master entities. All extend BaseEntity.

-- patient-service: Patient
CREATE TABLE IF NOT EXISTS ${schema}.mst_patient (
    id                       UUID PRIMARY KEY,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted               BOOLEAN DEFAULT FALSE,
    patient_display_id       VARCHAR(20)  NOT NULL UNIQUE,
    patient_code             VARCHAR(50)  NOT NULL UNIQUE,
    first_name               VARCHAR(100) NOT NULL,
    last_name                VARCHAR(100) NOT NULL,
    gender                   VARCHAR(255) NOT NULL,
    date_of_birth            DATE,
    age                      INTEGER,
    preferred_language       VARCHAR(50),
    mobile_number            VARCHAR(15)  NOT NULL,
    email                    VARCHAR(150),
    state                    VARCHAR(100),
    city                     VARCHAR(100),
    address                  TEXT,
    emergency_contact_name   VARCHAR(100),
    emergency_relationship   VARCHAR(50),
    emergency_phone_number   VARCHAR(15),
    id_proof_type            VARCHAR(255),
    id_proof_number          VARCHAR(50),
    occupation               VARCHAR(100),
    insurance_details        VARCHAR(255),
    status                   VARCHAR(20)  NOT NULL
);

-- doctor-service: Doctor
CREATE TABLE IF NOT EXISTS ${schema}.mst_doctor (
    id                   UUID PRIMARY KEY,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted           BOOLEAN DEFAULT FALSE,
    doctor_name          VARCHAR(150) NOT NULL,
    doctor_code          VARCHAR(100) NOT NULL UNIQUE,
    specialization       VARCHAR(150),
    status               VARCHAR(20)  NOT NULL,
    consultation_fees    NUMERIC(12, 2),
    follow_up_fees       NUMERIC(12, 2),
    availability         VARCHAR(255),
    mobile_number        VARCHAR(15),
    email                VARCHAR(100),
    qualification        VARCHAR(100),
    department           VARCHAR(100),
    consultation_room    VARCHAR(100)
);

-- therapist-service: Therapist
CREATE TABLE IF NOT EXISTS ${schema}.mst_therapist (
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    therapist_name  VARCHAR(150) NOT NULL,
    therapist_code  VARCHAR(100) NOT NULL UNIQUE,
    status          VARCHAR(20)  NOT NULL,
    specialization  VARCHAR(150),
    mobile_number   VARCHAR(15),
    email           VARCHAR(100),
    qualification   VARCHAR(100),
    therapy_room    VARCHAR(100)
);

-- therapist-service: ElementCollection join table
CREATE TABLE IF NOT EXISTS ${schema}.mst_therapist_assigned_therapies (
    therapist_id UUID NOT NULL,
    therapy_id   UUID NOT NULL,
    CONSTRAINT fk_therapist_assigned_therapies_therapist
        FOREIGN KEY (therapist_id) REFERENCES ${schema}.mst_therapist (id)
);

-- medicine-service: Medicine
CREATE TABLE IF NOT EXISTS ${schema}.medicine_inventory (
    id                       UUID PRIMARY KEY,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted               BOOLEAN DEFAULT FALSE,
    medicine_name            VARCHAR(150) NOT NULL,
    category                 VARCHAR(30)  NOT NULL,
    manufacturer             VARCHAR(150) NOT NULL,
    batch_number             VARCHAR(100) NOT NULL,
    quantity                 INTEGER      NOT NULL,
    expiry_date              DATE         NOT NULL,
    purchase_price           NUMERIC(12, 2) NOT NULL,
    selling_price            NUMERIC(12, 2) NOT NULL,
    low_stock_alert_enabled  BOOLEAN      NOT NULL DEFAULT FALSE,
    low_stock_threshold      INTEGER      NOT NULL DEFAULT 20,
    status                   VARCHAR(20)  NOT NULL,
    stock_status             VARCHAR(20)  NOT NULL
);

-- appointment-service masters
CREATE TABLE IF NOT EXISTS ${schema}.mst_treatment_category (
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    category_code   VARCHAR(20)  NOT NULL UNIQUE,
    category_name   VARCHAR(255) NOT NULL UNIQUE,
    description     VARCHAR(500),
    status          VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.mst_therapy (
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted        BOOLEAN DEFAULT FALSE,
    category_id       UUID         NOT NULL,
    therapy_code      VARCHAR(20)  NOT NULL UNIQUE,
    therapy_name      VARCHAR(255) NOT NULL UNIQUE,
    description       VARCHAR(500),
    status            VARCHAR(20)  NOT NULL,
    duration_minutes  INTEGER      NOT NULL,
    price             NUMERIC(12, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.mst_doshas (
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted       BOOLEAN DEFAULT FALSE,
    name             VARCHAR(100) NOT NULL UNIQUE,
    elements         TEXT,
    characteristics  TEXT,
    status           VARCHAR(20)  NOT NULL,
    active           BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.mst_consultation_type (
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted  BOOLEAN DEFAULT FALSE,
    name        VARCHAR(100) NOT NULL UNIQUE,
    status      VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.mst_treatment_plan (
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted  BOOLEAN DEFAULT FALSE,
    name        VARCHAR(150) NOT NULL UNIQUE,
    status      VARCHAR(20)  NOT NULL
);

-- billing-service: PackageMaster (master data)
CREATE TABLE IF NOT EXISTS ${schema}.mst_package (
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted     BOOLEAN DEFAULT FALSE,
    name           VARCHAR(150) NOT NULL UNIQUE,
    package_price  NUMERIC(12, 2) NOT NULL,
    status         VARCHAR(20)  NOT NULL
);
