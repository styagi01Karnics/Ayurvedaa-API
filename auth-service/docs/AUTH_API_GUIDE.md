# Auth API Guide

Complete frontend reference for **auth-service** login, signup/onboarding, password flows, and closely related auth APIs.

**Source of truth:** controllers under `com.ayurveda.auth.controller`, DTOs, `SecurityConfig`, `application.yml`.  
**Do not use removed legacy endpoints** such as public `/auth/signup` or `/tenants/register`.

---

## Base URL

| Item | Value |
|------|--------|
| Service | `auth-service` |
| Port | `8111` |
| Local base | `http://localhost:8111` |
| API prefix | `/api/v1` |
| Swagger UI | `http://localhost:8111/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8111/api-docs` |

**JWT header (all protected routes):**

```http
Authorization: Bearer <accessToken>
```

There is **no** servlet `context-path` — paths below are absolute from the host root.

---

## Response envelope

All endpoints return `com.ayurveda.common.ApiResponse<T>`:

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": { }
}
```

| Field | Type | Notes |
|-------|------|--------|
| `success` | boolean | `true` on success |
| `status` | int | HTTP-style status in body (often `200`) |
| `message` | string | Human-readable result message |
| `data` | T \| null | Payload; `null` for some void successes |

Failure shape (errors):

```json
{
  "success": false,
  "status": 400,
  "message": "…",
  "data": null
}
```

---

## Data model (brief)

| Table | Purpose |
|-------|---------|
| `tenants` | Hospitals + platform tenant. Onboard stores **all** clinic + contact form fields here (except passwords). Includes `tenantCode`, `schemaName`, address, `logoUrl`, contact `fullName` / `mobileNumber` / `email` / `photoUrl`, status. |
| `auth_users` | Login accounts. Canonical identity is **email only** (Gmail). Onboard stores `fullName`, `mobileNumber`, `email`, `password` → `passwordHash`, role `ADMIN`. Unique per `(tenant_id, email)`. No mirrored `username` column. |
| `tenant_roles` / `ui_pages` | Hospital Role Management (page codes). Seeded system roles on onboard. |
| `password_reset_tokens` | Hashed tokens for forgot/reset password. |

**Login identity = Gmail only** (`^[A-Za-z0-9._%+-]+@gmail\.com$`). Login/forgot request field is still named `usernameOrEmail` for FE compatibility but must be the Gmail address.

**tenantCode auto-generation:** `BRAND-STATE` from `clinicName` + `state` (name or code), e.g. `Ganesha Ayurveda` + `Delhi` → `GAN-DL`. Schema → `hosp_gan_dl`. If that code (or its schema) already exists, a numeric suffix is appended until free: `GAN-DL-2`, `GAN-DL-3`, … (schema follows, e.g. `hosp_gan_dl_2`). DB unique constraints on `tenantCode` / `schemaName` remain. Platform tenant uses schema `public`.

**Hospital schema provision (step-2):** Onboard / retry-provision runs `CREATE SCHEMA` then `HospitalSchemaMigrator`, which applies ordered classpath scripts under `auth-service/src/main/resources/db/hospital-schema/*.sql`. Scripts are recorded in `${schema}.schema_migrations` (idempotent on retry). Tables created per `hosp_*` schema:

| Script | Tables |
|--------|--------|
| `V000__baseline.sql` | `schema_baseline`, `schema_migrations` |
| `V001__masters.sql` | `mst_patient`, `mst_doctor`, `mst_therapist`, `mst_therapist_assigned_therapies`, `medicine_inventory`, `mst_treatment_category`, `mst_therapy`, `mst_doshas`, `mst_consultation_type`, `mst_treatment_plan`, `mst_package` |
| `V002__appointments.sql` | `appointment_bookings`, `appointment_consultation_types`, `treatments`, `follow_ups`, `appointment_therapies`, `appointment_therapy_recommendations`, `prescriptions`, `prescription_medicines`, `prescription_therapy_suggestions`, `prescription_therapy_suggestion_items`, `appointment_medical_histories`, `appointment_physical_examinations`, `appointment_systemic_examinations`, `appointment_ayurvedic_assessments`, `appointment_lifestyle_information`, `appointment_treatment_plans` |
| `V003__billing.sql` | `billings`, `billing_service_items`, `billing_invoices`, `billing_invoice_items`, `billing_invoice_payments`, `patient_packages` |
| `V004__ops.sql` | `attendances`, `employee_attendance_master`, `device_attendance_logs`, `appointment_documents`, `activity_logs`, `notifications` |

Auth platform tables (`tenants`, `auth_users`, `tenant_roles`, …) stay in **`public`**. Domain services still need tenant `search_path` / schema routing wired separately so they read/write these `hosp_*` tables instead of `public`.

---

## Who logs in how

| Actor | Login body | Notes |
|-------|------------|--------|
| Platform Super Admin | `usernameOrEmail` + `password` | **Omit** `tenantCode` |
| Hospital Admin / Doctor / etc. | `tenantCode` + `usernameOrEmail` + `password` | `tenantCode` required (e.g. `GAN-DL`) |

Public self-signup is **removed**. Hospital accounts are created by Super Admin onboard / create-admin, or by Hospital Admin via `register-user`.

---

## Procedures (step-by-step)

### 1. Platform Super Admin bootstrap (first-time)

One-time, public, until a `SUPER_ADMIN` already exists.

1. Call `POST /api/v1/platform/bootstrap-super-admin` with Gmail + password + fullName.
2. Store nothing yet (no JWT returned) — then log in (procedure 2).
3. If Super Admin already exists → `400` (“Bootstrap is disabled”).

**Order:** Bootstrap → Super Admin login → onboard hospitals.

---

### 2. Super Admin login (no tenantCode)

1. `POST /api/v1/auth/login` with Gmail + password only (no `tenantCode`, no `tenantId`).
2. Save `data.accessToken`, `data.user`, `data.tenant` (platform tenant).
3. Use `Authorization: Bearer <token>` for all `/api/v1/platform/**` calls.

---

### 3. Hospital + admin onboard / signup

Figma **Clinic Information + Contact Information** → single API.

1. Super Admin must be logged in (Bearer).
2. Optionally upload logo/photo (**upload-then-URL** — see Logo / photo flow below), then pass returned HTTPS URLs as `logoUrl` / `photoUrl`.
3. `POST /api/v1/platform/hospitals` with clinic + contact fields (see API reference).
4. Backend:
   - Generates unique `tenantCode` (`BRAND-STATE`, or `BRAND-STATE-N` on collision) and `schemaName` (`hosp_…`)
   - Writes clinic + contact fields onto `tenants` (passwords excluded), including `logoUrl` / `photoUrl`
   - Creates Postgres schema, applies hospital-schema baseline scripts, seeds hospital roles
   - Creates first hospital `ADMIN` on `auth_users` (`email` = Gmail only)
5. Response includes `hospital` + `admin`. Give the hospital their `tenantCode` for login.
6. Additional admins later: `POST /api/v1/platform/hospitals/{hospitalId}/admins`.
7. Edit clinic/contact later: `PUT /api/v1/platform/hospitals/{hospitalId}` (does **not** change `tenantCode`).

**Do not send:** `tenantId`, `tenantCode`, or a separate `userId`. Map UI “User ID” → `email`.

---

### Logo / photo upload-then-URL (FE)

Auth **never** accepts multipart files. Pass **URLs only** on onboard / hospital profile update.

1. Upload the image somewhere that returns a stable HTTPS URL.
2. Put that URL in `logoUrl` (clinic) and/or `photoUrl` (contact) on:
   - `POST /api/v1/platform/hospitals`
   - `PUT /api/v1/platform/hospitals/{hospitalId}`
3. Values are stored on `tenants.logoUrl` / `tenants.photoUrl`. Omitting them is fine (null).

**file-upload-service today:** port `8105`, APIs under `/api/v1/documents/**` are **patient appointment documents** (`patientId` + `DocumentType` required). That is **not** a general hospital logo/photo CDN. Prefer:

- External CDN / object storage / static host for logos & admin photos, **or**
- A future generic media upload endpoint

Do **not** break URL-based onboard: FE can always paste a public URL without calling file-upload-service.

---

### 4. Hospital user login (tenantCode + email + password)

1. `POST /api/v1/auth/login` with `tenantCode`, Gmail as `usernameOrEmail`, and `password`.
2. Save `accessToken`, `user` (especially `pageCodes`, `role`, `id`), and `tenant` (`id`, `tenantCode`, `name`, `schemaName`).
3. Send Bearer token on subsequent calls.

---

### 5. Forgot / reset password

1. **Forgot:** `POST /api/v1/auth/forgot-password`
   - Super Admin: Gmail only (omit `tenantCode`)
   - Hospital: `tenantCode` + Gmail
2. Auth generates a hashed reset token, then asks **notification-service** (`POST /api/v1/notifications/email`) to email a reset link to the user's Gmail.
3. **API response does not include `resetToken` in production-like config.** For local/dev only, set `auth.reset-password.expose-token=true` (default in local `application.yml`) to also return `resetToken` + `expiresAt`.
4. **Reset:** `POST /api/v1/auth/reset-password` with `token` (from email link or exposed local response), `newPassword`, `confirmPassword`.
5. User logs in again with the new password.

Email link base URL: `auth.reset-password.reset-link-base-url` (default `http://localhost:3000/reset-password?token=…`).

---

### 6. Change password (authenticated)

1. User must be logged in.
2. `PUT /api/v1/auth/change-password` with `currentPassword`, `newPassword`, `confirmPassword`.
3. Continue using the existing JWT (token is not rotated by this call).

---

### 7. Typical hospital setup after onboard (optional)

1. Hospital Admin logs in with `tenantCode` + admin Gmail.
2. `GET /api/v1/ui-pages` → build Role Management toggles.
3. `POST /api/v1/roles` → custom roles with `pageCodes`.
4. `POST /api/v1/auth/register-user` → create staff (Gmail + role + `tenantRoleId` for non-ADMIN).

---

## API reference

### Auth — public

#### POST `/api/v1/auth/login`

**Full URL:** `http://localhost:8111/api/v1/auth/login`  
**Auth:** Public  
**When:** Super Admin or hospital user sign-in.

**Request — Super Admin:**

```json
{
  "usernameOrEmail": "superadmin@gmail.com",
  "password": "YourPassword@123"
}
```

**Request — Hospital user:**

```json
{
  "tenantCode": "GAN-DL",
  "usernameOrEmail": "rahul.sharma@gmail.com",
  "password": "Password@123"
}
```

| Field | Required | Notes |
|-------|----------|--------|
| `tenantCode` | Hospital only | Omit for Super Admin. No `tenantId`. |
| `usernameOrEmail` | Yes | Gmail |
| `password` | Yes | |

**Response** (`ApiResponse<AuthTokenResponse>`):

```json
{
  "success": true,
  "status": 200,
  "message": "Login successful.",
  "data": {
    "accessToken": "<jwt>",
    "tokenType": "Bearer",
    "expiresInMs": 86400000,
    "user": {
      "id": "…",
      "tenantId": "…",
      "tenantCode": "GAN-DL",
      "schemaName": "hosp_gan_dl",
      "email": "rahul.sharma@gmail.com",
      "fullName": "Rahul Sharma",
      "mobileNumber": "9876543210",
      "role": "ADMIN",
      "tenantRoleId": "…",
      "tenantRoleCode": "HOSPITAL_ADMIN",
      "tenantRoleName": "Hospital Admin",
      "pageCodes": ["DASHBOARD", "PATIENTS", "DOCTORS", "APPOINTMENTS", "TREATMENTS", "MEDICINES", "SALES", "ACTIVITY_LOG", "BILLING", "SETTINGS"],
      "status": "ACTIVE"
    },
    "tenant": {
      "id": "…",
      "tenantCode": "GAN-DL",
      "name": "Ganesha Ayurveda",
      "clinicType": "Ayurveda Clinic",
      "state": "Delhi",
      "stateCode": "DL",
      "city": "New Delhi",
      "pinCode": "110001",
      "addressLine1": "12 Green Park",
      "addressLine2": "Near Metro Station",
      "registrationNumberGst": "07AAAAA0000A1Z5",
      "logoUrl": "https://cdn.example.com/hospitals/gan-dl/logo.png",
      "fullName": "Rahul Sharma",
      "mobileNumber": "9876543210",
      "email": "rahul.sharma@gmail.com",
      "photoUrl": "https://cdn.example.com/admins/rahul.png",
      "schemaName": "hosp_gan_dl",
      "platform": false,
      "status": "ACTIVE",
      "provisionMessage": "…"
    }
  }
}
```

---

#### POST `/api/v1/auth/forgot-password`

**Full URL:** `http://localhost:8111/api/v1/auth/forgot-password`  
**Auth:** Public

**Request — Super Admin:**

```json
{
  "usernameOrEmail": "superadmin@gmail.com"
}
```

**Request — Hospital:**

```json
{
  "tenantCode": "GAN-DL",
  "usernameOrEmail": "doctor@gmail.com"
}
```

**Response** (`ApiResponse<ForgotPasswordResponse>`):

When `auth.reset-password.expose-token=true` (local/dev):

```json
{
  "success": true,
  "status": 200,
  "message": "If an account exists, a password reset email has been sent.",
  "data": {
    "message": "If an account exists, a password reset email has been sent.",
    "resetToken": "<opaque-token>",
    "expiresAt": "2026-08-31T15:30:00"
  }
}
```

When `expose-token=false` (production-like): same message, but **`resetToken` is omitted** (email only). `expiresAt` may still be present.

If no matching account, still `success: true` with a generic message and no `resetToken` (anti-enumeration). Email delivery failures are logged and do not change this response shape.

---

#### POST `/api/v1/auth/reset-password`

**Full URL:** `http://localhost:8111/api/v1/auth/reset-password`  
**Auth:** Public

**Request:**

```json
{
  "token": "<resetToken-from-forgot>",
  "newPassword": "NewPass@123",
  "confirmPassword": "NewPass@123"
}
```

**Response:**

```json
{
  "success": true,
  "status": 200,
  "message": "Password reset successful.",
  "data": null
}
```

---

#### POST `/api/v1/auth/validate`

**Full URL:** `http://localhost:8111/api/v1/auth/validate`  
**Auth:** Public (token optional in header; used by other services)  
**When:** Gateway / microservices verify a JWT.

```http
POST /api/v1/auth/validate
Authorization: Bearer <accessToken>
```

**Response** (`ApiResponse<TokenValidationResponse>`):

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "valid": true,
    "userId": "…",
    "tenantId": "…",
    "tenantCode": "GAN-DL",
    "schemaName": "hosp_gan_dl",
    "email": "rahul.sharma@gmail.com",
    "role": "ADMIN",
    "tenantRoleId": "…",
    "pageCodes": ["DASHBOARD", "PATIENTS"]
  }
}
```

---

### Auth — authenticated (self)

#### GET `/api/v1/auth/me`

**Auth:** Bearer  
**Response:** `ApiResponse<UserResponse>` (same user shape as login).

---

#### PUT `/api/v1/auth/me`

**Auth:** Bearer  

**Request:**

```json
{
  "fullName": "Updated Name"
}
```

**Response:** `ApiResponse<UserResponse>` — message `"Profile updated successfully."`

---

#### PUT `/api/v1/auth/change-password`

**Auth:** Bearer  
**When:** Logged-in user changes password.

**Request:**

```json
{
  "currentPassword": "OldPass@123",
  "newPassword": "NewPass@123",
  "confirmPassword": "NewPass@123"
}
```

**Response:**

```json
{
  "success": true,
  "status": 200,
  "message": "Password changed successfully.",
  "data": null
}
```

---

#### GET `/api/v1/auth/tenant`

**Auth:** Bearer  
**Response:** `ApiResponse<TenantResponse>` for the caller’s current tenant.

---

### Platform (Super Admin)

All hospital management routes below require **Bearer + role SUPER_ADMIN** (enforced in service via `requireSuperAdmin()`). Bootstrap is the only public platform route.

#### POST `/api/v1/platform/bootstrap-super-admin`

**Full URL:** `http://localhost:8111/api/v1/platform/bootstrap-super-admin`  
**Auth:** Public (one-time)  
**HTTP status:** `201` on success

**Request:**

```json
{
  "fullName": "Platform Owner",
  "email": "superadmin@gmail.com",
  "password": "Password@123"
}
```

**Response** (`ApiResponse<UserResponse>`):

```json
{
  "success": true,
  "status": 200,
  "message": "Platform super admin bootstrapped successfully.",
  "data": {
    "id": "…",
    "tenantId": "…",
    "tenantCode": "…",
    "schemaName": "public",
    "email": "superadmin@gmail.com",
    "fullName": "Platform Owner",
    "mobileNumber": null,
    "role": "SUPER_ADMIN",
    "tenantRoleId": null,
    "tenantRoleCode": null,
    "tenantRoleName": null,
    "pageCodes": ["DASHBOARD", "PATIENTS", "DOCTORS", "APPOINTMENTS", "TREATMENTS", "MEDICINES", "SALES", "ACTIVITY_LOG", "BILLING", "SETTINGS"],
    "status": "ACTIVE"
  }
}
```

---

#### POST `/api/v1/platform/hospitals`

**Full URL:** `http://localhost:8111/api/v1/platform/hospitals`  
**Auth:** Bearer + **SUPER_ADMIN**  
**HTTP status:** `201`  
**When:** Figma clinic + contact signup / hospital onboard.

**Request** (`OnboardHospitalRequest`):

```json
{
  "clinicName": "Ganesha Ayurveda",
  "clinicType": "Ayurveda Clinic",
  "state": "Delhi",
  "city": "New Delhi",
  "pinCode": "110001",
  "addressLine1": "12 Green Park",
  "addressLine2": "Near Metro Station",
  "registrationNumberGst": "07AAAAA0000A1Z5",
  "logoUrl": "https://cdn.example.com/hospitals/gan-dl/logo.png",
  "fullName": "Rahul Sharma",
  "mobileNumber": "9876543210",
  "email": "rahul.sharma@gmail.com",
  "password": "Password@123",
  "confirmPassword": "Password@123",
  "photoUrl": "https://cdn.example.com/admins/rahul.png"
}
```

| Field | Required | Storage |
|-------|----------|---------|
| `clinicName` | Yes | `tenants.name` |
| `clinicType` | No | `tenants` |
| `state` | Yes | `tenants` (+ derived `stateCode`) |
| `city`, `pinCode`, `addressLine1`, `addressLine2`, `registrationNumberGst` | No | `tenants` |
| `logoUrl` | No | `tenants` |
| `fullName` | Yes | `tenants` + `auth_users` |
| `mobileNumber` | No | `tenants` + `auth_users` |
| `email` | Yes | Gmail; `tenants` + `auth_users.email` (login) |
| `password` / `confirmPassword` | Yes | Hashed on `auth_users` only |
| `photoUrl` | No | `tenants` only |

**tenantCode examples:**

| clinicName | state | tenantCode | schemaName |
|------------|-------|------------|------------|
| Ganesha Ayurveda | Delhi / DL | `GAN-DL` | `hosp_gan_dl` |
| Ganesha Ayurveda (2nd in Delhi) | Delhi / DL | `GAN-DL-2` | `hosp_gan_dl_2` |
| Ganesha Ayurveda | Uttarakhand / UK | `GAN-UK` | `hosp_gan_uk` |
| Ganesha Ayurveda | Odisha / OD | `GAN-OD` | `hosp_gan_od` |

**Response** (`ApiResponse<HospitalOnboardResponse>`):

```json
{
  "success": true,
  "status": 200,
  "message": "Hospital onboarded successfully. Schema provisioned.",
  "data": {
    "hospital": {
      "id": "…",
      "tenantCode": "GAN-DL",
      "name": "Ganesha Ayurveda",
      "clinicType": "Ayurveda Clinic",
      "state": "Delhi",
      "stateCode": "DL",
      "city": "New Delhi",
      "pinCode": "110001",
      "addressLine1": "12 Green Park",
      "addressLine2": "Near Metro Station",
      "registrationNumberGst": "07AAAAA0000A1Z5",
      "logoUrl": "https://cdn.example.com/hospitals/gan-dl/logo.png",
      "fullName": "Rahul Sharma",
      "mobileNumber": "9876543210",
      "email": "rahul.sharma@gmail.com",
      "photoUrl": "https://cdn.example.com/admins/rahul.png",
      "schemaName": "hosp_gan_dl",
      "platform": false,
      "status": "ACTIVE",
      "provisionMessage": "Schema hosp_gan_dl created. Hospital-schema scripts applied (V000__baseline.sql, V001__masters.sql, V002__appointments.sql, V003__billing.sql, V004__ops.sql). Clinical tables provisioned in hosp_gan_dl."
    },
    "admin": {
      "id": "…",
      "tenantId": "…",
      "tenantCode": "GAN-DL",
      "schemaName": "hosp_gan_dl",
      "email": "rahul.sharma@gmail.com",
      "fullName": "Rahul Sharma",
      "mobileNumber": "9876543210",
      "role": "ADMIN",
      "tenantRoleId": "…",
      "tenantRoleCode": "HOSPITAL_ADMIN",
      "tenantRoleName": "Hospital Admin",
      "pageCodes": ["DASHBOARD", "PATIENTS", "DOCTORS", "APPOINTMENTS", "TREATMENTS", "MEDICINES", "SALES", "ACTIVITY_LOG", "BILLING", "SETTINGS"],
      "status": "ACTIVE"
    }
  }
}
```

Hospital statuses: `PROVISIONING` | `ACTIVE` | `INACTIVE` | `SUSPENDED` | `FAILED`. Use `retry-provision` when `FAILED`.

---

#### GET `/api/v1/platform/hospitals`

**Auth:** Bearer + SUPER_ADMIN  
**Response:** `ApiResponse<List<TenantResponse>>`

---

#### GET `/api/v1/platform/hospitals/{hospitalId}`

**Auth:** Bearer + SUPER_ADMIN  
**Response:** `ApiResponse<TenantResponse>`

---

#### PUT `/api/v1/platform/hospitals/{hospitalId}`

**Full URL:** `http://localhost:8111/api/v1/platform/hospitals/{hospitalId}`  
**Auth:** Bearer + SUPER_ADMIN  
**When:** Edit hospital clinic/contact profile (same Figma fields as onboard except password).

**Request** (`UpdateHospitalRequest` — all fields optional; blank strings clear optional URL/text fields where applicable):

```json
{
  "clinicName": "Ganesha Ayurveda Updated",
  "clinicType": "Ayurveda Clinic",
  "state": "Delhi",
  "city": "New Delhi",
  "pinCode": "110001",
  "addressLine1": "12 Green Park",
  "addressLine2": "Near Metro Station",
  "registrationNumberGst": "07AAAAA0000A1Z5",
  "logoUrl": "https://cdn.example.com/hospitals/gan-dl/logo.png",
  "fullName": "Rahul Sharma",
  "mobileNumber": "9876543210",
  "email": "rahul.sharma@gmail.com",
  "photoUrl": "https://cdn.example.com/admins/rahul.png"
}
```

| Notes | |
|-------|--|
| Does **not** change `tenantCode` or `schemaName` (even if `state` changes display/`stateCode`) | |
| `email` updates `tenants.email` (contact) only — does **not** change `auth_users` login | |
| `logoUrl` / `photoUrl` are URL-only (see Logo / photo flow) | |

**Response:** `ApiResponse<TenantResponse>` — `"Hospital profile updated successfully."`

---

#### POST `/api/v1/platform/hospitals/{hospitalId}/admins`

**Auth:** Bearer + SUPER_ADMIN  
**HTTP status:** `201`  
**When:** Add another hospital admin (first admin is created by onboard).

**Request:**

```json
{
  "fullName": "Second Admin",
  "email": "admin2@gmail.com",
  "password": "Password@123"
}
```

(`hospitalId` in path is enough; optional body `hospitalId` is unused when path is set.)

**Response:** `ApiResponse<UserResponse>` — message `"Hospital admin created successfully."`

---

#### GET `/api/v1/platform/hospitals/{hospitalId}/admins`

**Auth:** Bearer + SUPER_ADMIN  
**Response:** `ApiResponse<List<UserResponse>>`

---

#### PUT `/api/v1/platform/hospitals/{hospitalId}/status`

**Auth:** Bearer + SUPER_ADMIN  

**Request:**

```json
{
  "status": "INACTIVE"
}
```

Manual values typically: `ACTIVE` | `INACTIVE` | `SUSPENDED`.

**Response:** `ApiResponse<TenantResponse>`

---

#### POST `/api/v1/platform/hospitals/{hospitalId}/retry-provision`

**Auth:** Bearer + SUPER_ADMIN  
**When:** Hospital `status` is `FAILED`.  
**Response:** `ApiResponse<TenantResponse>`

---

### Related — hospital user management

Requires **Bearer** and `ADMIN` / `SUPER_ADMIN` or authority `PAGE_SETTINGS`.

#### POST `/api/v1/auth/register-user`

**HTTP status:** `201`  
**When:** Hospital Admin creates staff under the current tenant (not public signup).

**Request:**

```json
{
  "fullName": "Anita Nurse",
  "email": "nurse@gmail.com",
  "password": "Password@123",
  "role": "RECEPTIONIST",
  "tenantRoleId": "<uuid-from-GET-/roles>"
}
```

| Field | Notes |
|-------|--------|
| `email` | Gmail login identity |
| `role` | `ADMIN`, `MANAGER`, `RECEPTIONIST`, `DIETICIAN`, `DOCTOR`, `CHEMIST` — not `SUPER_ADMIN` |
| `tenantRoleId` | Required for non-ADMIN (owns `pageCodes`) |

**Response:** `ApiResponse<UserResponse>` — `"User registered successfully."`

---

#### GET `/api/v1/auth/users`

**Auth:** Bearer + ADMIN / SUPER_ADMIN / PAGE_SETTINGS  
**Response:** `ApiResponse<List<UserResponse>>`

---

#### GET `/api/v1/auth/users/paged?page=0&size=20`

**Auth:** Bearer + ADMIN / SUPER_ADMIN / PAGE_SETTINGS  

**Response** (`ApiResponse<PagedResponse<UserResponse>>`):

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "content": [ ],
    "page": 0,
    "size": 20,
    "totalElements": 42,
    "totalPages": 3
  }
}
```

`page` is 0-based; default `size` 20.

---

#### GET `/api/v1/auth/users/{userId}`

**Auth:** Bearer + ADMIN / SUPER_ADMIN / PAGE_SETTINGS  
**Response:** `ApiResponse<UserResponse>`

---

#### PUT `/api/v1/auth/users/{userId}`

**Auth:** Bearer + ADMIN / SUPER_ADMIN / PAGE_SETTINGS  

**Request** (all optional):

```json
{
  "fullName": "New Name",
  "email": "newmail@gmail.com",
  "role": "DOCTOR",
  "tenantRoleId": "…",
  "status": "ACTIVE"
}
```

User status values: `ACTIVE` | `INACTIVE` | `LOCKED`.

---

#### PUT `/api/v1/auth/users/{userId}/status`

**Request:**

```json
{
  "status": "INACTIVE"
}
```

---

#### DELETE `/api/v1/auth/users/{userId}`

Soft-delete. Cannot delete self or modify Super Admin via these APIs.

---

### Related — roles & UI pages

**Auth:** Bearer + `ADMIN` / `SUPER_ADMIN` / `PAGE_SETTINGS` (class-level `@PreAuthorize` on `RoleController`).

Backend stores **page codes only**; frontend maps codes → routes.

Seeded catalog (`UiPageSeeder`):  
`DASHBOARD`, `PATIENTS`, `DOCTORS`, `APPOINTMENTS`, `TREATMENTS`, `MEDICINES`, `SALES`, `ACTIVITY_LOG`, `BILLING`, `SETTINGS`.

#### GET `/api/v1/ui-pages`

List toggleable modules for Role Management.

#### POST `/api/v1/roles`

```json
{
  "roleName": "Nurse",
  "description": "Ward nurse access",
  "pageCodes": ["DASHBOARD", "PATIENTS", "APPOINTMENTS"],
  "active": true
}
```

`roleCode` optional (auto from `roleName`). Send codes, never URLs.

#### GET `/api/v1/roles`  
#### GET `/api/v1/roles/{roleId}`  
#### PUT `/api/v1/roles/{roleId}`  
#### DELETE `/api/v1/roles/{roleId}`

System roles (e.g. `HOSPITAL_ADMIN`, `DOCTOR`, `RECEPTIONIST`) cannot be deleted.

**Role response shape** (`TenantRoleResponse`):

```json
{
  "id": "…",
  "tenantId": "…",
  "roleCode": "NURSE",
  "roleName": "Nurse",
  "description": "…",
  "systemRole": false,
  "active": true,
  "pageCodes": ["DASHBOARD", "PATIENTS"],
  "userCount": 3
}
```

---

## Security summary (`SecurityConfig`)

| Access | Paths |
|--------|--------|
| Public | `POST /api/v1/platform/bootstrap-super-admin`, `POST /api/v1/auth/login`, `forgot-password`, `reset-password`, `validate`, Swagger, Actuator health |
| Authenticated (Bearer) | Everything else |
| SUPER_ADMIN (service check) | All `/api/v1/platform/hospitals/**` |
| ADMIN / SUPER_ADMIN / PAGE_SETTINGS | User CRUD, roles, ui-pages |

---

## Endpoint index

| Method | Path | Auth |
|--------|------|------|
| POST | `/api/v1/platform/bootstrap-super-admin` | Public (once) |
| POST | `/api/v1/auth/login` | Public |
| POST | `/api/v1/auth/forgot-password` | Public |
| POST | `/api/v1/auth/reset-password` | Public |
| POST | `/api/v1/auth/validate` | Public |
| GET | `/api/v1/auth/me` | Bearer |
| PUT | `/api/v1/auth/me` | Bearer |
| PUT | `/api/v1/auth/change-password` | Bearer |
| GET | `/api/v1/auth/tenant` | Bearer |
| POST | `/api/v1/auth/register-user` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| GET | `/api/v1/auth/users` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| GET | `/api/v1/auth/users/paged` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| GET | `/api/v1/auth/users/{userId}` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| PUT | `/api/v1/auth/users/{userId}` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| PUT | `/api/v1/auth/users/{userId}/status` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| DELETE | `/api/v1/auth/users/{userId}` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| POST | `/api/v1/platform/hospitals` | Bearer + SUPER_ADMIN |
| GET | `/api/v1/platform/hospitals` | Bearer + SUPER_ADMIN |
| GET | `/api/v1/platform/hospitals/{hospitalId}` | Bearer + SUPER_ADMIN |
| PUT | `/api/v1/platform/hospitals/{hospitalId}` | Bearer + SUPER_ADMIN |
| POST | `/api/v1/platform/hospitals/{hospitalId}/admins` | Bearer + SUPER_ADMIN |
| GET | `/api/v1/platform/hospitals/{hospitalId}/admins` | Bearer + SUPER_ADMIN |
| PUT | `/api/v1/platform/hospitals/{hospitalId}/status` | Bearer + SUPER_ADMIN |
| POST | `/api/v1/platform/hospitals/{hospitalId}/retry-provision` | Bearer + SUPER_ADMIN |
| GET | `/api/v1/ui-pages` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| POST | `/api/v1/roles` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| GET | `/api/v1/roles` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| GET | `/api/v1/roles/{roleId}` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| PUT | `/api/v1/roles/{roleId}` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |
| DELETE | `/api/v1/roles/{roleId}` | Bearer + ADMIN/SUPER_ADMIN/PAGE_SETTINGS |

---


## Frontend: page permissions

Hospital UI access is controlled by **`pageCodes`** (strings from the seeded catalog). The backend stores codes only; the frontend maps each code → menu item + route and hides anything not in the user's list.

### What they are

| Source | Behavior |
|--------|----------|
| `SUPER_ADMIN` / hospital `ADMIN` | Always get **all** catalog `pageCodes` (`PageAccessService`) |
| Other hospital users | Codes from their assigned `tenantRole` (`tenant_role_pages`) |
| Inactive / missing tenant role | Empty list → no hospital modules |

JWT embeds the same list as claim `pageCodes`. On each request the filter turns each code into authority `PAGE_<CODE>` (e.g. `SETTINGS` → `PAGE_SETTINGS`). That authority (or role `ADMIN` / `SUPER_ADMIN`) is what guards Role Management and user CRUD APIs — see [Related — roles & UI pages](#related--roles--ui-pages) and Security summary.

### Where the FE gets them

1. **Login** — `POST /api/v1/auth/login` → `data.user.pageCodes` (also inside the JWT).
2. **Refresh profile** — `GET /api/v1/auth/me` → same `UserResponse` with **live** `pageCodes` from DB (use after an admin changes roles; JWT authorities stay stale until the user logs in again).
3. **Optional** — `POST /api/v1/auth/validate` returns `pageCodes` from the token only (not re-resolved).

Persist `pageCodes` with `accessToken` / session. Prefer `/auth/me` for menu rebuild after permission edits; require re-login if you need updated API authorities (`PAGE_SETTINGS`, etc.).

### Configure menus & routes

Map each code to a route/menu entry. Show only codes present in `user.pageCodes`. Do not invent codes — catalog is seeded by `UiPageSeeder`:

| `pageCode` | Seeded name | Module | Suggested FE route (example) |
|------------|-------------|--------|------------------------------|
| `DASHBOARD` | Dashboard | HOME | `/dashboard` |
| `PATIENTS` | Patients | CLINICAL | `/patients` |
| `DOCTORS` | Doctors | CLINICAL | `/doctors` |
| `APPOINTMENTS` | Appointments | CLINICAL | `/appointments` |
| `TREATMENTS` | Treatments | CLINICAL | `/treatments` |
| `MEDICINES` | Medicines | PHARMACY | `/medicines` |
| `SALES` | Sales | SALES | `/sales` |
| `ACTIVITY_LOG` | Activity Log | ADMIN | `/activity-log` |
| `BILLING` | Billing | BILLING | `/billing` |
| `SETTINGS` | Settings | ADMIN | `/settings` (Role Management lives here) |

**Route guard pattern:** if `!pageCodes.includes(requiredCode)` → redirect to dashboard or 403. Deep links must use the same check as the sidebar.

Onboard seeds system templates (`TenantBootstrapService`):

| Role code | Default pages |
|-----------|----------------|
| `HOSPITAL_ADMIN` | All catalog pages |
| `DOCTOR` | `DASHBOARD`, `PATIENTS`, `DOCTORS`, `APPOINTMENTS`, `TREATMENTS` |
| `RECEPTIONIST` | `DASHBOARD`, `PATIENTS`, `APPOINTMENTS`, `BILLING` |

Hospital users with enum role `ADMIN` already receive all pages regardless of template; custom staff use `tenantRoleId` + that role's `pageCodes`.

### How admins assign permissions

Full API shapes: [Related — roles & UI pages](#related--roles--ui-pages) and hospital user management. Flow:

1. **List modules** — `GET /api/v1/ui-pages` (Bearer + `ADMIN` / `SUPER_ADMIN` / `PAGE_SETTINGS`).
2. **Create / update role** with selected codes:

```http
POST /api/v1/roles
Authorization: Bearer <token>
```

```json
{
  "roleName": "Nurse",
  "description": "Ward nurse access",
  "pageCodes": ["DASHBOARD", "PATIENTS", "APPOINTMENTS"],
  "active": true
}
```

```http
PUT /api/v1/roles/{roleId}
```

```json
{
  "roleName": "Nurse",
  "description": "Updated",
  "pageCodes": ["DASHBOARD", "PATIENTS", "APPOINTMENTS", "BILLING"],
  "active": true
}
```

3. **Assign role to user** — `POST /api/v1/auth/register-user` or `PUT /api/v1/auth/users/{userId}` with `tenantRoleId` (required for non-`ADMIN`). Giving a role that includes `SETTINGS` grants `PAGE_SETTINGS` after next login, so that user can manage roles/users without being `ADMIN`.

System roles (`HOSPITAL_ADMIN`, `DOCTOR`, `RECEPTIONIST`) cannot be deleted; update their pages via `PUT /api/v1/roles/{roleId}` when allowed by product rules.

### Recommended FE pattern

1. On login: store `accessToken`, `user` (incl. `pageCodes`, `role`, `tenantRoleCode`), and `tenant`.
2. Build nav from `pageCodes` ∩ your route map; guard every protected route the same way.
3. **Super Admin** — omit `tenantCode` on login; all `pageCodes` returned; primary UI is platform hospital management (`/api/v1/platform/**`), not hospital Role Management.
4. **Hospital Admin (`role: ADMIN`)** — all hospital modules; use Role Management under Settings to create custom roles and register staff.
5. **Custom role user** — only assigned modules; if `SETTINGS` is included, Role/User APIs work after login (authority `PAGE_SETTINGS`).
6. After an admin changes someone's role pages: that user should call `GET /auth/me` (menus) and ideally **re-login** (JWT/`PAGE_*` authorities).

---
## Frontend checklist

- [ ] Two login UIs: Super Admin (no `tenantCode`) vs Hospital (`tenantCode` + Gmail)
- [ ] Persist `accessToken`, `user.pageCodes`, `tenant` — use `user.email` (no `username` field)
- [ ] Onboard form maps to `OnboardHospitalRequest` fields; UI “User ID” → `email`
- [ ] Logo/photo: upload elsewhere → pass HTTPS URLs only (`logoUrl` / `photoUrl`)
- [ ] No public signup; no `tenantId` in login/forgot bodies
- [ ] Map `pageCodes` → routes on the frontend only (see [Frontend: page permissions](#frontend-page-permissions))
- [ ] After onboard, hospital login uses returned `tenantCode` + same Gmail
- [ ] Forgot-password: prefer email link; only use API `resetToken` when local `expose-token=true`
- [ ] Hospital profile edit via `PUT /api/v1/platform/hospitals/{hospitalId}` (never change `tenantCode` from FE)
