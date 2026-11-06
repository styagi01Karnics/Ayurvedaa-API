-- medicine-service: medicine category master (replaces the fixed TABLET/SYRUP/POWDER enum)
CREATE TABLE IF NOT EXISTS ${schema}.mst_medicine_category (
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_deleted  BOOLEAN DEFAULT FALSE,
    code        VARCHAR(30)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mst_medicine_category_code ON ${schema}.mst_medicine_category (code);

-- Seed the codes that already exist in medicine_inventory.category.
INSERT INTO ${schema}.mst_medicine_category (id, created_at, updated_at, is_deleted, code, name, active)
VALUES
    (gen_random_uuid(), NOW(), NOW(), FALSE, 'TABLET', 'Tablet', TRUE),
    (gen_random_uuid(), NOW(), NOW(), FALSE, 'SYRUP', 'Syrup', TRUE),
    (gen_random_uuid(), NOW(), NOW(), FALSE, 'POWDER', 'Powder', TRUE)
ON CONFLICT (code) DO NOTHING;
