# Ayurvedaa API — New APIs Guide

**Updated:** 5 Aug 2026  
**Branch:** `fixes-development`

All responses (except raw file download) use:

```json
{
  "success": true,
  "status": 200,
  "message": "...",
  "data": {}
}
```

---

## Service ports

| Service | Port |
|---|---|
| Appointment | **8103** |
| Billing | **8109** |
| File upload | **8105** |
| Auth | **8111** |

---

## 1. Treatment (appointment-service `:8103`)

Table: `treatments`

**Status:** `SCHEDULED` | `ONGOING` | `COMPLETED`

### Create — `POST /api/v1/treatments`

```json
{
  "patientId": "uuid",
  "treatmentPlanName": "Panchakarma Detox",
  "startDate": "2026-08-05",
  "endDate": "2026-08-20",
  "totalSessions": 7,
  "completedSessions": 0,
  "assignedTherapistId": "uuid",
  "treatmentStatus": "SCHEDULED"
}
```

- `completedSessions` optional (default `0`)
- `treatmentStatus` optional (default `SCHEDULED`)
- `remainingSessions` = total − completed (server calculated)

### List all — `GET /api/v1/treatments`

### By patient — `GET /api/v1/treatments/patient/{patientId}`

### Update — `PUT /api/v1/treatments/{treatmentId}`

```json
{
  "treatmentPlanName": "Panchakarma Detox",
  "startDate": "2026-08-05",
  "endDate": "2026-08-20",
  "totalSessions": 7,
  "completedSessions": 2,
  "assignedTherapistId": "uuid"
}
```

### Change status — `PUT /api/v1/treatments/{treatmentId}/status`

```json
{
  "treatmentStatus": "ONGOING"
}
```

Only the status field changes.

### Response `data` item

```json
{
  "id": "uuid",
  "patientId": "uuid",
  "treatmentPlanName": "Panchakarma Detox",
  "startDate": "2026-08-05",
  "endDate": "2026-08-20",
  "totalSessions": 7,
  "completedSessions": 2,
  "remainingSessions": 5,
  "assignedTherapistId": "uuid",
  "assignedTherapistName": "Therapist Name",
  "treatmentStatus": "ONGOING"
}
```

---

## 2. Patient packages (billing-service `:8109`)

Table: `patient_packages`

**Status:** `SCHEDULED` | `ONGOING` | `COMPLETED`

### Create — `POST /api/v1/packages`

```json
{
  "patientId": "uuid",
  "packageName": "Detox Package",
  "validity": "2026-12-31",
  "status": "SCHEDULED",
  "discountApplied": 500
}
```

- `status` optional (default `SCHEDULED`)

### List all — `GET /api/v1/packages`

### By patient — `GET /api/v1/packages/patient/{patientId}`

### Update — `PUT /api/v1/packages/{packageId}`

```json
{
  "packageName": "Detox Package",
  "validity": "2026-12-31",
  "discountApplied": 500
}
```

### Change status — `PUT /api/v1/packages/{packageId}/status`

```json
{
  "status": "ONGOING"
}
```

Only the status field changes.

### Response `data` item

```json
{
  "id": "uuid",
  "patientId": "uuid",
  "packageName": "Detox Package",
  "validity": "2026-12-31",
  "status": "SCHEDULED",
  "discountApplied": 500
}
```

---

## 3. Follow-up (appointment-service `:8103`)

Table: `follow_ups`  
Does **not** auto-create an appointment booking.

**Status:** `UPCOMING` | `MISSED` | `COMPLETED` | `CANCELLED`

### Create — `POST /api/v1/follow-ups`

```json
{
  "patientId": "uuid",
  "assignedDoctorId": "uuid",
  "sourceBookingId": "uuid",
  "visitType": "CONSULTATION",
  "appointmentDate": "2026-10-15T01:05:00",
  "schedulingOption": "7_DAYS",
  "smsReminderEnabled": false,
  "status": "UPCOMING"
}
```

| Field | Notes |
|---|---|
| `visitType` | `CONSULTATION` \| `THERAPY` |
| `status` | Optional; default `UPCOMING` |
| `smsReminderEnabled` | Stored only; SMS send not implemented yet |
| `sourceBookingId` | Optional |

### List all — `GET /api/v1/follow-ups`  
(All Follow Ups tab)

### By patient — `GET /api/v1/follow-ups/patient/{patientId}`

### Change status — `PUT /api/v1/follow-ups/{followUpId}/status`

```json
{
  "status": "COMPLETED"
}
```

Only the status field changes.

### Cancel — `PUT /api/v1/follow-ups/{followUpId}/cancel`

No body. Sets status to `CANCELLED`.  
Error if already cancelled.

### GET response `data` item

```json
{
  "id": "uuid",
  "patientId": "uuid",
  "patientDisplayId": "#PT458652",
  "patientName": "Khushi Shroff",
  "assignedDoctorId": "uuid",
  "doctorName": "Dr. Sheekha",
  "sourceBookingId": "uuid",
  "visitType": "CONSULTATION",
  "appointmentDate": "2026-10-15T01:05:00",
  "schedulingOption": "7_DAYS",
  "smsReminderEnabled": false,
  "status": "UPCOMING"
}
```

Empty list returns `"data": []` with success.

---

## 4. Appointment list / today (appointment-service `:8103`)

### Today — all doctors

```http
GET /api/v1/appointments/today
```

### Today — one doctor

```http
GET /api/v1/appointments/doctor/{doctorId}/today
```

### By status (or all)

```http
GET /api/v1/appointments/status/ALL
GET /api/v1/appointments/status/SCHEDULED
```

- `ALL` = no status filter  
- Non-deleted only  
- Order: today → tomorrow → later; past dates after  

### Reschedule cancelled booking

```http
PUT /api/v1/appointments/{bookingId}/reschedule
```

Works for `CANCELLED` (also `SCHEDULED` / `RESCHEDULED`).  
Sets status to `RESCHEDULED` (active again).  
Not allowed for `COMPLETED` / `IN_CONSULTATION`.

---

## 5. Documents (file-upload-service `:8105`)

Documents are keyed by **patientId** (bookingId removed).

### Upload — `POST /api/v1/documents/upload` (multipart)

| Part | Type |
|---|---|
| `patientId` | UUID |
| `documentType` | `PAST_MEDICAL_REPORT` \| `PRESCRIPTION` \| `LAB_REPORT` |
| `file` | file |

### List — `GET /api/v1/documents/{patientId}`

### Download — `GET /api/v1/documents/{documentId}/download`

### Delete — `DELETE /api/v1/documents/{documentId}`

**DB migration (if old column exists):**

```sql
ALTER TABLE appointment_documents RENAME COLUMN booking_id TO patient_id;
```

---

## 6. Auth roles (auth-service `:8111`)

| Role |
|---|
| `SUPER_ADMIN` |
| `ADMIN` |
| `MANAGER` |
| `RECEPTIONIST` |
| `DIETICIAN` |
| `DOCTOR` |
| `CHEMIST` |

- Tenant register → first user `ADMIN`  
- Public signup → `RECEPTIONIST`  
- `POST /api/v1/auth/register-user` — role required; cannot create `SUPER_ADMIN`  

---

## Quick index

| Area | Method | Path | Service |
|---|---|---|---|
| Treatment | POST | `/api/v1/treatments` | :8103 |
| Treatment | GET | `/api/v1/treatments` | :8103 |
| Treatment | GET | `/api/v1/treatments/patient/{patientId}` | :8103 |
| Treatment | PUT | `/api/v1/treatments/{treatmentId}` | :8103 |
| Treatment | PUT | `/api/v1/treatments/{treatmentId}/status` | :8103 |
| Package | POST | `/api/v1/packages` | :8109 |
| Package | GET | `/api/v1/packages` | :8109 |
| Package | GET | `/api/v1/packages/patient/{patientId}` | :8109 |
| Package | PUT | `/api/v1/packages/{packageId}` | :8109 |
| Package | PUT | `/api/v1/packages/{packageId}/status` | :8109 |
| Follow-up | POST | `/api/v1/follow-ups` | :8103 |
| Follow-up | GET | `/api/v1/follow-ups` | :8103 |
| Follow-up | GET | `/api/v1/follow-ups/patient/{patientId}` | :8103 |
| Follow-up | PUT | `/api/v1/follow-ups/{followUpId}/status` | :8103 |
| Follow-up | PUT | `/api/v1/follow-ups/{followUpId}/cancel` | :8103 |
| Appointment | GET | `/api/v1/appointments/today` | :8103 |
| Appointment | GET | `/api/v1/appointments/status/{status\|ALL}` | :8103 |
| Documents | POST | `/api/v1/documents/upload` | :8105 |
| Documents | GET | `/api/v1/documents/{patientId}` | :8105 |

Swagger per service: `http://HOST:PORT/swagger-ui/index.html`
