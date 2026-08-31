# Tenant schema wiring (clinical services)

## Overview

Hospital JWTs include claim `schemaName` (e.g. `hosp_gan_dl`). Clinical services set PostgreSQL `search_path` to that schema for the request so JPA hits the hospital’s tables. Auth/platform tables stay in `public` (auth-service does **not** enable this routing).

## FE / client

1. Login as hospital user (`tenantCode` + Gmail + password) via auth-service.
2. Send `Authorization: Bearer <accessToken>` on every clinical API call.
3. Do **not** rely on `public` tables for patients/appointments/etc. — rows are written under `schemaName` from the token.

Super Admin tokens use `schemaName: public` and receive **403** on clinical APIs (hospital schema required).

## How it works

| Piece | Role |
| --- | --- |
| JWT claim `schemaName` | Source of truth for hospital schema |
| `TenantSchemaFilter` (common-service) | Parses Bearer JWT, sets `TenantContext`, rejects missing/`public` schema |
| `TenantAwareDataSource` | On each connection checkout: `SET search_path TO <hosp_*>` (else `public`) |
| `HibernatePropertiesCustomizer` | Removes `hibernate.default_schema` so SQL is not forced to `public.*` |
| Profile `tenant` | Activated by clinical `*Application` mains; loads `application-tenant.yml` |

## Services with routing enabled

patient, appointment, billing, doctor, medicine, therapist, attendance, file-upload, activity-log.

**Skipped:** auth-service (public), notification-service (shared/public for now).

## Config

`application-tenant.yml` (per clinical service):

```yaml
ayurveda:
  tenant:
    routing:
      enabled: true
      require-hospital-schema: true
auth:
  jwt:
    secret: ${JWT_SECRET:...}   # must match auth-service
```

Optional local cleanup (not required if code overrides are active): remove URL `currentSchema=public`, Hikari `connection-init-sql: SET search_path TO public`, and `hibernate.default_schema: public` from `application.yml`. **Do not commit DB passwords.**

## Test

1. Onboard/login hospital → note `tenant.schemaName` (e.g. `hosp_gan_dl`).
2. `POST` create patient on patient-service with Bearer token.
3. Verify row in `hosp_gan_dl.mst_patient` (not `public.mst_patient`).

## Gaps

- ADMS device paths (`/iclock/**`, `/cdata*`) skip JWT; device punches may still land in `public` until device→hospital mapping exists.
- Auth→activity-log without a hospital JWT (platform Super Admin) will not write to `hosp_*` activity_logs.
- Prefer env `JWT_SECRET` in deploy; keep the same value on auth and clinical services.

## Business codes

See [CODES.md](./CODES.md). Clinical generators read `tenantCode` from JWT → `TenantContext` (required when hospital schema routing is on). Patients no longer expose `patientDisplayId`.
