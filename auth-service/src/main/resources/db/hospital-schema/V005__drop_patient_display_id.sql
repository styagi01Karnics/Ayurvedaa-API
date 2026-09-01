-- Drop obsolete patient display id and widen code columns for tenant-prefixed business codes.
-- Applied to already-provisioned hospital schemas via HospitalSchemaMigrator.
-- Fresh onboard uses updated V001 (no patient_display_id); this script is still idempotent.

ALTER TABLE ${schema}.mst_patient DROP COLUMN IF EXISTS patient_display_id;

ALTER TABLE ${schema}.mst_therapy
    ALTER COLUMN therapy_code TYPE VARCHAR(50);

ALTER TABLE ${schema}.mst_treatment_category
    ALTER COLUMN category_code TYPE VARCHAR(50);
