-- Attendance, file-upload, activity-log, and notification tables.

-- attendance-service
CREATE TABLE IF NOT EXISTS ${schema}.attendances (
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted        BOOLEAN DEFAULT FALSE,
    serial_number     VARCHAR(50)  NOT NULL UNIQUE,
    emp_id            VARCHAR(255) NOT NULL,
    emp_name          VARCHAR(150) NOT NULL,
    staff_type        VARCHAR(255) NOT NULL,
    attendance_date   DATE NOT NULL,
    check_in_time     TIMESTAMP WITHOUT TIME ZONE,
    check_out_time    TIMESTAMP WITHOUT TIME ZONE,
    status            VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.employee_attendance_master (
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    emp_id          VARCHAR(50)  NOT NULL UNIQUE,
    emp_name        VARCHAR(150) NOT NULL,
    staff_type      VARCHAR(30)  NOT NULL,
    department      VARCHAR(100),
    shift           VARCHAR(50),
    stp             VARCHAR(100),
    designation     VARCHAR(100),
    mobile_number   VARCHAR(15),
    email           VARCHAR(100),
    status          VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS ${schema}.device_attendance_logs (
    id                     UUID PRIMARY KEY,
    created_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted             BOOLEAN DEFAULT FALSE,
    device_serial_number   VARCHAR(50),
    table_name             VARCHAR(50),
    employee_id            VARCHAR(50) NOT NULL,
    punch_date             DATE,
    punch_time             TIME,
    punch_date_time        TIMESTAMP WITHOUT TIME ZONE,
    raw_punch_date         VARCHAR(50),
    raw_punch_time         VARCHAR(50),
    raw_line               VARCHAR(500)
);

-- file-upload-service
CREATE TABLE IF NOT EXISTS ${schema}.appointment_documents (
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    patient_id      UUID NOT NULL,
    document_type   VARCHAR(255) NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    file_type       VARCHAR(255) NOT NULL,
    file_size       BIGINT NOT NULL,
    file_data       BYTEA NOT NULL
);

-- activity-log-service
CREATE TABLE IF NOT EXISTS ${schema}.activity_logs (
    id                       UUID PRIMARY KEY,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted               BOOLEAN DEFAULT FALSE,
    page                     VARCHAR(100) NOT NULL,
    action                   VARCHAR(30)  NOT NULL,
    target                   VARCHAR(150) NOT NULL,
    before_value             TEXT,
    after_value              TEXT,
    activity_timestamp       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    performed_by_user_id     UUID,
    performed_by_user_name   VARCHAR(150),
    performed_by_role        VARCHAR(50)
);

-- notification-service (column "read" quoted — reserved-ish identifier)
CREATE TABLE IF NOT EXISTS ${schema}.notifications (
    id                    UUID PRIMARY KEY,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted            BOOLEAN DEFAULT FALSE,
    recipient_user_id     UUID NOT NULL,
    recipient_user_name   VARCHAR(150),
    recipient_role        VARCHAR(50),
    title                 VARCHAR(200) NOT NULL,
    message               TEXT NOT NULL,
    type                  VARCHAR(30) NOT NULL,
    priority              VARCHAR(20) NOT NULL,
    reference_id          UUID,
    reference_type        VARCHAR(50),
    "read"                BOOLEAN NOT NULL DEFAULT FALSE,
    read_at               TIMESTAMP WITHOUT TIME ZONE
);
