-- =============================================================================
-- Run in DBeaver connected as postgres (superuser) on database: ayurveda_db
-- Purpose: move microservice tables from public -> ayurveda schema
-- =============================================================================

ALTER SCHEMA ayurveda OWNER TO ayurveda;

GRANT USAGE, CREATE ON SCHEMA ayurveda TO ayurveda;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ayurveda TO ayurveda;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ayurveda TO ayurveda;
ALTER DEFAULT PRIVILEGES IN SCHEMA ayurveda GRANT ALL ON TABLES TO ayurveda;
ALTER DEFAULT PRIVILEGES IN SCHEMA ayurveda GRANT ALL ON SEQUENCES TO ayurveda;

-- Move application tables
ALTER TABLE IF EXISTS public.mst_patient SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.mst_doctor SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.mst_therapist SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.mst_treatment_category SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.mst_therapy SET SCHEMA ayurveda;

ALTER TABLE IF EXISTS public.appointment_bookings SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_consultation_types SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_therapies SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_therapy_recommendations SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_ayurvedic_assessments SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_physical_examinations SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_medical_histories SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_lifestyle_information SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_systemic_examinations SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_treatment_plans SET SCHEMA ayurveda;
ALTER TABLE IF EXISTS public.appointment_documents SET SCHEMA ayurveda;

-- Optional: remove old Flyway history from public (services recreate history in ayurveda)
DROP TABLE IF EXISTS public.flyway_schema_history_patient;
DROP TABLE IF EXISTS public.flyway_schema_history_doctor;
DROP TABLE IF EXISTS public.flyway_schema_history_doctor_v2;
DROP TABLE IF EXISTS public.flyway_schema_history_therapist;
DROP TABLE IF EXISTS public.flyway_schema_history_therapist_v2;
DROP TABLE IF EXISTS public.flyway_schema_history_appointment;
DROP TABLE IF EXISTS public.flyway_schema_history_appointment_v2;
DROP TABLE IF EXISTS public.flyway_schema_history_file_upload;
DROP TABLE IF EXISTS public.flyway_schema_history_file_upload_v2;

-- Verify
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_schema IN ('ayurveda', 'public')
  AND table_type = 'BASE TABLE'
ORDER BY table_schema, table_name;
