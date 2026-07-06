CREATE TABLE IF NOT EXISTS mst_doshas (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    elements TEXT,
    characteristics TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

INSERT INTO mst_doshas (id, name, elements, characteristics, active, is_deleted, created_at, updated_at)
VALUES
    ('11111111-1111-1111-1111-111111111001', 'Vata', 'Air & Ether', 'Dry, light, cold, rough, subtle, mobile', TRUE, FALSE, NOW(), NOW()),
    ('11111111-1111-1111-1111-111111111002', 'Pitta', 'Fire & Water', 'Hot, sharp, light, liquid, spreading', TRUE, FALSE, NOW(), NOW()),
    ('11111111-1111-1111-1111-111111111003', 'Kapha', 'Earth & Water', 'Heavy, slow, cool, oily, smooth, dense', TRUE, FALSE, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

ALTER TABLE appointment_ayurvedic_assessments
    ADD COLUMN IF NOT EXISTS dosha_id UUID;

UPDATE appointment_ayurvedic_assessments a
SET dosha_id = d.id
FROM mst_doshas d
WHERE a.dosha_id IS NULL
  AND a.dosha_type IS NOT NULL
  AND LOWER(TRIM(a.dosha_type)) = LOWER(TRIM(d.name));

ALTER TABLE appointment_ayurvedic_assessments
    DROP COLUMN IF EXISTS dosha_type;
