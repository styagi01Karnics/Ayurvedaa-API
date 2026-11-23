-- doctor-service: optional user code linking a doctor to its user (unique per hospital among non-deleted doctors)
ALTER TABLE ${schema}.mst_doctor ADD COLUMN IF NOT EXISTS user_code VARCHAR(20);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mst_doctor_user_code
    ON ${schema}.mst_doctor (LOWER(user_code))
    WHERE user_code IS NOT NULL AND is_deleted = FALSE;
