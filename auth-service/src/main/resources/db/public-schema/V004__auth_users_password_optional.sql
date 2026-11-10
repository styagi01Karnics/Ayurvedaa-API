-- Doctor logins are created without a password; the doctor sets it via the reset-password link.
ALTER TABLE IF EXISTS public.auth_users
    ALTER COLUMN password_hash DROP NOT NULL;
