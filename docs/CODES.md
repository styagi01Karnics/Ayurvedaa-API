# Business codes

Hospital-scoped human-readable codes (UUID remains the DB/API technical PK).

## Format

`{tenantCode}-{TYPE}-{#####}` — five-digit zero-padded sequence per type within the current hospital schema.

Examples (tenant `GAN-DL`):

| Entity | TYPE | Example |
| --- | --- | --- |
| Patient | `PT` | `GAN-DL-PT-00001` |
| Doctor | `DOC` | `GAN-DL-DOC-00001` |
| Therapist | `THP` | `GAN-DL-THP-00001` |
| Therapy | `TH` | `GAN-DL-TH-00001` |
| Treatment category | `TC` | `GAN-DL-TC-00001` |
| Invoice | `INV` | `GAN-DL-INV-00001` |
| Attendance | `ATT` | `GAN-DL-ATT-00001` |

`tenantCode` comes from the JWT claim (same value as login), via `TenantContext` set by `TenantSchemaFilter`.

Next sequence = max trailing digits of existing codes with that tenant/type prefix in the schema, then +1 (start at 1).

## Patient

- `patientCode` is the only business identifier (`…-PT-#####`).
- `patientDisplayId` / `#PT…` / `patient_display_id` have been removed (API + `mst_patient`).
- Do not use hardcoded `patient.tenant-code`.

## Attendance note

Manual attendance `serialNumber` uses `{tenantCode}-ATT-#####` (same pattern). ADMS/iClock device SN is separate (`device_serial_number` / allowlist) and does not depend on the old `ATT-yyyyMMdd-####` format.
