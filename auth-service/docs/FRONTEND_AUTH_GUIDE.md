# Ayurvedaa Auth — Frontend Integration Guide

Auth service base URL: `http://<host>:8111`  
API prefix: `/api/v1`

---

## 1. Login

### Super Admin (platform)
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "admin@gmail.com",
  "password": "YourPassword"
}
```
Do **not** send `tenantId` / `tenantCode`.

### Hospital user (admin / doctor / receptionist / custom role)
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "tenantCode": "HOSP01",
  "usernameOrEmail": "doctor@gmail.com",
  "password": "YourPassword"
}
```
Use either `tenantId` (UUID) **or** `tenantCode`.

### Rules
- Username = **Gmail only** (`@gmail.com`)
- Username is unique **per hospital**, not globally

### Login response (important fields)
```json
{
  "success": true,
  "data": {
    "accessToken": "<jwt>",
    "tokenType": "Bearer",
    "expiresInMs": 86400000,
    "user": {
      "id": "...",
      "username": "doctor@gmail.com",
      "email": "doctor@gmail.com",
      "fullName": "...",
      "role": "DOCTOR",
      "tenantRoleId": "...",
      "pageCodes": ["DASHBOARD", "PATIENTS", "APPOINTMENTS", "TREATMENTS"],
      "status": "ACTIVE"
    },
    "tenant": {
      "id": "...",
      "tenantCode": "HOSP01",
      "name": "...",
      "schemaName": "hosp_hosp01",
      "status": "ACTIVE"
    }
  }
}
```

**Store after login:** `accessToken`, `user`, `tenant`, especially `user.pageCodes`.

For APIs that need the current hospital/admin context, send:
```http
Authorization: Bearer <accessToken>
```

> Demo note: HTTP security is currently open (`permitAll`). Still send the Bearer token for role/user APIs so the backend knows which hospital and user is acting.

---

## 2. Page codes (sidebar + route access)

### What backend stores
Backend stores **permission codes**, not frontend URLs.

Catalog API:
```http
GET /api/v1/ui-pages
Authorization: Bearer <hospital-admin-token>
```

Seeded codes:

| pageCode       | UI meaning      |
|----------------|-----------------|
| DASHBOARD      | Dashboard       |
| PATIENTS       | Patients        |
| DOCTORS        | Doctors         |
| APPOINTMENTS   | Appointments    |
| TREATMENTS     | Treatments      |
| MEDICINES      | Medicines       |
| SALES          | Sales           |
| ACTIVITY_LOG   | Activity Log    |
| BILLING        | Billing         |
| SETTINGS       | Settings        |

### What frontend must do
Frontend owns the **code → route** map. Do **not** invent new codes.

Suggested mapping:

```ts
export const PAGE_ROUTES: Record<string, string[]> = {
  DASHBOARD: ["/dashboard"],
  PATIENTS: ["/patients", "/patients/:id"],
  DOCTORS: ["/doctors", "/doctors/:id"],
  APPOINTMENTS: ["/appointments"],
  TREATMENTS: ["/treatments"],
  MEDICINES: ["/medicines"],
  SALES: ["/sales"],
  ACTIVITY_LOG: ["/activity-log"],
  BILLING: ["/billing"],
  SETTINGS: ["/settings", "/settings/roles", "/settings/users"],
};
```

### After login
1. Read `user.pageCodes`
2. Show sidebar items only if code is present
3. Guard routes: allow only if matching `pageCode` is in `user.pageCodes`
4. `ADMIN` / `SUPER_ADMIN` receive **all** page codes from backend

### New screen later
1. Backend adds a new row in `ui_pages` (new `pageCode`)
2. Frontend adds that code to `PAGE_ROUTES` and menu
3. Hospital admin can then assign it to roles

---

## 3. Role Management (hospital admin)

Only **hospital ADMIN** (not platform Super Admin) manages roles for their hospital.

### List toggle options
```http
GET /api/v1/ui-pages
```

### Create role
```http
POST /api/v1/roles
Authorization: Bearer <hospital-admin-token>
Content-Type: application/json

{
  "roleName": "Nurse",
  "description": "Ward nurse access",
  "pageCodes": ["DASHBOARD", "PATIENTS", "APPOINTMENTS"],
  "active": true
}
```
- `roleCode` optional (auto-generated from `roleName` if omitted)
- `pageCodes` = modules toggled **On** in UI

### List / get / update / delete
```http
GET    /api/v1/roles
GET    /api/v1/roles/{roleId}
PUT    /api/v1/roles/{roleId}
DELETE /api/v1/roles/{roleId}
```

### Seeded system roles (already created per hospital)
- `HOSPITAL_ADMIN` — all pages
- `DOCTOR` — clinical pages
- `RECEPTIONIST` — front-desk pages

System roles **cannot be deleted**. Admin can still create extra custom roles.

---

## 4. User Management (hospital admin)

### Create user
```http
POST /api/v1/auth/register-user
Authorization: Bearer <hospital-admin-token>
Content-Type: application/json

{
  "username": "nurse@gmail.com",
  "fullName": "Anita Nurse",
  "password": "Password@123",
  "role": "RECEPTIONIST",
  "tenantRoleId": "<uuid-from-GET-/roles>"
}
```

- `username` = Gmail
- `role` = system authority (`ADMIN`, `DOCTOR`, `RECEPTIONIST`, …) — **not** Super Admin
- `tenantRoleId` = custom/hospital role that carries `pageCodes`  
  - Required for non-admin users  
  - Optional for `ADMIN` (defaults to `HOSPITAL_ADMIN` template)

### List / get / update / status / delete
```http
GET    /api/v1/auth/users
GET    /api/v1/auth/users/paged?page=0&size=20
GET    /api/v1/auth/users/{userId}
PUT    /api/v1/auth/users/{userId}
PUT    /api/v1/auth/users/{userId}/status
DELETE /api/v1/auth/users/{userId}
```

Status values: `ACTIVE` | `INACTIVE` | `LOCKED`

---

## 5. Profile / password

```http
GET  /api/v1/auth/me
PUT  /api/v1/auth/me                    // update fullName
PUT  /api/v1/auth/change-password
POST /api/v1/auth/forgot-password       // Super Admin: Gmail only; Hospital: tenant + Gmail
POST /api/v1/auth/reset-password
```

Public signup is **disabled**. Accounts are created by hospital admin (or Super Admin creates hospital admins).

---

## 6. Platform Super Admin (hospital onboarding)

```http
POST /api/v1/platform/bootstrap-super-admin   // one-time
POST /api/v1/platform/hospitals               // onboard hospital + schema
GET  /api/v1/platform/hospitals
GET  /api/v1/platform/hospitals/{hospitalId}
POST /api/v1/platform/hospitals/{hospitalId}/admins
GET  /api/v1/platform/hospitals/{hospitalId}/admins
PUT  /api/v1/platform/hospitals/{hospitalId}/status
POST /api/v1/platform/hospitals/{hospitalId}/retry-provision
```

---

## 7. Frontend checklist

- [ ] Login screens: Super Admin vs Hospital (tenant field)
- [ ] Save token + `pageCodes` after login
- [ ] Sidebar filtered by `pageCodes`
- [ ] Route guards by `pageCodes` (frontend map above)
- [ ] Role Management page: load `ui-pages`, create/edit roles with toggles
- [ ] User Management: create users with `tenantRoleId`
- [ ] Settings screens require `SETTINGS` page code (or ADMIN)
- [ ] Do not hardcode routes into API payloads — only send `pageCodes`

---

## 8. Quick mental model

```text
ui_pages (DB)     = DASHBOARD, PATIENTS, SETTINGS, ...
tenant_role_pages = which pages a role can open
login response    = pageCodes for this user
frontend          = pageCode → /routes + menu visibility
```

**DB saves codes. Frontend maps codes to routes.**
