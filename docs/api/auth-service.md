# auth-service API (Step-1)

| Item | Value |
| --- | --- |
| Port | `8111` |
| Base URL | `http://localhost:8111` |
| Prefix | `/api/v1` |
| Swagger | `http://localhost:8111/swagger-ui.html` |
| Deep guide | [AUTH_API_GUIDE.md](../../auth-service/docs/AUTH_API_GUIDE.md) |

**Auth:** public routes listed below; everything else needs `Authorization: Bearer <accessToken>`. Platform hospital APIs require **SUPER_ADMIN**. User/role APIs need **ADMIN**, **SUPER_ADMIN**, or authority **PAGE_SETTINGS**.

**Removed (do not call):** public signup, `/api/v1/tenants/register`, `TenantController`.

---

## Step-1 procedures

### 1. Bootstrap Super Admin (first time only)

```http
POST http://localhost:8111/api/v1/platform/bootstrap-super-admin
Content-Type: application/json
```

```json
{
  "fullName": "Platform Admin",
  "email": "superadmin@gmail.com",
  "password": "SecurePass1"
}
```

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "id": "…",
    "email": "superadmin@gmail.com",
    "fullName": "Platform Admin",
    "role": "SUPER_ADMIN",
    "status": "ACTIVE"
  }
}
```

**Note:** No JWT returned. If Super Admin already exists → `400`. Next: login.

### 2. Super Admin login

```http
POST http://localhost:8111/api/v1/auth/login
```

```json
{
  "usernameOrEmail": "superadmin@gmail.com",
  "password": "SecurePass1"
}
```

Omit `tenantCode`. Save `data.accessToken`.

### 3. Onboard hospital + admin

```http
POST http://localhost:8111/api/v1/platform/hospitals
Authorization: Bearer <superAdminToken>
```

```json
{
  "clinicName": "Ganesha Ayurveda",
  "clinicType": "CLINIC",
  "state": "Delhi",
  "city": "New Delhi",
  "pinCode": "110001",
  "addressLine1": "12 Main Road",
  "fullName": "Hospital Admin",
  "mobileNumber": "9876543210",
  "email": "admin@gmail.com",
  "password": "SecurePass1",
  "confirmPassword": "SecurePass1",
  "logoUrl": "https://cdn.example.com/logo.png"
}
```

Response `data` includes `hospital` (`tenantCode`, `schemaName`, status) and `admin`. Tenant schema is provisioned automatically (`hosp_gan_dl`, etc.).

### 4. Hospital login

```json
{
  "tenantCode": "GAN-DL",
  "usernameOrEmail": "admin@gmail.com",
  "password": "SecurePass1"
}
```

Use this JWT for all Step-2 clinical APIs.

### 5. Roles & page permissions

1. `GET /api/v1/ui-pages` — FE page catalog (`pageCode`, `pageName`, …)
2. `POST /api/v1/roles` — create role with `pageCodes`
3. `POST /api/v1/auth/register-user` — staff with `tenantRoleId`

### 6. Passwords

- Change (Bearer): `PUT /api/v1/auth/change-password`
- Forgot (public): `POST /api/v1/auth/forgot-password` → email / optional `resetToken`
- Reset (public): `POST /api/v1/auth/reset-password` with `token`, `newPassword`, `confirmPassword`

---

## Endpoints (~30)

### Auth — `/api/v1/auth`

| Method | Path | Auth | Body / params | Response `data` | Note |
| --- | --- | --- | --- | --- | --- |
| POST | `/login` | Public | `tenantCode?`, `usernameOrEmail`, `password` | `AuthTokenResponse` | Super: no tenantCode; hospital: required |
| POST | `/forgot-password` | Public | `tenantCode?`, `usernameOrEmail` | `ForgotPasswordResponse` | Emails reset link |
| POST | `/reset-password` | Public | `token`, `newPassword`, `confirmPassword` | `null` | Completes reset |
| POST | `/validate` | Public | optional Bearer | `TokenValidationResponse` | S2S JWT check |
| POST | `/register-user` | Bearer + admin/settings | `fullName`, `email`, `password`, `role`, `tenantRoleId?` | `UserResponse` | Staff under current tenant |
| GET | `/me` | Bearer | — | `UserResponse` | Current user |
| PUT | `/me` | Bearer | `fullName?` | `UserResponse` | Own profile |
| PUT | `/change-password` | Bearer | `currentPassword`, `newPassword`, `confirmPassword` | `null` | Does not rotate JWT |
| GET | `/users` | Bearer + admin/settings | — | `List<UserResponse>` | Tenant users |
| GET | `/users/paged` | Bearer + admin/settings | `page`, `size` | `PagedResponse<UserResponse>` | Paginated |
| GET | `/users/{userId}` | Bearer + admin/settings | path UUID | `UserResponse` | |
| PUT | `/users/{userId}` | Bearer + admin/settings | `fullName?`, `email?`, `role?`, `tenantRoleId?`, `status?` | `UserResponse` | |
| PUT | `/users/{userId}/status` | Bearer + admin/settings | `status` | `UserResponse` | |
| DELETE | `/users/{userId}` | Bearer + admin/settings | — | `null` | Soft-delete |
| GET | `/tenant` | Bearer | — | `TenantResponse` | Current hospital/platform |

**Login response example:**

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "accessToken": "eyJ…",
    "tokenType": "Bearer",
    "expiresInMs": 86400000,
    "user": {
      "id": "…",
      "tenantId": "…",
      "tenantCode": "GAN-DL",
      "schemaName": "hosp_gan_dl",
      "email": "admin@gmail.com",
      "fullName": "Hospital Admin",
      "role": "ADMIN",
      "pageCodes": ["DASHBOARD", "PATIENTS"],
      "status": "ACTIVE"
    },
    "tenant": {
      "id": "…",
      "tenantCode": "GAN-DL",
      "name": "Ganesha Ayurveda",
      "schemaName": "hosp_gan_dl",
      "status": "ACTIVE"
    }
  }
}
```

### Platform — `/api/v1/platform` (SUPER_ADMIN except bootstrap)

| Method | Path | Auth | Body | Response | Note |
| --- | --- | --- | --- | --- | --- |
| POST | `/bootstrap-super-admin` | Public once | `fullName`, `email`, `password` | `UserResponse` | No JWT |
| POST | `/hospitals` | Bearer SUPER_ADMIN | clinic + contact fields | `HospitalOnboardResponse` | Auto tenantCode + schema |
| GET | `/hospitals` | Bearer SUPER_ADMIN | — | `List<TenantResponse>` | |
| GET | `/hospitals/{hospitalId}` | Bearer SUPER_ADMIN | — | `TenantResponse` | |
| PUT | `/hospitals/{hospitalId}` | Bearer SUPER_ADMIN | clinic/contact (no passwords) | `TenantResponse` | Does not change codes/schema |
| POST | `/hospitals/{hospitalId}/admins` | Bearer SUPER_ADMIN | `fullName`, `email`, `password` | `UserResponse` | Extra admin |
| GET | `/hospitals/{hospitalId}/admins` | Bearer SUPER_ADMIN | — | `List<UserResponse>` | |
| PUT | `/hospitals/{hospitalId}/status` | Bearer SUPER_ADMIN | `status` | `TenantResponse` | |
| POST | `/hospitals/{hospitalId}/retry-provision` | Bearer SUPER_ADMIN | — | `TenantResponse` | Retry FAILED schema |

### Roles — `/api/v1` (ADMIN / SUPER_ADMIN / PAGE_SETTINGS)

| Method | Path | Body | Response | Note |
| --- | --- | --- | --- | --- |
| GET | `/ui-pages` | — | `List<UiPageResponse>` | FE pageCodes |
| POST | `/roles` | `roleCode?`, `roleName`, `description?`, `pageCodes`, `active?` | `TenantRoleResponse` | |
| GET | `/roles` | — | `List<TenantRoleResponse>` | |
| GET | `/roles/{roleId}` | — | `TenantRoleResponse` | |
| PUT | `/roles/{roleId}` | `roleName`, `description?`, `pageCodes`, `active?` | `TenantRoleResponse` | |
| DELETE | `/roles/{roleId}` | — | `null` | Soft-delete custom role |

---

## Key response field notes

- **UserResponse:** `id`, `tenantId`, `tenantCode`, `schemaName`, `email`, `fullName`, `mobileNumber`, `role`, `tenantRoleId`, `tenantRoleCode`, `tenantRoleName`, `pageCodes`, `status`
- **TenantResponse:** clinic/contact fields, `tenantCode`, `schemaName`, `platform`, `status`, `provisionMessage`
- **UserRole:** `SUPER_ADMIN`, `ADMIN`, `MANAGER`, `RECEPTIONIST`, `DIETICIAN`, `DOCTOR`, `CHEMIST`
- **TenantStatus:** `PROVISIONING`, `ACTIVE`, `INACTIVE`, `SUSPENDED`, `FAILED`

For full request/response examples and FE page flows, use [AUTH_API_GUIDE.md](../../auth-service/docs/AUTH_API_GUIDE.md).

← [API_GUIDE.md](../API_GUIDE.md)
