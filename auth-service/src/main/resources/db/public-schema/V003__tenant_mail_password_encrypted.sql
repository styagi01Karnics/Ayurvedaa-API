-- Widen smtp_password for AES-GCM ciphertext (salt + IV + encrypted value).
ALTER TABLE public.tenant_mail_settings
    ALTER COLUMN smtp_password TYPE VARCHAR(1024);
