-- Run as superuser AFTER 01_grant_ayurveda_schema.sql
-- Moves microservice tables from public schema into ayurveda schema.
-- Skips flyway history tables (services will recreate history in ayurveda).

DO $$
DECLARE
    t text;
    tables text[] := ARRAY[
        'mst_patient',
        'mst_doctor',
        'mst_therapist',
        'mst_treatment_category',
        'mst_therapy',
        'appointment_bookings',
        'appointment_consultation_types',
        'appointment_therapies',
        'appointment_therapy_recommendations',
        'appointment_ayurvedic_assessments',
        'appointment_physical_examinations',
        'appointment_medical_histories',
        'appointment_lifestyle_information',
        'appointment_systemic_examinations',
        'appointment_treatment_plans',
        'appointment_documents'
    ];
BEGIN
    FOREACH t IN ARRAY tables LOOP
        IF EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'public' AND table_name = t
        ) THEN
            EXECUTE format('ALTER TABLE public.%I SET SCHEMA ayurveda', t);
            RAISE NOTICE 'Moved public.% to ayurveda schema', t;
        END IF;
    END LOOP;
END $$;
