-- UPI Dynamic QR fields on payment_links (scan → pay exact amount).
ALTER TABLE ${schema}.payment_links
    ADD COLUMN IF NOT EXISTS upi_qr_payload TEXT;

ALTER TABLE ${schema}.payment_links
    ADD COLUMN IF NOT EXISTS payu_txn_id VARCHAR(25);
