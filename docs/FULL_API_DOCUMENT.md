# Ayurvedaa API documentation (all services except attendance)

Generated from controllers and DTOs on `fixes-development`.  
Host: `http://45.195.229.15`  
Attendance APIs are **excluded**.

Swagger UI (per service): `http://HOST:PORT/swagger-ui/index.html`  
OpenAPI JSON: `http://HOST:PORT/api-docs`

---

## 1. Services and ports

| Service | Port | Base |
|---------|------|------|
| Auth | 8111 | `http://HOST:8111` |
| Patient | 8101 | `http://HOST:8101` |
| Doctor | 8102 | `http://HOST:8102` |
| Appointment | 8103 | `http://HOST:8103` |
| Therapist | 8104 | `http://HOST:8104` |
| File upload | 8105 | `http://HOST:8105` |
| Activity log | 8107 | `http://HOST:8107` |
| Medicine | 8108 | `http://HOST:8108` |
| Billing | 8109 | `http://HOST:8109` |
| Notification | 8110 | `http://HOST:8110` |
| Payment | 8112 | `http://HOST:8112` |

---

## 2. How auth works

### 2.1 Login identities

| Who | Body | Result |
|-----|------|--------|
| Super Admin | `usernameOrEmail` + `password` (**no** `tenantCode`) | JWT with platform tenant (`schemaName` is public). Use **only** platform APIs. |
| Hospital user | `tenantCode` + `usernameOrEmail` + `password` | JWT with hospital `schemaName` (`hosp_gan_dl`, …). Use **all clinical APIs**. |

Hospital login is blocked if hospital status is `INACTIVE` or `SUSPENDED` (403).

### 2.2 Token usage

Send on every protected call:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

JWT claims include `userId`, `tenantId`, `tenantCode`, `schemaName`, `role`, `pageCodes`.  
Clinical services read the Bearer token, set tenant schema, and query that hospital’s schema. Super Admin tokens **cannot** call patient/appointment/billing (no hospital schema → 403).

### 2.3 Typical UI flow

1. `GET /api/v1/public/tenants` — pick hospital (`tenantCode`, state, city). No token.
2. `POST /api/v1/auth/login` with `tenantCode` + Gmail + password.
3. Store `data.accessToken`, `data.user.pageCodes`, `data.tenant.tenantCode`.
4. Call clinical APIs with the Bearer token.
5. Super Admin uses a separate login (no `tenantCode`) for ` /api/v1/platform/** `.

### 2.4 Common envelope (all JSON APIs)

```json
{
  "success": true,
  "status": 200,
  "message": "…",
  "data": {}
}
```

Error:

```json
{
  "success": false,
  "status": 400,
  "message": "…",
  "data": null
}
```

Paginated `data`:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

Default pagination: `page=0`, `size=20` (max 100 unless noted).

UUID fields below are shown as `"<uuid>"`. Dates: `yyyy-MM-dd`. Times: `HH:mm:ss`.

---

# 3. Auth service (`:8111`)

## 3.1 Public / login

### GET `/api/v1/public/tenants`

Auth: none.

Response `data[]`:

```json
{ "tenantCode": "GAN-DL", "state": "Delhi", "city": "New Delhi" }
```

### POST `/api/v1/auth/login`

Hospital request:

```json
{
  "tenantCode": "GAN-DL",
  "usernameOrEmail": "ganesha.admin.dl@gmail.com",
  "password": "Admin@123"
}
```

Super Admin request:

```json
{
  "usernameOrEmail": "<super-admin-email>",
  "password": "<super-admin-password>"
}
```

Response `data`:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresInMs": 86400000,
  "user": {
    "id": "<uuid>",
    "tenantId": "<uuid>",
    "tenantCode": "GAN-DL",
    "schemaName": "hosp_gan_dl",
    "email": "ganesha.admin.dl@gmail.com",
    "fullName": "Ganesha Admin",
    "mobileNumber": "9090909099",
    "role": "ADMIN",
    "tenantRoleId": "<uuid>",
    "tenantRoleCode": "ADMIN",
    "tenantRoleName": "Admin",
    "pageCodes": ["DASHBOARD", "PATIENTS", "SETTINGS"],
    "status": "ACTIVE"
  },
  "tenant": {
    "id": "<uuid>",
    "tenantCode": "GAN-DL",
    "name": "Ganesha Delhi",
    "clinicType": "HOSPITAL",
    "state": "Delhi",
    "stateCode": "DL",
    "city": "New Delhi",
    "pinCode": "110001",
    "addressLine1": "…",
    "addressLine2": null,
    "registrationNumberGst": null,
    "logoUrl": null,
    "fullName": "Ganesha Admin",
    "mobileNumber": "9090909099",
    "email": "ganesha.admin.dl@gmail.com",
    "mailEmail": "alert@karnics.com",
    "mailProvider": "MICROSOFT",
    "mailPasswordMasked": "****0108",
    "photoUrl": null,
    "schemaName": "hosp_gan_dl",
    "platform": false,
    "status": "ACTIVE",
    "provisionMessage": "…"
  }
}
```

`UserRole`: `SUPER_ADMIN` | `ADMIN` | `DOCTOR` | `RECEPTIONIST` | `THERAPIST` | `STAFF` (custom roles use `tenantRoleId`).  
`UserStatus`: `ACTIVE` | `INACTIVE`.  
`TenantStatus`: `ACTIVE` | `INACTIVE` | `SUSPENDED` | `FAILED`.

### POST `/api/v1/auth/forgot-password`

```json
{ "tenantCode": "GAN-DL", "usernameOrEmail": "user@gmail.com" }
```

Super Admin: omit `tenantCode`.

Response `data`:

```json
{
  "message": "Reset instructions sent.",
  "resetToken": "<token-or-null>",
  "expiresAt": "2026-10-14T12:00:00"
}
```

### POST `/api/v1/auth/reset-password`

```json
{
  "token": "<resetToken>",
  "newPassword": "NewPass@123",
  "confirmPassword": "NewPass@123"
}
```

Response `data`: `null`

### POST `/api/v1/auth/validate`

Header: `Authorization: Bearer <jwt>` (no body).

Response `data`:

```json
{
  "valid": true,
  "userId": "<uuid>",
  "tenantId": "<uuid>",
  "tenantCode": "GAN-DL",
  "schemaName": "hosp_gan_dl",
  "email": "user@gmail.com",
  "role": "ADMIN",
  "tenantRoleId": "<uuid>",
  "pageCodes": ["DASHBOARD"]
}
```

## 3.2 Authenticated profile / users

All require Bearer. User admin APIs also need `ADMIN` / `SUPER_ADMIN` or `PAGE_SETTINGS`.

### GET `/api/v1/auth/me`

Response `data`: `UserResponse` (same as login `user`).

### PUT `/api/v1/auth/me`

```json
{ "fullName": "Updated Name" }
```

Response `data`: `UserResponse`

### PUT `/api/v1/auth/change-password`

```json
{
  "currentPassword": "Admin@123",
  "newPassword": "Admin@456",
  "confirmPassword": "Admin@456"
}
```

Response `data`: `null`

### GET `/api/v1/auth/tenant`

Response `data`: `TenantResponse` (same shape as login `tenant`).

### POST `/api/v1/auth/register-user`

```json
{
  "fullName": "Reception Staff",
  "email": "staff@gmail.com",
  "password": "Staff@123",
  "mobileNumber": "9876543210",
  "tenantRoleId": "<uuid-of-tenant-role>"
}
```

Response `data`: `UserResponse` (HTTP 201)

### GET `/api/v1/auth/users`

Response `data`: `UserResponse[]`

### GET `/api/v1/auth/users/paged?page=0&size=20`

Response `data`: paged `UserResponse`

### GET `/api/v1/auth/users/{userId}`

Response `data`: `UserResponse`

### PUT `/api/v1/auth/users/{userId}`

```json
{
  "fullName": "Name",
  "email": "user@gmail.com",
  "role": "DOCTOR",
  "tenantRoleId": null,
  "status": "ACTIVE"
}
```

Response `data`: `UserResponse`

### PUT `/api/v1/auth/users/{userId}/status`

```json
{ "status": "INACTIVE" }
```

Response `data`: `UserResponse`

### DELETE `/api/v1/auth/users/{userId}`

Response `data`: `null` (soft delete)

## 3.3 Roles (hospital admin)

Bearer + `ADMIN` / `SUPER_ADMIN` / `PAGE_SETTINGS`.

### GET `/api/v1/ui-pages`

```json
{
  "id": "<uuid>",
  "pageCode": "DASHBOARD",
  "pageName": "Dashboard",
  "description": "…",
  "module": "CORE",
  "sortOrder": 1
}
```

### POST `/api/v1/roles`

```json
{
  "roleCode": "BILLING_CLERK",
  "roleName": "Billing clerk",
  "description": "Invoices only",
  "pageCodes": ["BILLING", "PAYMENTS"],
  "active": true
}
```

Response `data` (HTTP 201):

```json
{
  "id": "<uuid>",
  "tenantId": "<uuid>",
  "roleCode": "BILLING_CLERK",
  "roleName": "Billing clerk",
  "description": "Invoices only",
  "systemRole": false,
  "active": true,
  "pageCodes": ["BILLING", "PAYMENTS"],
  "userCount": 0
}
```

### GET `/api/v1/roles` — `TenantRoleResponse[]`

### GET `/api/v1/roles/{roleId}` — `TenantRoleResponse`

### PUT `/api/v1/roles/{roleId}`

```json
{
  "roleName": "Billing clerk",
  "description": "…",
  "pageCodes": ["BILLING"],
  "active": true
}
```

### DELETE `/api/v1/roles/{roleId}` — `data: null`

## 3.4 Platform (Super Admin)

Bearer Super Admin except bootstrap.

### POST `/api/v1/platform/bootstrap-super-admin` (public until first SA exists)

```json
{
  "fullName": "Super Admin",
  "email": "<super-admin-email>",
  "password": "<super-admin-password>"
}
```

Response `data`: `UserResponse` (201)

### POST `/api/v1/platform/hospitals`

```json
{
  "clinicName": "Ganesha Delhi",
  "clinicType": "HOSPITAL",
  "state": "Delhi",
  "city": "New Delhi",
  "pinCode": "110001",
  "addressLine1": "MG Road",
  "addressLine2": null,
  "registrationNumberGst": null,
  "logoUrl": null,
  "fullName": "Hospital Admin",
  "mobileNumber": "9999999999",
  "email": "admin@gmail.com",
  "password": "Admin@123",
  "confirmPassword": "Admin@123",
  "photoUrl": null
}
```

`clinicType`: `HOSPITAL` | `CLINIC`. Tenant code auto-generated (`GAN-DL`).

Response `data` (201):

```json
{
  "hospital": { "...TenantResponse..." },
  "admin": { "...UserResponse..." }
}
```

### GET `/api/v1/platform/hospitals` — `TenantResponse[]`

### GET `/api/v1/platform/hospitals/{hospitalId}` — `TenantResponse`

### PUT `/api/v1/platform/hospitals/{hospitalId}`

Same fields as onboard except passwords. Does not change `tenantCode` / `schemaName`.

### POST `/api/v1/platform/hospitals/{hospitalId}/admins`

```json
{
  "hospitalId": "<uuid-optional>",
  "fullName": "Second Admin",
  "email": "admin2@gmail.com",
  "password": "Admin@123"
}
```

Response: `UserResponse` (201)

### GET `/api/v1/platform/hospitals/{hospitalId}/admins` — `UserResponse[]`

### PUT `/api/v1/platform/hospitals/{hospitalId}/status`

```json
{ "status": "INACTIVE" }
```

Values: `ACTIVE` | `INACTIVE` | `SUSPENDED`. Response: `TenantResponse`.

### POST `/api/v1/platform/hospitals/{hospitalId}/retry-provision` — `TenantResponse`

### PUT `/api/v1/platform/hospitals/{hospitalId}/mail`

```json
{ "email": "alert@karnics.com", "password": "smtp-or-app-password" }
```

Response `data`:

```json
{
  "hospitalId": "<uuid>",
  "tenantCode": "GAN-DL",
  "email": "alert@karnics.com",
  "provider": "MICROSOFT",
  "passwordMasked": "****0108",
  "enabled": true,
  "updatedAt": "2026-10-14T10:00:00"
}
```

### GET `/api/v1/platform/hospitals/{hospitalId}/mail` — same `HospitalMailResponse`

### PUT `/api/v1/platform/tenants/{tenantCode}/payment-gateway` (disabled; use payment-service env)

```json
{
  "provider": "PAYU",
  "merchantKey": "…",
  "merchantSalt": "…",
  "clientId": null,
  "clientSecret": null,
  "mode": "TEST",
  "paymentUrl": null,
  "enabled": true
}
```

### GET `/api/v1/platform/tenants/{tenantCode}/payment-gateway`

```json
{
  "tenantCode": "GAN-DL",
  "provider": "PAYU",
  "merchantKey": "…",
  "merchantSaltMasked": "****",
  "clientId": null,
  "clientSecretMasked": null,
  "mode": "TEST",
  "paymentUrl": null,
  "resolvedPaymentUrl": "https://test.payu.in/_payment",
  "enabled": true,
  "updatedAt": "2026-10-14T10:00:00"
}
```

---

# 4. Patient service (`:8101`)

Hospital JWT required.

### POST `/api/v1/patients/create-patient`

```json
{
  "fullName": "Pradeep Swain",
  "gender": "MALE",
  "dateOfBirth": "1998-01-15",
  "age": 28,
  "preferredLanguage": "English",
  "mobileNumber": "8328840320",
  "email": "pswain1998@gmail.com",
  "state": "Delhi",
  "city": "New Delhi",
  "address": "…",
  "emergencyContactName": null,
  "emergencyRelationship": null,
  "emergencyPhoneNumber": null,
  "idProofType": "AADHAAR",
  "idProofNumber": "xxxx",
  "occupation": null,
  "insuranceDetails": null
}
```

`gender`: `MALE` | `FEMALE` | `OTHER`. Duplicate email/mobile → 409.

Response `data` (201):

```json
{
  "id": "<uuid>",
  "patientCode": "GAN-DL-PT-00001",
  "firstName": "Pradeep",
  "lastName": "Swain",
  "fullName": "Pradeep Swain",
  "gender": "MALE",
  "dateOfBirth": "1998-01-15",
  "age": 28,
  "preferredLanguage": "English",
  "email": "pswain1998@gmail.com",
  "mobileNumber": "8328840320",
  "state": "Delhi",
  "city": "New Delhi",
  "address": null,
  "emergencyContactName": null,
  "emergencyRelationship": null,
  "emergencyPhoneNumber": null,
  "idProofType": null,
  "idProofNumber": null,
  "occupation": null,
  "insuranceDetails": null,
  "status": "ACTIVE",
  "createdAt": "2026-10-05T03:14:22",
  "updatedAt": "2026-10-05T03:14:22"
}
```

### GET `/api/v1/patients/get-patient/{patientId}` — `PatientResponse`

### GET `/api/v1/patients/get-all-patients?page=0&size=20` — paged `PatientResponse`

### GET `/api/v1/patients/get-patient-count`

```json
{
  "totalPatients": 10,
  "activePatients": 9,
  "inactivePatients": 1
}
```

### DELETE `/api/v1/patients/delete-patient/{patientId}` — soft delete, `data: null`

**Booking note:** `POST /appointments` also creates a patient. If the email already exists you get 409. Do not pre-create if you will book immediately.

### GET `/api/v1/medical-history/options?gender=MALE`

Required `gender`: `MALE` | `FEMALE` | `OTHER`.

```json
{
  "gender": "MALE",
  "pastMedicalConditions": [{ "code": "DM", "name": "Diabetes" }],
  "pastSurgeries": [{ "code": "APP", "name": "Appendectomy" }],
  "currentMedications": [{ "code": "MET", "name": "Metformin" }],
  "allergies": [{ "code": "PNC", "name": "Penicillin" }]
}
```

### GET `/api/v1/dashboard/new-patients-by-month?year=2026`

```json
{
  "year": 2026,
  "months": [{ "monthNumber": 10, "count": 2 }]
}
```

---

# 5. Doctor service (`:8102`)

### POST `/api/v1/doctors`

```json
{
  "name": "Dr Smoke Test",
  "specialization": "Panchakarma",
  "status": "ACTIVE",
  "consultationFees": 500,
  "followUpFees": 300,
  "availability": "Mon-Sat"
}
```

Response `data` (201):

```json
{
  "id": "<uuid>",
  "name": "Dr Smoke Test",
  "specialization": "Panchakarma",
  "qualification": null,
  "mobileNumber": null,
  "status": "ACTIVE",
  "consultationFees": 500.00,
  "followUpFees": 300.00,
  "availability": "Mon-Sat"
}
```

`DoctorStatus`: `ACTIVE` | `INACTIVE`.

### GET `/api/v1/doctors?page=0&size=20` — paged `DoctorResponse`

### GET `/api/v1/doctors/active?page=0&size=20` — paged active only

### GET `/api/v1/doctors/{doctorId}` — `DoctorResponse`

### PATCH `/api/v1/doctors/{doctorId}/status`

```json
{ "status": "INACTIVE" }
```

### DELETE `/api/v1/doctors/{doctorId}` — `data: null`

---

# 6. Appointment service (`:8103`)

Hospital JWT. Booking statuses: `SCHEDULED` | `CANCELLED` | `RESCHEDULED` | `IN_CONSULTATION` | `COMPLETED`.

## 6.1 Bookings `/api/v1/appointments`

### POST `/api/v1/appointments`

Creates a **new patient** from nested `patient`, then books.

```json
{
  "patient": {
    "fullName": "Pradeep Swain",
    "gender": "MALE",
    "dateOfBirth": "1998-01-15",
    "age": 28,
    "preferredLanguage": "English",
    "mobileNumber": "8328840320",
    "email": "pswain1998@gmail.com",
    "state": null,
    "city": null,
    "address": null,
    "emergencyContactName": null,
    "emergencyRelationship": null,
    "emergencyPhoneNumber": null,
    "idProofType": null,
    "idProofNumber": null,
    "occupation": null,
    "insuranceDetails": null
  },
  "registrationDate": "2026-10-05",
  "slotTime": "10:00:00",
  "assignedDoctorId": "<doctor-uuid>",
  "consultationTypeIds": ["<consultation-type-uuid>"]
}
```

Response `data` (201) `AppointmentBookingResponse`:

```json
{
  "id": "<booking-uuid>",
  "patientId": "<patient-uuid>",
  "patient": {
    "id": "<uuid>",
    "patientCode": "GAN-DL-PT-00002",
    "firstName": "Pradeep",
    "lastName": "Swain",
    "fullName": "Pradeep Swain",
    "gender": "MALE",
    "dateOfBirth": "1998-01-15",
    "age": 28,
    "preferredLanguage": null,
    "email": "pswain1998@gmail.com",
    "mobileNumber": "8328840320",
    "state": null,
    "city": null,
    "address": null,
    "emergencyContactName": null,
    "emergencyRelationship": null,
    "emergencyPhoneNumber": null,
    "idProofType": null,
    "idProofNumber": null,
    "occupation": null,
    "insuranceDetails": null
  },
  "registrationDate": "2026-10-05",
  "slotTime": "10:00:00",
  "assignedDoctorId": "<uuid>",
  "assignedDoctor": {
    "id": "<uuid>",
    "name": "Dr Smoke Test",
    "specialization": "Panchakarma",
    "qualification": null,
    "mobileNumber": null,
    "status": "ACTIVE",
    "consultationFees": 500.00,
    "followUpFees": 300.00,
    "availability": "Mon-Sat"
  },
  "consultationTypes": [
    { "id": "<uuid>", "name": "CONSULTATION" }
  ],
  "bookingStatus": "SCHEDULED"
}
```

### GET `/api/v1/appointments/stats`

```json
{
  "currentMonthAppointmentCount": 12,
  "completedCount": 4,
  "ongoingCount": 8,
  "todayAppointmentCount": 2
}
```

### GET `/api/v1/appointments/patients`

Query: **required** `statusTab=ACTIVE|INACTIVE`  
Optional: `search`, `bookingStatus`, `consultationTypeId`, `doshaId`, `doctorId`, `page`, `size`.

Paged `PatientAppointmentListItemResponse`:

```json
{
  "bookingId": "<uuid>",
  "patientId": "<uuid>",
  "patientCode": "GAN-DL-PT-00002",
  "patientFullName": "Pradeep Swain",
  "patientMobileNumber": "8328840320",
  "assignedDoctorId": "<uuid>",
  "doctorName": "Dr Smoke Test",
  "consultationTypes": [{ "id": "<uuid>", "name": "THERAPY" }],
  "appointmentDate": "2026-10-05",
  "slotTime": "10:00:00",
  "bookingTime": "2026-10-05T03:27:00",
  "doshaId": null,
  "doshaName": null,
  "bookingStatus": "SCHEDULED"
}
```

### GET `/api/v1/appointments/cancelled?page=0&size=20` — paged `AppointmentBookingResponse`

### GET `/api/v1/appointments/today?page=0&size=20`

```json
{
  "doctorId": null,
  "date": "2026-10-05",
  "totalAppointments": 1,
  "page": 0,
  "size": 20,
  "totalPages": 1,
  "appointments": [
    {
      "bookingId": "<uuid>",
      "assignedDoctorId": "<uuid>",
      "slotTime": "10:00:00",
      "bookingTime": "2026-10-05T03:27:00",
      "bookingStatus": "SCHEDULED",
      "patientId": "<uuid>",
      "patientName": "Pradeep Swain",
      "patientMobileNumber": "8328840320",
      "consultationTypes": [{ "id": "<uuid>", "name": "General Consultation" }]
    }
  ]
}
```

### GET `/api/v1/appointments/today/consultation-type/{consultationTypeId}?page=0&size=20`

Paged `AppointmentBookingResponse` (full booking + patient + doctor).

### GET `/api/v1/appointments/doctor/{doctorId}/today?page=0&size=20` — `DoctorTodayScheduleResponse` (`doctorId` set)

### GET `/api/v1/appointments/{bookingId}` — `AppointmentBookingResponse`

### GET `/api/v1/appointments/patient/{patientId}?page=0&size=20` — paged bookings

### GET `/api/v1/appointments/status/{bookingStatus}?page=0&size=20`

`bookingStatus`: `SCHEDULED` | `CANCELLED` | `RESCHEDULED` | `IN_CONSULTATION` | `COMPLETED` | `ALL`

### GET `/api/v1/appointments/date/{registrationDate}?page=0&size=20`

Path date `yyyy-MM-dd`. Paged bookings.

### PUT `/api/v1/appointments/{bookingId}/reschedule`

```json
{
  "patientId": "<optional>",
  "registrationDate": "2026-10-06",
  "slotTime": "11:00:00",
  "assignedDoctorId": "<optional>",
  "consultationTypeIds": ["<optional>"]
}
```

Not allowed if `COMPLETED` or `IN_CONSULTATION`. Sets `RESCHEDULED`.

### PUT `/api/v1/appointments/{bookingId}/cancel` — no body → `CANCELLED`

### DELETE `/api/v1/appointments/{bookingId}` — soft delete + cancelled, `data: null`

### PUT `/api/v1/appointments/{bookingId}/in-consultation` — `SCHEDULED|RESCHEDULED` → `IN_CONSULTATION`

### PUT `/api/v1/appointments/{bookingId}/complete` — `IN_CONSULTATION` → `COMPLETED`

## 6.2 Consultation types `/api/v1/consultation-types`

Masters (e.g. `CONSULTATION`, `THERAPY`) live **per hospital**. Empty hospital only has what you create.

### POST `/api/v1/consultation-types`

```json
{ "name": "CONSULTATION", "status": "ACTIVE" }
```

`status`: `ACTIVE` | `INACTIVE`. Response (201):

```json
{ "id": "<uuid>", "name": "CONSULTATION", "status": "ACTIVE" }
```

### GET `/api/v1/consultation-types` — all non-deleted `ConsultationTypeResponse[]`

### GET `/api/v1/consultation-types/active` — ACTIVE only

### GET `/api/v1/consultation-types/{consultationTypeId}` — one

## 6.3 Dashboard `/api/v1/dashboard`

### GET `/api/v1/dashboard/todays-schedule?doctorId=<optional>`

```json
{
  "date": "2026-10-05",
  "currentDateTime": "2026-10-05T10:15:00",
  "ongoingAppointment": {
    "bookingId": "<uuid>",
    "patientId": "<uuid>",
    "patientName": "Pradeep Swain",
    "serviceType": "CONSULTATION",
    "bookingStatus": "IN_CONSULTATION"
  },
  "nextAppointment": { "bookingId": "<uuid>", "patientId": "<uuid>", "patientName": "…", "serviceType": "THERAPY", "bookingStatus": "SCHEDULED" },
  "remainingToday": 3
}
```

### GET `/api/v1/dashboard/patient-trends?year=2026`

```json
{
  "year": 2026,
  "totalNewPatients": 12,
  "totalFollowUps": 4,
  "points": [
    { "month": "Jan", "monthNumber": 1, "newPatients": 0, "followUps": 0 }
  ]
}
```

## 6.4 Therapies master `/api/v1/therapies`

### POST `/api/v1/therapies`

```json
{
  "name": "Abhyanga",
  "categoryId": "<treatment-category-uuid>",
  "status": "ACTIVE",
  "durationMinutes": 45,
  "price": 1500,
  "description": "Oil massage"
}
```

Response:

```json
{
  "id": "<uuid>",
  "name": "Abhyanga",
  "therapyName": "Abhyanga",
  "therapyCode": "THR-0001",
  "categoryId": "<uuid>",
  "categoryName": "Panchakarma",
  "status": "ACTIVE",
  "durationMinutes": 45,
  "price": 1500,
  "description": "Oil massage"
}
```

### GET `/api/v1/therapies?page=0&size=20` — paged

### GET `/api/v1/therapies/category/{categoryId}?page=0&size=20`

### GET `/api/v1/therapies/{therapyId}`

### PUT `/api/v1/therapies/{therapyId}` — same body as create (`UpdateTherapyRequest`)

### PATCH `/api/v1/therapies/{therapyId}/status`

```json
{ "status": "INACTIVE" }
```

### DELETE `/api/v1/therapies/{therapyId}` — `data: null`

## 6.5 Treatment categories `/api/v1/treatment-categories`

### POST `/api/v1/treatment-categories`

```json
{
  "categoryName": "Panchakarma",
  "description": "…",
  "status": "ACTIVE"
}
```

Response:

```json
{
  "id": "<uuid>",
  "categoryCode": "CAT-0001",
  "categoryName": "Panchakarma",
  "description": "…",
  "status": "ACTIVE"
}
```

### GET `/api/v1/treatment-categories` — list  
### GET `/api/v1/treatment-categories/{categoryId}`

## 6.6 Doshas `/api/v1/doshas`

### POST `/api/v1/doshas`

```json
{
  "name": "Vata",
  "elements": "Air, Ether",
  "characteristics": "…",
  "status": "ACTIVE"
}
```

Response:

```json
{
  "id": "<uuid>",
  "name": "Vata",
  "elements": "Air, Ether",
  "characteristics": "…",
  "status": "ACTIVE"
}
```

### GET `/api/v1/doshas` — list  
### GET `/api/v1/doshas/{doshaId}`

## 6.7 Treatment plan masters `/api/v1/treatment-plan-masters`

### POST `/api/v1/treatment-plan-masters`

```json
{ "name": "Panchakarma 7-day", "status": "ACTIVE" }
```

```json
{ "id": "<uuid>", "name": "Panchakarma 7-day", "status": "ACTIVE" }
```

### GET `/api/v1/treatment-plan-masters`  
### GET `/api/v1/treatment-plan-masters/active`  
### GET `/api/v1/treatment-plan-masters/{treatmentPlanId}`

## 6.8 Treatments `/api/v1/treatments`

### POST `/api/v1/treatments`

```json
{
  "patientId": "<uuid>",
  "treatmentPlanId": "<uuid>",
  "startDate": "2026-10-05",
  "endDate": "2026-10-12",
  "totalSessions": 7,
  "completedSessions": 0,
  "assignedTherapistId": "<uuid>",
  "treatmentStatus": "SCHEDULED"
}
```

Response:

```json
{
  "id": "<uuid>",
  "patientId": "<uuid>",
  "treatmentPlanId": "<uuid>",
  "treatmentPlanName": "Panchakarma 7-day",
  "startDate": "2026-10-05",
  "endDate": "2026-10-12",
  "totalSessions": 7,
  "completedSessions": 0,
  "remainingSessions": 7,
  "assignedTherapistId": "<uuid>",
  "assignedTherapistName": "Therapist A",
  "treatmentStatus": "SCHEDULED"
}
```

### GET `/api/v1/treatments`  
### GET `/api/v1/treatments/patient/{patientId}`  
### PUT `/api/v1/treatments/{treatmentId}`

```json
{
  "treatmentPlanId": "<uuid>",
  "startDate": "2026-10-05",
  "endDate": "2026-10-12",
  "totalSessions": 7,
  "completedSessions": 1,
  "assignedTherapistId": "<uuid>"
}
```

### PUT `/api/v1/treatments/{treatmentId}/status`

```json
{ "treatmentStatus": "ONGOING" }
```

## 6.9 Appointment therapies `/api/v1/appointment-therapies`

### POST `/api/v1/appointment-therapies`

```json
{
  "patientId": "<uuid>",
  "treatmentCategoryId": "<uuid>",
  "assignedTherapistId": "<uuid>",
  "scheduleDate": "2026-10-06",
  "scheduleTime": "09:00:00",
  "sessionDuration": 45,
  "sessionFrequency": 1,
  "therapyInstructions": "Warm oil",
  "remarks": null,
  "therapyIds": ["<therapy-uuid>"]
}
```

`TherapyStatus`: `SCHEDULED` | `IN_PROGRESS` | `COMPLETED` | `CANCELLED`.

Response (201):

```json
{
  "therapyId": "<appointment-therapy-uuid>",
  "patient": { "...PatientSummaryResponse..." },
  "treatmentCategory": { "id": "<uuid>", "categoryCode": "CAT-0001", "categoryName": "Panchakarma", "description": null, "status": "ACTIVE" },
  "assignedTherapist": { "id": "<uuid>", "name": "Therapist A", "therapistName": "Therapist A", "therapistCode": "TH-0001", "status": "ACTIVE" },
  "scheduleDate": "2026-10-06",
  "scheduleTime": "09:00:00",
  "sessionDuration": 45,
  "sessionFrequency": 1,
  "therapyInstructions": "Warm oil",
  "remarks": null,
  "therapyStatus": "SCHEDULED",
  "therapies": [{ "id": "<uuid>", "name": "Abhyanga", "therapyName": "Abhyanga", "therapyCode": "THR-0001", "categoryId": "<uuid>", "categoryName": "Panchakarma", "status": "ACTIVE", "durationMinutes": 45, "price": 1500, "description": null }]
}
```

### PUT `/api/v1/appointment-therapies/{appointmentTherapyId}/status`

```json
{ "therapyStatus": "COMPLETED" }
```

### GET `/api/v1/appointment-therapies/therapist/{therapistId}/today?page=0&size=20`

```json
{
  "therapistId": "<uuid>",
  "date": "2026-10-06",
  "totalSlots": 1,
  "page": 0,
  "size": 20,
  "totalPages": 1,
  "slots": [
    {
      "appointmentTherapyId": "<uuid>",
      "scheduleTime": "09:00:00",
      "sessionDuration": 45,
      "therapyStatus": "SCHEDULED",
      "patientId": "<uuid>",
      "patientName": "Pradeep Swain",
      "patientMobileNumber": "8328840320",
      "therapies": ["Abhyanga"],
      "treatmentCategoryName": "Panchakarma"
    }
  ]
}
```

### GET `/api/v1/appointment-therapies/{patientId}` — `AppointmentTherapyResponse[]`

## 6.10 Follow-ups `/api/v1/follow-ups`

Does **not** create a booking.

### POST `/api/v1/follow-ups`

```json
{
  "patientId": "<uuid>",
  "assignedDoctorId": "<uuid>",
  "sourceBookingId": "<uuid>",
  "visitTypeId": "<consultation-type-uuid>",
  "appointmentDate": "2026-10-20T10:00:00",
  "schedulingOption": "AFTER_7_DAYS",
  "smsReminderEnabled": true,
  "status": "UPCOMING"
}
```

`FollowUpStatus`: `UPCOMING` | `MISSED` | `COMPLETED` | `CANCELLED`.

Response (201):

```json
{
  "id": "<uuid>",
  "patientId": "<uuid>",
  "patientCode": "GAN-DL-PT-00002",
  "patientName": "Pradeep Swain",
  "assignedDoctorId": "<uuid>",
  "doctorName": "Dr Smoke Test",
  "sourceBookingId": "<uuid>",
  "visitTypeId": "<uuid>",
  "visitTypeName": "CONSULTATION",
  "appointmentDate": "2026-10-20T10:00:00",
  "schedulingOption": "AFTER_7_DAYS",
  "smsReminderEnabled": true,
  "status": "UPCOMING"
}
```

### GET `/api/v1/follow-ups?page=0&size=20`  
### GET `/api/v1/follow-ups/patient/{patientId}?page=0&size=20`

### PUT `/api/v1/follow-ups/{followUpId}/status`

```json
{ "status": "COMPLETED" }
```

### PUT `/api/v1/follow-ups/{followUpId}/cancel` — no body

## 6.11 Prescriptions `/api/v1/prescriptions`

### POST `/api/v1/prescriptions`

```json
{
  "patientId": "<uuid>",
  "appointmentBookingId": "<uuid>",
  "assignedDoctorId": "<uuid>",
  "diagnosis": "Vata imbalance",
  "notes": "Take after food",
  "medicines": [
    {
      "medicineId": "<uuid>",
      "medicineName": "Dashamoola",
      "dosage": "5ml",
      "frequency": "BD",
      "duration": "7 days",
      "notes": null
    }
  ],
  "therapySuggestions": [
    {
      "therapyCategoryId": "<uuid>",
      "recommendedTherapyIds": ["<uuid>"],
      "setUpRequired": true,
      "schedulingOption": "NEXT_WEEK",
      "suggestions": "Gentle"
    }
  ],
  "nextFollowUp": {
    "schedulingOption": "AFTER_7_DAYS",
    "appointmentDate": "2026-10-20T10:00:00"
  }
}
```

Response includes nested `patient`, `treatment`, `consultant`, `medicines`, `therapySuggestions`, `nextFollowUp`, timestamps.

### PUT `/api/v1/prescriptions/{prescriptionId}` — same lists (patientId cannot change)

### GET `/api/v1/prescriptions/patient/{patientId}` — `PrescriptionResponse[]` newest first  
### GET `/api/v1/prescriptions/{prescriptionId}`

## 6.12 Medical assessment `/api/v1/medical-assessment`

Saves six sections. Nested objects all include `patientId` (same as root).

### POST `/api/v1/medical-assessment` (JSON)

```json
{
  "patientId": "<uuid>",
  "ayurvedicAssessment": {
    "patientId": "<uuid>",
    "doshaId": "<uuid>",
    "bodyConstitution": "Vata-Pitta",
    "currentImbalances": "Dry skin"
  },
  "physicalExamination": {
    "patientId": "<uuid>",
    "weight": 70.5,
    "height": 172.0,
    "ibw": 68.0,
    "pulse": 72,
    "bp": "120/80",
    "temperature": 36.8,
    "pallor": "Absent",
    "icterus": "Absent",
    "cyanosis": "Absent",
    "lymphNodes": "NAD",
    "oedema": "Absent",
    "sensorium": "Alert",
    "acidityGas": "Nil",
    "motion": "Regular",
    "micturition": "Normal"
  },
  "medicalHistory": {
    "patientId": "<uuid>",
    "pastMedicalConditions": "None",
    "pastSurgeries": "None",
    "currentMedications": "None",
    "allergies": "None",
    "familyHistory": "None"
  },
  "lifestyleInformation": {
    "patientId": "<uuid>",
    "dietType": "Vegetarian",
    "sleepPattern": "7 hours",
    "exerciseHabits": "Walk",
    "addiction": "None"
  },
  "systemicExamination": {
    "patientId": "<uuid>",
    "cardiovascular": "S1 S2 normal",
    "respiratory": "B/L AE",
    "nervous": "NAD",
    "abdomenGi": "Soft",
    "locomotor": "NAD"
  },
  "treatmentPlan": {
    "patientId": "<uuid>",
    "investigationAndPlanSuggested": "CBC",
    "planTaken": "Panchakarma"
  }
}
```

Response `data` mirrors each section with generated `id`s plus `documents: []`.

### POST `/api/v1/medical-assessment/with-documents` (`multipart/form-data`)

- Part `data`: JSON `CreateMedicalAssessmentRequest`
- Optional files: `pastMedicalReports`, `prescriptions`, `labReports`

### GET `/api/v1/medical-assessment/{patientId}` — `MedicalAssessmentResponse`

## 6.13 Section APIs (same fields as nested assessment)

| Method | Path |
|--------|------|
| POST | `/api/v1/ayurvedic-assessments` |
| GET | `/api/v1/ayurvedic-assessments/{patientId}` |
| POST | `/api/v1/physical-examinations` |
| GET | `/api/v1/physical-examinations/{patientId}` |
| POST | `/api/v1/medical-histories` |
| GET | `/api/v1/medical-histories/{patientId}` |
| POST | `/api/v1/lifestyle-information` |
| GET | `/api/v1/lifestyle-information/{patientId}` |
| POST | `/api/v1/systemic-examinations` |
| GET | `/api/v1/systemic-examinations/{patientId}` |
| POST | `/api/v1/treatment-plans` |
| GET | `/api/v1/treatment-plans/{patientId}` |

Request bodies = the nested objects in §6.12. GET returns that section (or list) for the patient.

---

# 7. Therapist service (`:8104`)

### POST `/api/v1/therapists`

```json
{
  "name": "Therapist A",
  "status": "ACTIVE",
  "assignedTherapyIds": ["<therapy-uuid>"]
}
```

Response (201):

```json
{
  "id": "<uuid>",
  "name": "Therapist A",
  "therapistName": "Therapist A",
  "therapistCode": "TH-0001",
  "status": "ACTIVE",
  "assignedTherapies": [{ "id": "<uuid>", "name": "Abhyanga" }]
}
```

### PUT `/api/v1/therapists/{therapistId}` — same body as create

### GET `/api/v1/therapists/by-therapies?therapyIds=<uuid>&therapyIds=<uuid>` — `TherapistResponse[]`

### GET `/api/v1/therapists/{therapistId}`  
### GET `/api/v1/therapists?page=0&size=20` — paged

### PATCH `/api/v1/therapists/{therapistId}/status`

```json
{ "status": "INACTIVE" }
```

### DELETE `/api/v1/therapists/{therapistId}` — `data: null`

---

# 8. File upload service (`:8105`)

### POST `/api/v1/documents/upload` (`multipart/form-data`)

Form fields:

| Field | Value |
|-------|--------|
| `patientId` | UUID |
| `documentType` | `PAST_MEDICAL_REPORT` \| `PRESCRIPTION` \| `LAB_REPORT` |
| `file` | binary |

Response:

```json
{
  "id": "<uuid>",
  "patientId": "<uuid>",
  "documentType": "LAB_REPORT",
  "fileName": "report.pdf",
  "fileType": "application/pdf",
  "fileSize": 12345,
  "downloadUrl": "/api/v1/documents/<id>/download"
}
```

### GET `/api/v1/documents/{patientId}` — `AppointmentDocumentResponse[]`

### GET `/api/v1/documents/{documentId}/download` — file bytes (not JSON envelope)

### DELETE `/api/v1/documents/{documentId}`

```json
{ "success": true, "status": 200, "message": "…", "data": "…" }
```

---

# 9. Activity log service (`:8107`)

### POST `/api/v1/activity-logs`

```json
{
  "page": "Appointments",
  "action": "CREATED",
  "target": "Appointment <uuid>",
  "beforeValue": null,
  "afterValue": null,
  "activityTimestamp": "2026-10-05T10:00:00",
  "performedByUserId": "<uuid>",
  "performedByUserName": "Admin",
  "performedByRole": "ADMIN"
}
```

`ActivityAction`: `CREATED` | `UPDATED` | `DELETED` | `STATUS_CHANGED` (and related enum values used by publisher).

Response (201):

```json
{
  "id": "<uuid>",
  "page": "Appointments",
  "action": "CREATED",
  "target": "Appointment <uuid>",
  "before": null,
  "after": null,
  "timestamp": "2026-10-05T10:00:00",
  "performedByUserId": "<uuid>",
  "performedByUserName": "Admin",
  "performedByRole": "ADMIN"
}
```

### GET `/api/v1/activity-logs?page=&action=&search=` — `ActivityLogResponse[]` (newest first)  
`page` here is a **filter string** (UI page name), not pagination.

### GET `/api/v1/activity-logs/{id}` — one log

---

# 10. Medicine service (`:8108`)

`MedicineCategory` examples: `TABLET` | `SYRUP` | `POWDER` (and other enum values in code).  
`MedicineStatus`: `ACTIVE` | `INACTIVE`.  
`MedicineStockStatus`: `IN_STOCK` | `OUT_OF_STOCK` | `LOW_STOCK`.

### POST `/api/v1/medicines`

One object **or** `{ "medicines": [ ... ] }`.

```json
{
  "medicineName": "Dashamoola",
  "category": "SYRUP",
  "manufacturer": "Himalaya",
  "batchNumber": "B001",
  "quantity": 50,
  "expiryDate": "2027-12-31",
  "purchasePrice": 80,
  "sellingPrice": 120,
  "lowStockAlertEnabled": true,
  "lowStockThreshold": 10,
  "status": "ACTIVE"
}
```

Response (201) `MedicineResponse[]`:

```json
{
  "id": "<uuid>",
  "medicineName": "Dashamoola",
  "category": "SYRUP",
  "manufacturer": "Himalaya",
  "batchNumber": "B001",
  "stockQuantity": 50,
  "expiryDate": "2027-12-31",
  "purchasePrice": 80.00,
  "sellingPrice": 120.00,
  "price": 120.00,
  "lowStockAlertEnabled": true,
  "lowStockThreshold": 10,
  "status": "ACTIVE",
  "stockStatus": "IN_STOCK"
}
```

### PUT `/api/v1/medicines/{medicineId}` — `UpdateMedicineRequest` (same fields)

### GET `/api/v1/medicines?medicineName=&category=&stockStatus=&page=0&size=20` — paged

### GET `/api/v1/medicines/stock/summary`

```json
{
  "totalStock": 500,
  "byCategory": [{ "category": "TABLET", "totalStock": 300, "medicineCount": 12 }]
}
```

### GET `/api/v1/medicines/stock/category/{category}` — `CategoryStockCountResponse`

### GET `/api/v1/medicines/low-stock?page=0&size=20` — paged `MedicineResponse`

### GET `/api/v1/medicines/meta/categories` — `["TABLET","SYRUP","POWDER",…]`

### GET `/api/v1/medicines/meta/manufacturers` — `["Himalaya",…]`

### GET `/api/v1/medicines/meta/names` — `[{ "id": "<uuid>", "medicineName": "Dashamoola" }]`

### GET `/api/v1/medicines/{medicineId}`

### POST `/api/v1/medicines/{medicineId}/stock/deduct`

```json
{ "quantity": 2 }
```

### POST `/api/v1/medicines/{medicineId}/stock/restore`

```json
{ "quantity": 2 }
```

### DELETE `/api/v1/medicines/{medicineId}` — `data: null`

### GET `/api/v1/dashboard/medicine-stock?lowStockLimit=5`

```json
{
  "totalStock": 500,
  "tablets": 300,
  "syrups": 100,
  "powder": 100,
  "statusBreakdown": { "inStock": 40, "outOfStock": 2, "lowStock": 5 },
  "lowStockItems": [{ "...MedicineResponse..." }]
}
```

---

# 11. Billing service (`:8109`)

`VisitType`: `CONSULTATION` | `FOLLOW_UP` | `THERAPY` | `PACKAGE`  
`InvoiceStatus`: `UNPAID` | `ONGOING` | `COMPLETED`  
`BillingStatus`: `PENDING` | `COMPLETED`  
`PackageStatus`: `SCHEDULED` | `ONGOING` | `COMPLETED`

## 11.1 Invoices `/api/v1/invoices`

### POST `/api/v1/invoices`

Minimal consultation invoice:

```json
{
  "patientId": "<uuid>",
  "patientName": "Pradeep Swain",
  "contactNumber": "8328840320",
  "patientEmail": "pswain1998@gmail.com",
  "invoiceDate": "2026-10-05",
  "visitType": "CONSULTATION",
  "serviceFees": 10,
  "packageMasterId": null,
  "packageType": null,
  "packageCharges": null,
  "medicines": [
    { "medicineId": "<uuid>", "quantity": 1, "unitPrice": 120 }
  ],
  "therapies": [
    {
      "itemName": "Abhyanga",
      "quantity": 1,
      "unitPrice": 1500,
      "assignedTherapistId": "<uuid>",
      "assignedTherapistName": "Therapist A",
      "scheduleDate": "2026-10-06",
      "scheduleTime": "09:00:00",
      "sessionDuration": 45,
      "sessionFrequency": 1
    }
  ],
  "discount": 0,
  "taxEnabled": false,
  "cgstPercent": 0,
  "sgstPercent": 0,
  "amountPaid": 0,
  "paymentMethod": null,
  "paymentRemarks": null
}
```

Sections may be combined (service / medicine / therapy). Each POST creates `{tenantCode}-INV-#####`.

Response (201) `InvoiceResponse`:

```json
{
  "id": "<invoice-uuid>",
  "invoiceId": "GAN-DL-INV-00001",
  "patientId": "<uuid>",
  "patientName": "Pradeep Swain",
  "contactNumber": "8328840320",
  "invoiceDate": "2026-10-05",
  "visitType": "CONSULTATION",
  "serviceFees": 10.00,
  "packageMasterId": null,
  "packageName": null,
  "packageType": null,
  "packageCharges": null,
  "subtotal": 10.00,
  "discount": 0,
  "taxEnabled": false,
  "cgstPercent": 0,
  "cgstAmount": 0,
  "sgstPercent": 0,
  "sgstAmount": 0,
  "totalAmount": 10.00,
  "paidAmount": 0,
  "leftAmount": 10.00,
  "status": "UNPAID",
  "billSections": ["SERVICE"],
  "items": [
    {
      "id": "<uuid>",
      "itemType": "SERVICE",
      "itemName": "CONSULTATION",
      "quantity": 1,
      "unitPrice": 10.00,
      "amount": 10.00,
      "medicineId": null,
      "assignedTherapistId": null,
      "assignedTherapistName": null,
      "scheduleDate": null,
      "scheduleTime": null,
      "sessionDuration": null,
      "sessionFrequency": null
    }
  ],
  "payments": []
}
```

### GET `/api/v1/invoices?patientId=&status=&page=0&size=20`

Paged `InvoiceListResponse`:

```json
{
  "id": "<uuid>",
  "invoiceId": "GAN-DL-INV-00001",
  "patientId": "<uuid>",
  "patientName": "Pradeep Swain",
  "invoiceDate": "2026-10-05",
  "totalAmount": 10.00,
  "paidAmount": 0,
  "leftAmount": 10.00,
  "status": "UNPAID",
  "billSections": ["SERVICE"]
}
```

### GET `/api/v1/invoices/patient/{patientId}?status=&page=0&size=20`

### GET `/api/v1/invoices/{invoiceId}` — full `InvoiceResponse`

### POST `/api/v1/invoices/{invoiceId}/payments` (cash / part)

```json
{
  "amountPaid": 10,
  "paymentMethod": "CASH",
  "remarks": "Paid at desk"
}
```

`ONLINE` / `QR` are **not** accepted here — use payment links. Cash full amount → `COMPLETED`; partial → `ONGOING`.

### POST `/api/v1/invoices/{invoiceId}/refunds` (cash refund)

```json
{
  "amount": 10,
  "reason": "Cancelled",
  "patientEmail": "pswain1998@gmail.com"
}
```

Omit `amount` to refund full paid balance. PayU refunds use payment-service.

### DELETE `/api/v1/invoices/{invoiceId}` — `data: null`

## 11.2 Doctor billing `/api/v1/billings`

### POST `/api/v1/billings` (PENDING, no invoice)

```json
{
  "patientId": "<uuid>",
  "patientName": "Pradeep Swain",
  "contactNumber": "8328840320",
  "billingDate": "2026-10-05",
  "services": [
    {
      "serviceType": "CONSULTATION",
      "serviceFees": 500,
      "packageMasterId": null,
      "packageType": null,
      "packageCharges": null
    }
  ]
}
```

Response (201) `BillingResponse` with `status: PENDING`, `invoiceId: null`.

### GET `/api/v1/billings?status=PENDING&page=0&size=20` — paged `BillingListResponse`

### GET `/api/v1/billings/patient/{patientId}?page=0&size=20`

### GET `/api/v1/billings/{billingId}`

### POST `/api/v1/billings/{billingId}/generate-invoice`

Body = `CreateInvoiceRequest`. Marks billing `COMPLETED`, returns `InvoiceResponse`.

## 11.3 Patient billing aggregate `/api/v1/billing/patient/{patientId}?status=`

```json
{
  "patientId": "<uuid>",
  "summary": {
    "invoiceCount": 2,
    "unpaidCount": 1,
    "ongoingCount": 0,
    "completedCount": 1,
    "totalAmount": 510.00,
    "paidAmount": 500.00,
    "leftAmount": 10.00,
    "packageCount": 0
  },
  "invoices": [{ "...InvoiceListResponse..." }],
  "packages": [{ "...PatientPackageResponse..." }]
}
```

## 11.4 Package masters `/api/v1/package-masters`

### POST `/api/v1/package-masters`

```json
{ "name": "7-day Panchakarma", "packagePrice": 25000, "status": "ACTIVE" }
```

```json
{ "id": "<uuid>", "name": "7-day Panchakarma", "packagePrice": 25000, "status": "ACTIVE" }
```

### GET `/api/v1/package-masters`  
### GET `/api/v1/package-masters/active`  
### GET `/api/v1/package-masters/{packageMasterId}`

## 11.5 Patient packages `/api/v1/packages`

### POST `/api/v1/packages`

```json
{
  "patientId": "<uuid>",
  "packageMasterId": "<uuid>",
  "validity": "2026-12-31",
  "status": "SCHEDULED",
  "discountApplied": 0
}
```

```json
{
  "id": "<uuid>",
  "patientId": "<uuid>",
  "packageMasterId": "<uuid>",
  "packageName": "7-day Panchakarma",
  "packagePrice": 25000,
  "validity": "2026-12-31",
  "status": "SCHEDULED",
  "discountApplied": 0
}
```

### GET `/api/v1/packages`  
### GET `/api/v1/packages/patient/{patientId}`  
### PUT `/api/v1/packages/{packageId}`

```json
{ "packageMasterId": "<uuid>", "validity": "2026-12-31", "discountApplied": 500 }
```

### PUT `/api/v1/packages/{packageId}/status`

```json
{ "status": "ONGOING" }
```

## 11.6 Sales `/api/v1/sales`

### GET `/api/v1/sales?serviceType=&dateCreated=2026-10-05&page=0&size=20`

```json
{
  "revenueThisMonth": 1000.00,
  "revenueFrom": "2026-10-01",
  "revenueTo": "2026-10-31",
  "sales": [
    {
      "invoiceId": "GAN-DL-INV-00001",
      "invoiceDate": "2026-10-05",
      "treatmentCategory": "Panchakarma",
      "serviceType": "CONSULTATION",
      "totalAmount": 10.00
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

### GET `/api/v1/sales/revenue/month?year=2026&month=10`

```json
{
  "year": 2026,
  "month": 10,
  "fromDate": "2026-10-01",
  "toDate": "2026-10-31",
  "totalRevenue": 1000.00,
  "invoiceCount": 5
}
```

### GET `/api/v1/dashboard/billing-summary?period=MONTHLY`

`period`: `WEEKLY` | `MONTHLY` | `YEARLY`

```json
{
  "period": "MONTHLY",
  "fromDate": "2026-10-01",
  "toDate": "2026-10-31",
  "totalRevenue": 1000.00,
  "totalBillsGenerated": 5,
  "pendingPayments": 10.00,
  "collectedPayments": 990.00
}
```

---

# 12. Notification service (`:8110`)

### POST `/api/v1/notifications/email`

SMTP failure is **soft** (HTTP 200, `deliveryStatus` FAILED/SKIPPED).

```json
{
  "to": "pswain1998@gmail.com",
  "subject": "Welcome",
  "body": "Hello",
  "tenantCode": "GAN-DL"
}
```

```json
{
  "deliveryStatus": "SENT",
  "attempts": 1,
  "to": "pswain1998@gmail.com",
  "tenantCode": "GAN-DL",
  "errorMessage": null
}
```

### POST `/api/v1/notifications`

```json
{
  "recipientUserId": "<uuid>",
  "recipientUserName": "Admin",
  "recipientRole": "ADMIN",
  "title": "New appointment",
  "message": "Patient booked",
  "type": "APPOINTMENT",
  "priority": "NORMAL",
  "referenceId": "<uuid>",
  "referenceType": "APPOINTMENT"
}
```

`type`: `APPOINTMENT` | `BILLING` | `MEDICINE` | `THERAPY` | `SYSTEM` | `GENERAL`

Response (201):

```json
{
  "id": "<uuid>",
  "recipientUserId": "<uuid>",
  "recipientUserName": "Admin",
  "recipientRole": "ADMIN",
  "title": "New appointment",
  "message": "Patient booked",
  "type": "APPOINTMENT",
  "priority": "NORMAL",
  "referenceId": "<uuid>",
  "referenceType": "APPOINTMENT",
  "read": false,
  "readAt": null,
  "createdAt": "2026-10-05T10:00:00"
}
```

### GET `/api/v1/notifications?userId=<uuid>&unreadOnly=true&type=APPOINTMENT`

### GET `/api/v1/notifications/unread-count?userId=<uuid>`

```json
{ "unreadCount": 3 }
```

### GET `/api/v1/notifications/{notificationId}`

### PUT `/api/v1/notifications/{notificationId}/read` — marks read, returns `NotificationResponse`

### PUT `/api/v1/notifications/read-all?userId=<uuid>` — `data: null`

### DELETE `/api/v1/notifications/{notificationId}` — `data: null`

---

# 13. Payment service (`:8112`)

Hospital JWT except public pay APIs. Super Admin cannot create links (no hospital schema).

## 13.1 Payments `/api/v1/payments`

### POST `/api/v1/payments/initiate`

```json
{
  "amount": 10,
  "productInfo": "Invoice GAN-DL-INV-00001",
  "firstName": "Pradeep",
  "email": "pswain1998@gmail.com",
  "phone": "8328840320",
  "invoiceId": "<uuid>",
  "patientId": "<uuid>",
  "frontendSuccessUrl": "https://ui.example/pay/success",
  "frontendFailureUrl": "https://ui.example/pay/fail"
}
```

Response (201) `PaymentResponse`:

```json
{
  "id": "<uuid>",
  "paymentCode": "PAY-…",
  "payuTxnId": "hosp_gan_dl.…",
  "amount": 10.00,
  "productInfo": "Invoice GAN-DL-INV-00001",
  "firstName": "Pradeep",
  "email": "pswain1998@gmail.com",
  "phone": "8328840320",
  "invoiceId": "<uuid>",
  "patientId": "<uuid>",
  "gateway": "PAYU",
  "status": "INITIATED",
  "payuStatus": null,
  "mihpayid": null,
  "paymentMode": null,
  "errorMessage": null,
  "refundedAmount": 0,
  "lastRefundRequestId": null,
  "createdAt": "2026-10-05T03:27:00",
  "updatedAt": "2026-10-05T03:27:00",
  "actionUrl": "https://test.payu.in/_payment"
}
```

Frontend POSTs PayU fields to `actionUrl`. PayU then hits success/failure callbacks.

### GET `/api/v1/payments?invoiceId=<optional>` — `PaymentResponse[]`

### GET `/api/v1/payments/by-txn/{txnId}`  
### GET `/api/v1/payments/{paymentId}`

### POST `/api/v1/payments/{paymentId}/refund`

```json
{ "amount": 10, "reason": "Patient cancelled" }
```

Body optional (full refund). Recalculates invoice via Kafka; emails patient.

### POST|GET `/api/v1/payments/payu/success` and `/failure`

PayU server callbacks (form POST). Not called by the UI with JWT.

## 13.2 Payment links `/api/v1/payments/links`

Valid **15 minutes**. Amount must be ≤ invoice `leftAmount`.

### POST `/api/v1/payments/links`

```json
{
  "invoiceId": "<uuid>",
  "patientId": "<uuid>",
  "amount": 10,
  "invoiceNumber": "GAN-DL-INV-00001",
  "firstName": "Pradeep",
  "email": "pswain1998@gmail.com",
  "phone": "8328840320",
  "payPageBaseUrl": "http://45.195.229.15:8112",
  "sendEmail": true,
  "upiQr": false
}
```

`sendEmail: true` → patient email, status `SHARED`. `upiQr: true` → UPI QR payload.

Response (201):

```json
{
  "id": "<uuid>",
  "token": "hosp_gan_dl.c8bcef0079154d07a9439cd6b2fdd018",
  "payUrl": "http://45.195.229.15:8112/pay/hosp_gan_dl.c8bcef0079154d07a9439cd6b2fdd018",
  "qrPayload": "http://45.195.229.15:8112/pay/hosp_gan_dl.c8bcef0079154d07a9439cd6b2fdd018",
  "invoiceId": "<uuid>",
  "invoiceNumber": "GAN-DL-INV-00001",
  "patientId": "<uuid>",
  "amount": 10.00,
  "firstName": "Pradeep",
  "email": "pswain1998@gmail.com",
  "phone": "8328840320",
  "createdAt": "2026-10-05T03:27:37",
  "sharedAt": "2026-10-05T03:27:37",
  "expiresAt": "2026-10-05T03:42:37",
  "status": "SHARED",
  "emailSent": true,
  "upiQr": false,
  "secondsRemaining": 900
}
```

Link status: `SHARED` | `PAID` | `EXPIRED` | `SUPERSEDED` (legacy `OPEN` treated as SHARED).

### GET `/api/v1/payments/links?page=0&size=20&status=&patientId=&invoiceId=&search=`

Paged `PaymentLinkResponse`.

### GET `/api/v1/payments/links/invoice/{invoiceId}` — active SHARED link

### GET `/api/v1/payments/links/{id}`

### POST `/api/v1/payments/links/{id}/email`

```json
{ "email": "override@gmail.com" }
```

Body optional. Resends using hospital SMTP.

## 13.3 Public (no JWT)

Token in path is the pay token (`hosp_gan_dl.…`), **not** the login JWT.

### GET `/api/v1/payments/public/{token}?payPageBaseUrl=`

Same `PaymentLinkResponse` as create.

### POST `/api/v1/payments/public/{token}/initiate?frontendSuccessUrl=&frontendFailureUrl=`

Starts PayU; returns `PaymentResponse` with `actionUrl`.

### GET `/pay/{token}` — HTML pay page  
### POST `/pay/{token}` — HTML form post to PayU

---

# 14. Suggested clinical sequence

1. Hospital login (`:8111`) → Bearer token.  
2. Ensure consultation types (`CONSULTATION`, `THERAPY`) exist (`:8103`).  
3. Create/list doctor (`:8102`).  
4. Book appointment (`POST :8103/api/v1/appointments`) **or** create patient then other flows.  
5. Medical assessment / prescription / therapies as needed.  
6. Invoice (`POST :8109/api/v1/invoices`).  
7. Cash: `POST /invoices/{id}/payments`. Online: `POST :8112/api/v1/payments/links` with `sendEmail: true`.  
8. Patient pays `payUrl` → PayU → invoice `COMPLETED`.

---

# 15. Out of scope

**Attendance** (`:8106`) is excluded as requested.

Live Swagger remains the try-out source if a field is added after this document.
