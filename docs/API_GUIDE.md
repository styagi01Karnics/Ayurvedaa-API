# Ayurveda API Guide (Step-1 / Step-2)

Master index for all HTTP APIs in this monorepo. Source of truth: live `*Controller.java` classes and DTOs. **attendance-service** and ADMS/iClock endpoints are **out of scope**.

| Related doc | Purpose |
| --- | --- |
| [CODES.md](./CODES.md) | Business codes `{tenantCode}-{TYPE}-{#####}` |
| [TENANT_SCHEMA_WIRING.md](./TENANT_SCHEMA_WIRING.md) | JWT `schemaName` → `hosp_*` routing |
| [auth-service/docs/AUTH_API_GUIDE.md](../auth-service/docs/AUTH_API_GUIDE.md) | Deep auth / platform / roles reference |
| [FRONTEND_PAGE_PERMISSIONS.md](./FRONTEND_PAGE_PERMISSIONS.md) | React: pageCodes → sidebar / routes |

---

## Ports (localhost)

| Service | Port | Base URL |
| --- | ---: | --- |
| patient-service | 8101 | `http://localhost:8101` |
| doctor-service | 8102 | `http://localhost:8102` |
| appointment-service | 8103 | `http://localhost:8103` |
| therapist-service | 8104 | `http://localhost:8104` |
| file-upload-service | 8105 | `http://localhost:8105` |
| activity-log-service | 8107 | `http://localhost:8107` |
| medicine-service | 8108 | `http://localhost:8108` |
| billing-service | 8109 | `http://localhost:8109` |
| notification-service | 8110 | `http://localhost:8110` |
| auth-service | 8111 | `http://localhost:8111` |

No servlet `context-path`. API prefix is `/api/v1` on every service.

---

## Shared conventions

### JWT header

```http
Authorization: Bearer <accessToken>
```

Hospital JWTs include `tenantCode`, `schemaName` (e.g. `hosp_gan_dl`), roles, and `pageCodes`. Clinical services route writes into that hospital schema — see [TENANT_SCHEMA_WIRING.md](./TENANT_SCHEMA_WIRING.md). Super Admin tokens use `schemaName: public` and get **403** on clinical APIs.

### Response envelope

All JSON APIs return `com.ayurveda.common.ApiResponse<T>`:

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {}
}
```

Failure:

```json
{
  "success": false,
  "status": 400,
  "message": "…",
  "data": null
}
```

Exception: `GET /api/v1/documents/{documentId}/download` returns a file `Resource` stream (not `ApiResponse`).

### Business codes

See [CODES.md](./CODES.md). Examples: `GAN-DL-PT-00001`, `GAN-DL-DOC-00001`, `GAN-DL-INV-00001`.

---

## Step-1 — Platform & access (auth)

**Service:** auth-service `:8111` · Guide: [api/auth-service.md](./api/auth-service.md) · Deep: [AUTH_API_GUIDE.md](../auth-service/docs/AUTH_API_GUIDE.md)

| Step | Action | Endpoint |
| --- | --- | --- |
| 1 | Bootstrap Super Admin (once) | `POST /api/v1/platform/bootstrap-super-admin` |
| 2 | Super Admin login (no `tenantCode`) | `POST /api/v1/auth/login` |
| 3 | Onboard hospital + first admin | `POST /api/v1/platform/hospitals` |
| 4 | Hospital admin login (`tenantCode` + Gmail + password) | `POST /api/v1/auth/login` |
| 5 | Roles / FE `pageCodes` | `GET /api/v1/ui-pages`, `POST/GET/PUT/DELETE /api/v1/roles` — FE map: [FRONTEND_PAGE_PERMISSIONS.md](./FRONTEND_PAGE_PERMISSIONS.md) |
| 6 | Register hospital staff | `POST /api/v1/auth/register-user` |
| 7 | Change / forgot password | `PUT /api/v1/auth/change-password`, `POST …/forgot-password`, `POST …/reset-password` |

Onboard auto-creates `tenantCode` (e.g. `GAN-DL`), PostgreSQL schema `hosp_*`, and runs hospital DDL. Clinical data for that hospital lives only in that schema.

**Login identity:** Gmail only. Field name remains `usernameOrEmail` for FE compatibility.

---

## Step-2 — Hospital clinical operations

After hospital login, send the Bearer token on every clinical call. Typical FE order:

1. **Upload** documents if needed → [file-upload-service](./api/file-upload-service.md)
2. **Masters** — doctors, therapists, medicines, therapy categories / therapies, consultation types, doshas, packages → doctor / therapist / medicine / appointment / billing guides
3. **Patients** → [patient-service](./api/patient-service.md)
4. **Appointments** (+ assessments, prescriptions, therapies) → [appointment-service](./api/appointment-service.md)
5. **Billing / invoices** → [billing-service](./api/billing-service.md)
6. **Activity logs / notifications** → [activity-log-service](./api/activity-log-service.md), [notification-service](./api/notification-service.md)

---

## Per-service guides

| Guide | Port | ~Endpoints | Step |
| --- | ---: | ---: | --- |
| [auth-service.md](./api/auth-service.md) | 8111 | ~30 | Step-1 |
| [patient-service.md](./api/patient-service.md) | 8101 | 5 | Step-2 |
| [doctor-service.md](./api/doctor-service.md) | 8102 | 6 | Step-2 |
| [appointment-service.md](./api/appointment-service.md) | 8103 | ~71 | Step-2 |
| [therapist-service.md](./api/therapist-service.md) | 8104 | 7 | Step-2 |
| [file-upload-service.md](./api/file-upload-service.md) | 8105 | 4 | Step-2 |
| [activity-log-service.md](./api/activity-log-service.md) | 8107 | 3 | Step-2 |
| [medicine-service.md](./api/medicine-service.md) | 8108 | 14 | Step-2 |
| [billing-service.md](./api/billing-service.md) | 8109 | 24 | Step-2 |
| [notification-service.md](./api/notification-service.md) | 8110 | 8 | Step-2 (shared) |

**Skipped:** attendance-service (8106), ADMS/iClock. **common-service** has no HTTP controllers (shared library only).

**Approximate total documented endpoints:** ~172 (auth ~30 + clinical ~142).
