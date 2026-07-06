DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'appointment_therapies' AND column_name = 'booking_id'
    ) THEN
        ALTER TABLE appointment_therapies RENAME COLUMN booking_id TO patient_id;
    END IF;
END $$;

ALTER INDEX IF EXISTS idx_appointment_therapies_booking_id
    RENAME TO idx_appointment_therapies_patient_id;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'appointment_ayurvedic_assessments' AND column_name = 'booking_id'
    ) THEN
        ALTER TABLE appointment_ayurvedic_assessments RENAME COLUMN booking_id TO patient_id;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'appointment_physical_examinations' AND column_name = 'booking_id'
    ) THEN
        ALTER TABLE appointment_physical_examinations RENAME COLUMN booking_id TO patient_id;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'appointment_medical_histories' AND column_name = 'booking_id'
    ) THEN
        ALTER TABLE appointment_medical_histories RENAME COLUMN booking_id TO patient_id;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'appointment_lifestyle_information' AND column_name = 'booking_id'
    ) THEN
        ALTER TABLE appointment_lifestyle_information RENAME COLUMN booking_id TO patient_id;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'appointment_systemic_examinations' AND column_name = 'booking_id'
    ) THEN
        ALTER TABLE appointment_systemic_examinations RENAME COLUMN booking_id TO patient_id;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'appointment_treatment_plans' AND column_name = 'booking_id'
    ) THEN
        ALTER TABLE appointment_treatment_plans RENAME COLUMN booking_id TO patient_id;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_appointment_ayurvedic_assessments_patient_id
    ON appointment_ayurvedic_assessments (patient_id);

CREATE INDEX IF NOT EXISTS idx_appointment_physical_examinations_patient_id
    ON appointment_physical_examinations (patient_id);

CREATE INDEX IF NOT EXISTS idx_appointment_medical_histories_patient_id
    ON appointment_medical_histories (patient_id);

CREATE INDEX IF NOT EXISTS idx_appointment_lifestyle_information_patient_id
    ON appointment_lifestyle_information (patient_id);

CREATE INDEX IF NOT EXISTS idx_appointment_systemic_examinations_patient_id
    ON appointment_systemic_examinations (patient_id);

CREATE INDEX IF NOT EXISTS idx_appointment_treatment_plans_patient_id
    ON appointment_treatment_plans (patient_id);
