-- Existing users created before user codes were mandatory get a generated code (USR + 12 hex chars of their id).
UPDATE public.auth_users
SET user_code = 'USR' || UPPER(SUBSTRING(REPLACE(id::text, '-', ''), 1, 12))
WHERE user_code IS NULL;

ALTER TABLE IF EXISTS public.auth_users
    ALTER COLUMN user_code SET NOT NULL;

-- Profession becomes an enum (DOCTOR, OTHERS). Legacy free-text values are mapped onto it.
UPDATE public.auth_users
SET profession = CASE
    WHEN TRIM(profession) = '' THEN NULL
    WHEN LOWER(TRIM(profession)) = 'doctor' THEN 'DOCTOR'
    ELSE 'OTHERS'
END
WHERE profession IS NOT NULL;

ALTER TABLE IF EXISTS public.auth_users
    ALTER COLUMN profession TYPE VARCHAR(20);
