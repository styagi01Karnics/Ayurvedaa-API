# Ayurvedaa API — Frontend Documentation

**Version:** 1.0  
**Date:** 3 Aug 2026  
**Base host (server):** `http://103.174.103.250`  
**Local:** `http://localhost`

All business APIs return a common wrapper unless noted (file download / biometric device).

---

## 1. Common response wrapper

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {}
}
```

| Field | Type | Description |
|---|---|---|
| `success` | boolean | `true` on success, `false` on error |
| `status` | number | HTTP-style status (200, 201, 400, 404, …) |
| `message` | string | Human-readable message |
| `data` | object / array / null | Payload |

---

## 2. Service ports

| Service | Port | Base URL example |
|---|---|---|
| Auth | **8100** | `http://103.174.103.250:8100` |
| Patient | **8101** | `http://103.174.103.250:8101` |
| Doctor | **8102** | `http://103.174.103.250:8102` |
| Appointment | **8103** | `http://103.174.103.250:8103` |
| Therapist | **8104** | `http://103.174.103.250:8104` |
| File Upload | **8105** | `http://103.174.103.250:8105` |
| Attendance | **8106** | `http://103.174.103.250:8106` |
| Activity Log | **8107** | `http://103.174.103.250:8107` |
| Medicine | **8108** | `http://103.174.103.250:8108` |
| Billing | **8109** | `http://103.174.103.250:8109` |
| Notification | **8110** | `http://103.174.103.250:8110` |

Swagger UI (per service): `http://HOST:PORT/swagger-ui/index.html`

---

## 3. Enums (use exact values)

| Enum | Values |
|---|---|
| `UserRole` | `SUPER_ADMIN`, `TENANT_ADMIN`, `DOCTOR`, `THERAPIST`, `STAFF` |
| `UserStatus` | `ACTIVE`, `INACTIVE`, `LOCKED` |
| `TenantStatus` | `ACTIVE`, `INACTIVE`, `SUSPENDED` |
| `PatientStatus` / `DoctorStatus` / `TherapistStatus` / `MedicineStatus` / `TherapyMasterStatus` / `TreatmentCategoryStatus` / `DoshaMasterStatus` | `ACTIVE`, `INACTIVE` |
| `Gender` | `MALE`, `FEMALE`, `OTHER` |
| `IdProofType` | `AADHAAR`, `PAN`, `PASSPORT`, `DRIVING_LICENSE`, `VOTER_ID` |
| `BookingStatus` | `SCHEDULED`, `CANCELLED`, `RESCHEDULED`, `IN_CONSULTATION`, `COMPLETED` |
| `ConsultationType` | `CONSULTATION`, `THERAPY` |
| `PatientListTab` | `ACTIVE`, `INACTIVE` |
| `TherapyStatus` | `SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |
| `DocumentType` | `PAST_MEDICAL_REPORT`, `PRESCRIPTION`, `LAB_REPORT` |
| `MedicineCategory` | `TABLET`, `SYRUP`, `POWDER` |
| `MedicineStockStatus` | `IN_STOCK`, `LOW_STOCK`, `OUT_OF_STOCK` |
| `InvoiceStatus` | `UNPAID`, `ONGOING`, `COMPLETED` |
| `BillingPeriod` | `WEEKLY`, `MONTHLY`, `YEARLY` |
| `BillSection` / `InvoiceItemType` | `SERVICE`, `MEDICINE`, `THERAPY` (+ `PACKAGE` for item type) |
| `VisitType` (billing) | `CONSULTATION`, `FOLLOW_UP`, `THERAPY`, `PACKAGE` |
| `AttendanceStatus` | `PRESENT`, `ABSENT`, `HALF_DAY`, `ON_LEAVE`, `LATE` |
| `StaffType` | `MANAGER`, `ENGINEER`, `HELPER`, `CONTRACTOR` |
| `NotificationType` | `APPOINTMENT`, `BILLING`, `MEDICINE`, `THERAPY`, `SYSTEM`, `GENERAL` |
| `NotificationPriority` | `LOW`, `MEDIUM`, `HIGH` |
| `ActivityAction` | `VIEWED`, `CREATED`, `UPDATED`, `DELETED` |

---

## 4. Auth Service (`:8100`)

### POST `/api/v1/auth/login`

**Request**
```json
{
  "usernameOrEmail": "admin@clinic.com",
  "password": "Secret@123"
}
```

**Response `data`**
```json
{
  "accessToken": "eyJhbGciOi...",
  "tokenType": "Bearer",
  "expiresInMs": 86400000,
  "user": {
    "id": "uuid",
    "tenantId": "uuid",
    "tenantCode": "GAN",
    "username": "admin",
    "email": "admin@clinic.com",
    "fullName": "Admin User",
    "role": "TENANT_ADMIN",
    "status": "ACTIVE"
  },
  "tenant": {
    "id": "uuid",
    "tenantCode": "GAN",
    "name": "Ganesha Clinic",
    "email": "clinic@example.com",
    "phone": "+91-9xxxxxxxxx",
    "address": "City",
    "status": "ACTIVE"
  }
}
```

### POST `/api/v1/auth/signup`

**Request**
```json
{
  "fullName": "Rahul Sharma",
  "username": "rahul",
  "email": "rahul@example.com",
  "password": "Secret@123",
  "confirmPassword": "Secret@123",
  "tenantCode": "GAN"
}
```

**Response:** same as login (`AuthTokenResponse`), HTTP 201.

### POST `/api/v1/auth/forgot-password`

**Request:** `{ "usernameOrEmail": "rahul@example.com" }`

**Response `data`**
```json
{
  "message": "Reset token generated",
  "resetToken": "token-string",
  "expiresAt": "2026-08-03T18:00:00"
}
```

### POST `/api/v1/auth/reset-password`

**Request**
```json
{
  "token": "reset-token",
  "newPassword": "NewSecret@123",
  "confirmPassword": "NewSecret@123"
}
```

### POST `/api/v1/auth/validate`

Header: `Authorization: Bearer <token>` (optional)

**Response `data`**
```json
{
  "valid": true,
  "userId": "uuid",
  "tenantId": "uuid",
  "tenantCode": "GAN",
  "email": "admin@clinic.com",
  "role": "TENANT_ADMIN"
}
```

### POST `/api/v1/auth/register-user` — HTTP 201

**Request**
```json
{
  "fullName": "Staff One",
  "username": "staff1",
  "email": "staff1@example.com",
  "password": "Secret@123",
  "role": "STAFF"
}
```

**Response `data`:** `UserResponse` (see login).

### GET `/api/v1/auth/me` → `UserResponse`  
### GET `/api/v1/auth/users` → `UserResponse[]`  
### GET `/api/v1/auth/tenant` → `TenantResponse`

### POST `/api/v1/tenants/register` — HTTP 201

**Request**
```json
{
  "tenantCode": "GAN",
  "name": "Ganesha Clinic",
  "email": "clinic@example.com",
  "phone": "+91-9xxxxxxxxx",
  "address": "Address",
  "adminFullName": "Admin",
  "adminUsername": "admin",
  "adminEmail": "admin@clinic.com",
  "adminPassword": "Secret@123"
}
```

**Response `data`:** `TenantResponse`

---

## 5. Patient Service (`:8101`)

### POST `/api/v1/patients/create-patient` — HTTP 201

**Request**
```json
{
  "fullName": "Khushi Shroff",
  "gender": "FEMALE",
  "dateOfBirth": "1998-05-12",
  "age": 28,
  "preferredLanguage": "English",
  "mobileNumber": "9205061339",
  "email": "khushi@example.com",
  "state": "Maharashtra",
  "city": "Mumbai",
  "address": "Andheri West",
  "emergencyContactName": "Parent",
  "emergencyRelationship": "Father",
  "emergencyPhoneNumber": "9xxxxxxxxx",
  "idProofType": "AADHAAR",
  "idProofNumber": "xxxx-xxxx-xxxx",
  "occupation": "Student",
  "insuranceDetails": "None"
}
```

**Response `data`**
```json
{
  "id": "uuid",
  "patientDisplayId": "#PT458652",
  "patientCode": "GAN2025-0129",
  "firstName": "Khushi",
  "lastName": "Shroff",
  "fullName": "Khushi Shroff",
  "gender": "FEMALE",
  "dateOfBirth": "1998-05-12",
  "age": 28,
  "preferredLanguage": "English",
  "email": "khushi@example.com",
  "mobileNumber": "9205061339",
  "state": "Maharashtra",
  "city": "Mumbai",
  "address": "Andheri West",
  "emergencyContactName": "Parent",
  "emergencyRelationship": "Father",
  "emergencyPhoneNumber": "9xxxxxxxxx",
  "idProofType": "AADHAAR",
  "idProofNumber": "xxxx-xxxx-xxxx",
  "occupation": "Student",
  "insuranceDetails": "None",
  "status": "ACTIVE",
  "createdAt": "2026-08-03T10:00:00",
  "updatedAt": "2026-08-03T10:00:00"
}
```

| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/patients/get-patient/{patientId}` | `PatientResponse` |
| GET | `/api/v1/patients/get-all-patients` | `PatientResponse[]` |
| GET | `/api/v1/patients/get-patient-count` | `number` (Long) |
| DELETE | `/api/v1/patients/delete-patient/{patientId}` | `null` (soft delete → INACTIVE) |

---

## 6. Doctor Service (`:8102`)

### POST `/api/v1/doctors` — HTTP 201

**Request**
```json
{
  "name": "Dr. Sheekha",
  "specialization": "Panchakarma",
  "status": "ACTIVE",
  "consultationFees": 500,
  "followUpFees": 300,
  "availability": "Mon-Fri 10AM-5PM"
}
```

**Response `data`**
```json
{
  "id": "uuid",
  "name": "Dr. Sheekha",
  "specialization": "Panchakarma",
  "status": "ACTIVE",
  "consultationFees": 500,
  "followUpFees": 300,
  "availability": "Mon-Fri 10AM-5PM"
}
```

| Method | Path | Body / Notes |
|---|---|---|
| GET | `/api/v1/doctors` | List |
| GET | `/api/v1/doctors/{doctorId}` | By id |
| PATCH | `/api/v1/doctors/{doctorId}/status` | `{ "status": "INACTIVE" }` |
| DELETE | `/api/v1/doctors/{doctorId}` | Soft delete |

---

## 7. Appointment Service (`:8103`)

### 7.1 Book appointment — POST `/api/v1/appointments` — HTTP 201

**Request**
```json
{
  "patient": {
    "fullName": "Khushi Shroff",
    "gender": "FEMALE",
    "dateOfBirth": "1998-05-12",
    "age": 28,
    "preferredLanguage": "English",
    "mobileNumber": "9205061339",
    "email": "khushi@example.com",
    "state": "Maharashtra",
    "city": "Mumbai",
    "address": "Andheri",
    "emergencyContactName": "Parent",
    "emergencyRelationship": "Father",
    "emergencyPhoneNumber": "9xxxxxxxxx",
    "idProofType": "AADHAAR",
    "idProofNumber": "xxxx",
    "occupation": "Student",
    "insuranceDetails": "None"
  },
  "registrationDate": "2026-10-15",
  "slotTime": "01:05:00",
  "assignedDoctorId": "doctor-uuid",
  "consultationTypes": ["CONSULTATION"]
}
```

**Response `data`**
```json
{
  "id": "booking-uuid",
  "patientId": "patient-uuid",
  "patient": { "...PatientSummary..." },
  "registrationDate": "2026-10-15",
  "slotTime": "01:05:00",
  "assignedDoctorId": "doctor-uuid",
  "assignedDoctor": {
    "id": "doctor-uuid",
    "name": "Dr. Sheekha",
    "specialization": "Panchakarma",
    "status": "ACTIVE",
    "consultationFees": 500,
    "followUpFees": 300,
    "availability": "Mon-Fri"
  },
  "consultationTypes": ["CONSULTATION"],
  "bookingStatus": "SCHEDULED"
}
```

### 7.2 Patients page (Active / Inactive tabs)

**GET** `/api/v1/appointments/patients`

| Query | Required | Values |
|---|---|---|
| `statusTab` | Yes | `ACTIVE` / `INACTIVE` |
| `search` | No | Patient id / code / name / mobile |
| `bookingStatus` | No | `BookingStatus` |
| `consultationType` | No | `CONSULTATION` / `THERAPY` |
| `doshaId` | No | UUID |
| `doctorId` | No | UUID |

**Response `data[]`**
```json
{
  "bookingId": "uuid",
  "patientId": "uuid",
  "patientDisplayId": "#PT458652",
  "patientCode": "GAN2025-0129",
  "patientFullName": "Khushi Shroff",
  "patientMobileNumber": "+91-9205061339",
  "assignedDoctorId": "uuid",
  "doctorName": "Dr. Sheekha",
  "consultationTypes": ["CONSULTATION"],
  "appointmentDate": "2026-10-15",
  "slotTime": "01:05:00",
  "bookingTime": "2026-10-14T12:00:00",
  "doshaId": "uuid",
  "doshaName": "Vata",
  "bookingStatus": "COMPLETED"
}
```

### 7.3 Other booking endpoints

| Method | Path | Notes |
|---|---|---|
| GET | `/api/v1/appointments/stats` | Dashboard counts |
| GET | `/api/v1/appointments/cancelled` | Cancelled list |
| GET | `/api/v1/appointments/today/{consultationType}` | `CONSULTATION` or `THERAPY` |
| GET | `/api/v1/appointments/doctor/{doctorId}/today` | Doctor today schedule |
| GET | `/api/v1/appointments/{bookingId}` | By booking id |
| GET | `/api/v1/appointments/patient/{patientId}` | By patient |
| GET | `/api/v1/appointments/status/{bookingStatus}` | By status |
| GET | `/api/v1/appointments/date/{registrationDate}` | `yyyy-MM-dd` |
| PUT | `/api/v1/appointments/{bookingId}/reschedule` | Body below |
| PUT | `/api/v1/appointments/{bookingId}/cancel` | No body |
| PUT | `/api/v1/appointments/{bookingId}/in-consultation` | Start consult |
| PUT | `/api/v1/appointments/{bookingId}/complete` | Complete |
| DELETE | `/api/v1/appointments/{bookingId}` | Soft delete |

**Reschedule body**
```json
{
  "patientId": "uuid",
  "registrationDate": "2026-10-16",
  "slotTime": "10:30:00",
  "assignedDoctorId": "uuid",
  "consultationTypes": ["CONSULTATION"]
}
```

**Stats response `data`**
```json
{
  "currentMonthAppointmentCount": 40,
  "completedCount": 25,
  "ongoingCount": 3,
  "todayAppointmentCount": 8
}
```

### 7.4 Dashboard — today's schedule

**GET** `/api/v1/dashboard/todays-schedule?doctorId={optionalUuid}`

```json
{
  "date": "2026-08-03",
  "currentDateTime": "2026-08-03T17:00:00",
  "ongoingAppointment": {
    "bookingId": "uuid",
    "patientId": "uuid",
    "patientName": "Name",
    "serviceType": "CONSULTATION",
    "bookingStatus": "IN_CONSULTATION"
  },
  "nextAppointment": { "...same shape..." },
  "remainingToday": 4
}
```

### 7.5 Therapy master — `/api/v1/therapies`

**POST** create
```json
{
  "name": "Kayakalpa",
  "categoryId": "category-uuid",
  "status": "ACTIVE",
  "durationMinutes": 45,
  "price": 1800,
  "description": "Therapeutic massage"
}
```

**Response `data`**
```json
{
  "id": "uuid",
  "name": "Kayakalpa",
  "therapyName": "Kayakalpa",
  "therapyCode": "TH003",
  "categoryId": "uuid",
  "categoryName": "Massage Therapy",
  "status": "ACTIVE",
  "durationMinutes": 45,
  "price": 1800,
  "description": "Therapeutic massage"
}
```

| Method | Path |
|---|---|
| GET | `/api/v1/therapies?status=ACTIVE` |
| GET | `/api/v1/therapies/category/{categoryId}?status=` |
| GET | `/api/v1/therapies/{therapyId}` |
| PUT | `/api/v1/therapies/{therapyId}` (same body as create) |
| PATCH | `/api/v1/therapies/{therapyId}/status` → `{ "status": "INACTIVE" }` |
| DELETE | `/api/v1/therapies/{therapyId}` |

### 7.6 Treatment categories — `/api/v1/treatment-categories`

**POST**
```json
{
  "categoryName": "Massage Therapy",
  "description": "Oil and herbal massage",
  "status": "ACTIVE"
}
```

**Response:** `{ "id", "categoryCode", "categoryName", "description", "status" }`

| GET | `/api/v1/treatment-categories` | List |
| GET | `/api/v1/treatment-categories/{categoryId}` | By id |

### 7.7 Dosha master — `/api/v1/doshas`

**POST**
```json
{
  "name": "Vata",
  "elements": "Air + Ether",
  "characteristics": "Dry, light, cold",
  "status": "ACTIVE"
}
```

**Response:** `{ "id", "name", "elements", "characteristics", "status" }`

### 7.8 Appointment therapy — `/api/v1/appointment-therapies`

**POST**
```json
{
  "patientId": "uuid",
  "treatmentCategoryId": "uuid",
  "assignedTherapistId": "uuid",
  "scheduleDate": "2026-08-10",
  "scheduleTime": "11:00:00",
  "sessionDuration": 45,
  "sessionFrequency": 1,
  "therapyInstructions": "Oil massage",
  "remarks": "Notes",
  "therapyIds": ["therapy-uuid-1", "therapy-uuid-2"]
}
```

| GET | `/api/v1/appointment-therapies/{patientId}` |
| GET | `/api/v1/appointment-therapies/therapist/{therapistId}/today` |

### 7.9 Medical assessment

**POST** `/api/v1/medical-assessment` (JSON)

```json
{
  "patientId": "uuid",
  "ayurvedicAssessment": {
    "patientId": "uuid",
    "doshaId": "uuid",
    "bodyConstitution": "Vata-Pitta",
    "currentImbalances": "Vata elevated"
  },
  "physicalExamination": {
    "patientId": "uuid",
    "weight": 62.5,
    "height": 165,
    "ibw": 58,
    "pulse": 72,
    "bp": "120/80",
    "temperature": 98.6,
    "pallor": "Absent",
    "icterus": "Absent",
    "cyanosis": "Absent",
    "lymphNodes": "NAD",
    "oedema": "Absent",
    "sensorium": "Alert",
    "acidityGas": "Mild",
    "motion": "Normal",
    "micturition": "Normal"
  },
  "medicalHistory": {
    "patientId": "uuid",
    "pastMedicalConditions": "...",
    "pastSurgeries": "...",
    "currentMedications": "...",
    "allergies": "...",
    "familyHistory": "..."
  },
  "lifestyleInformation": {
    "patientId": "uuid",
    "dietType": "Vegetarian",
    "sleepPattern": "6-7 hrs",
    "exerciseHabits": "Walking",
    "addiction": "None"
  },
  "systemicExamination": {
    "patientId": "uuid",
    "cardiovascular": "NAD",
    "respiratory": "NAD",
    "nervous": "NAD",
    "abdomenGi": "Soft",
    "locomotor": "NAD"
  },
  "treatmentPlan": {
    "patientId": "uuid",
    "investigationAndPlanSuggested": "CBC",
    "planTaken": "Panchakarma"
  }
}
```

**POST** `/api/v1/medical-assessment/with-documents` — `multipart/form-data`  
- `data` = JSON (`CreateMedicalAssessmentRequest`)  
- `pastMedicalReports` = files  
- `prescriptions` = files  
- `labReports` = files  

**GET** `/api/v1/medical-assessment/{patientId}`

Also available as separate resources:

| POST / GET | Path |
|---|---|
| medical histories | `/api/v1/medical-histories` |
| lifestyle | `/api/v1/lifestyle-information` |
| physical exam | `/api/v1/physical-examinations` |
| systemic exam | `/api/v1/systemic-examinations` |
| ayurvedic | `/api/v1/ayurvedic-assessments` |
| treatment plans | `/api/v1/treatment-plans` |

---

## 8. Therapist Service (`:8104`)

### POST `/api/v1/therapists` — HTTP 201

**Request**
```json
{
  "name": "Dr. Rahul Verma",
  "status": "ACTIVE",
  "assignedTherapyIds": ["therapy-uuid-1", "therapy-uuid-2"]
}
```

**Response `data`**
```json
{
  "id": "uuid",
  "name": "Dr. Rahul Verma",
  "therapistName": "Dr. Rahul Verma",
  "therapistCode": "THP-0002",
  "status": "ACTIVE",
  "assignedTherapies": [
    { "id": "therapy-uuid-1", "name": "Kayakalpa" },
    { "id": "therapy-uuid-2", "name": "Podikizhi" }
  ]
}
```

| Method | Path | Body |
|---|---|---|
| PUT | `/api/v1/therapists/{id}` | Same as create |
| GET | `/api/v1/therapists` | List |
| GET | `/api/v1/therapists/{id}` | By id |
| GET | `/api/v1/therapists/by-therapies?therapyIds=uuid1&therapyIds=uuid2` | Filter |
| PATCH | `/api/v1/therapists/{id}/status` | `{ "status": "INACTIVE" }` |
| DELETE | `/api/v1/therapists/{id}` | Soft delete |

---

## 9. File Upload Service (`:8105`)

### POST `/api/v1/documents/upload` — `multipart/form-data`

| Part / param | Type |
|---|---|
| `bookingId` | UUID |
| `documentType` | `PAST_MEDICAL_REPORT` / `PRESCRIPTION` / `LAB_REPORT` |
| `file` | file |

**Response `data`**
```json
{
  "id": "uuid",
  "bookingId": "uuid",
  "documentType": "PRESCRIPTION",
  "fileName": "rx.pdf",
  "fileType": "application/pdf",
  "fileSize": 10240,
  "downloadUrl": "/api/v1/documents/{documentId}/download"
}
```

| Method | Path | Notes |
|---|---|---|
| GET | `/api/v1/documents/{bookingId}` | List for booking |
| GET | `/api/v1/documents/{documentId}/download` | **Raw file** (not ApiResponse) |
| DELETE | `/api/v1/documents/{documentId}` | Soft delete |

---

## 10. Attendance Service (`:8106`)

### POST `/api/v1/attendances/check-in` — HTTP 201

**Request**
```json
{
  "empId": "EMP001",
  "empName": "Ravi Kumar",
  "staffType": "HELPER",
  "attendanceDate": "2026-08-03",
  "checkInTime": "2026-08-03T09:05:00",
  "status": "PRESENT"
}
```

**Response `data`**
```json
{
  "id": "uuid",
  "serialNumber": "ATT-0001",
  "empId": "EMP001",
  "empName": "Ravi Kumar",
  "staffType": "HELPER",
  "attendanceDate": "2026-08-03",
  "checkInTime": "2026-08-03T09:05:00",
  "checkOutTime": null,
  "status": "PRESENT",
  "createdAt": "2026-08-03T09:05:00",
  "updatedAt": "2026-08-03T09:05:00"
}
```

| Method | Path | Body |
|---|---|---|
| PUT | `/api/v1/attendances/{id}/check-out` | `{ "checkOutTime": "2026-08-03T18:00:00" }` |
| PUT | `/api/v1/attendances/{id}/status` | `{ "status": "HALF_DAY" }` |
| GET | `/api/v1/attendances` | List |
| GET | `/api/v1/attendances/{id}` | By id |
| GET | `/api/v1/attendances/employee/{empId}` | By employee |
| DELETE | `/api/v1/attendances/{id}` | Soft delete |

> Biometric `/iclock/*` routes are device-only (plain text) — not for frontend UI.

---

## 11. Activity Log Service (`:8107`)

### POST `/api/v1/activity-logs` — HTTP 201

**Request**
```json
{
  "page": "Patients",
  "action": "UPDATED",
  "target": "Patient #PT458652",
  "beforeValue": "ACTIVE",
  "afterValue": "INACTIVE",
  "activityTimestamp": "2026-08-03T12:00:00",
  "performedByUserId": "uuid",
  "performedByUserName": "Rahul Sharma",
  "performedByRole": "SUPER_ADMIN"
}
```

**Response `data`**
```json
{
  "id": "uuid",
  "page": "Patients",
  "action": "UPDATED",
  "target": "Patient #PT458652",
  "before": "ACTIVE",
  "after": "INACTIVE",
  "timestamp": "2026-08-03T12:00:00",
  "performedByUserId": "uuid",
  "performedByUserName": "Rahul Sharma",
  "performedByRole": "SUPER_ADMIN"
}
```

| GET | `/api/v1/activity-logs?page=&action=&search=` |
| GET | `/api/v1/activity-logs/{id}` |

---

## 12. Medicine Service (`:8108`)

### POST `/api/v1/medicines` — HTTP 201

Accepts **one object** or **array**:

```json
{
  "medicineName": "Ashwagandha",
  "category": "POWDER",
  "manufacturer": "Ayur Co",
  "batchNumber": "B001",
  "quantity": 100,
  "expiryDate": "2027-12-31",
  "purchasePrice": 80,
  "sellingPrice": 120,
  "lowStockAlertEnabled": true,
  "lowStockThreshold": 20,
  "status": "ACTIVE"
}
```

**Response `data`:** `MedicineResponse[]`

```json
{
  "id": "uuid",
  "medicineName": "Ashwagandha",
  "category": "POWDER",
  "manufacturer": "Ayur Co",
  "batchNumber": "B001",
  "stockQuantity": 100,
  "expiryDate": "2027-12-31",
  "purchasePrice": 80,
  "sellingPrice": 120,
  "price": 120,
  "lowStockAlertEnabled": true,
  "lowStockThreshold": 20,
  "status": "ACTIVE",
  "stockStatus": "IN_STOCK"
}
```

| Method | Path | Notes |
|---|---|---|
| PUT | `/api/v1/medicines/{id}` | Same fields as create |
| GET | `/api/v1/medicines?medicineName=&category=&stockStatus=` | List/filter |
| GET | `/api/v1/medicines/{id}` | By id |
| GET | `/api/v1/medicines/stock/summary` | Totals by category |
| GET | `/api/v1/medicines/stock/category/{category}` | One category |
| GET | `/api/v1/medicines/low-stock` | Low stock list |
| GET | `/api/v1/medicines/meta/categories` | `["TABLET","SYRUP","POWDER"]` |
| GET | `/api/v1/medicines/meta/manufacturers` | `string[]` |
| GET | `/api/v1/medicines/meta/names` | `[{ "id", "medicineName" }]` |
| POST | `/api/v1/medicines/{id}/stock/deduct` | `{ "quantity": 2 }` |
| POST | `/api/v1/medicines/{id}/stock/restore` | `{ "quantity": 2 }` |
| DELETE | `/api/v1/medicines/{id}` | Soft delete → INACTIVE |

### Dashboard — medicine stock

**GET** `/api/v1/dashboard/medicine-stock?lowStockLimit=5`

```json
{
  "totalStock": 500,
  "tablets": 200,
  "syrups": 150,
  "powder": 150,
  "statusBreakdown": {
    "inStock": 40,
    "outOfStock": 2,
    "lowStock": 5
  },
  "lowStockItems": [ /* MedicineResponse[] */ ]
}
```

---

## 13. Billing Service (`:8109`)

### POST `/api/v1/invoices` — HTTP 201

**Request**
```json
{
  "patientId": "uuid",
  "patientDisplayId": "PT458652",
  "patientCode": "GAN2025-0129",
  "patientName": "Khushi Shroff",
  "contactNumber": "9205061339",
  "invoiceDate": "2026-08-03",
  "visitType": "CONSULTATION",
  "serviceFees": 500,
  "packageType": null,
  "packageCharges": 0,
  "medicines": [
    {
      "medicineId": "uuid",
      "quantity": 2,
      "unitPrice": 120
    }
  ],
  "therapies": [
    {
      "itemName": "Kayakalpa",
      "quantity": 1,
      "unitPrice": 1800,
      "assignedTherapistId": "uuid",
      "assignedTherapistName": "Dr. Rahul",
      "scheduleDate": "2026-08-10",
      "scheduleTime": "11:00:00",
      "sessionDuration": 45,
      "sessionFrequency": 1
    }
  ],
  "discount": 0,
  "taxEnabled": true,
  "cgstPercent": 9,
  "sgstPercent": 9,
  "amountPaid": 500,
  "paymentMethod": "UPI",
  "paymentRemarks": "Advance"
}
```

**Response `data` (InvoiceResponse)** includes: `invoiceId`, amounts (`subtotal`, `totalAmount`, `paidAmount`, `leftAmount`), `status` (`UNPAID`/`ONGOING`/`COMPLETED`), `billSections`, `items[]`, `payments[]`.

| Method | Path | Notes |
|---|---|---|
| GET | `/api/v1/invoices?patientId=&status=` | List (`InvoiceListResponse[]`) |
| GET | `/api/v1/invoices/{invoiceId}` | Detail |
| POST | `/api/v1/invoices/{invoiceId}/payments` | Part payment |
| DELETE | `/api/v1/invoices/{invoiceId}` | Soft delete |

**Part payment body**
```json
{
  "amountPaid": 200,
  "paymentMethod": "CASH",
  "remarks": "Second installment"
}
```

### Sales

| Method | Path |
|---|---|
| GET | `/api/v1/sales?serviceType=&dateCreated=yyyy-MM-dd` |
| GET | `/api/v1/sales/revenue/month?year=2026&month=8` |

### Dashboard billing

**GET** `/api/v1/dashboard/billing-summary?period=MONTHLY`

```json
{
  "period": "MONTHLY",
  "fromDate": "2026-08-01",
  "toDate": "2026-08-31",
  "totalRevenue": 125000,
  "totalBillsGenerated": 40,
  "pendingPayments": 15000,
  "collectedPayments": 110000
}
```

---

## 14. Notification Service (`:8110`)

### POST `/api/v1/notifications` — HTTP 201

**Request**
```json
{
  "recipientUserId": "uuid",
  "recipientUserName": "Dr. Sheekha",
  "recipientRole": "DOCTOR",
  "title": "New appointment",
  "message": "Patient Khushi booked for 10:30",
  "type": "APPOINTMENT",
  "priority": "MEDIUM",
  "referenceId": "booking-uuid",
  "referenceType": "APPOINTMENT"
}
```

**Response `data`**
```json
{
  "id": "uuid",
  "recipientUserId": "uuid",
  "recipientUserName": "Dr. Sheekha",
  "recipientRole": "DOCTOR",
  "title": "New appointment",
  "message": "Patient Khushi booked for 10:30",
  "type": "APPOINTMENT",
  "priority": "MEDIUM",
  "referenceId": "booking-uuid",
  "referenceType": "APPOINTMENT",
  "read": false,
  "readAt": null,
  "createdAt": "2026-08-03T10:00:00"
}
```

| Method | Path |
|---|---|
| GET | `/api/v1/notifications?userId={uuid}&unreadOnly=&type=` |
| GET | `/api/v1/notifications/unread-count?userId={uuid}` → `{ "unreadCount": 3 }` |
| GET | `/api/v1/notifications/{notificationId}` |
| PUT | `/api/v1/notifications/{notificationId}/read` |
| PUT | `/api/v1/notifications/read-all?userId={uuid}` |
| DELETE | `/api/v1/notifications/{notificationId}` |

---

## 15. Frontend quick map (screens → APIs)

| Screen | Primary API |
|---|---|
| Login / Signup | Auth `:8100` |
| Patients (Active/Inactive) | `GET :8103/api/v1/appointments/patients` |
| Book appointment | `POST :8103/api/v1/appointments` |
| Doctors | Doctor `:8102` |
| Therapists | Therapist `:8104` |
| Therapies / categories / doshas | Appointment `:8103` |
| Medicine inventory | Medicine `:8108` |
| Billing / invoices / sales | Billing `:8109` |
| Dashboard cards | `:8103/dashboard/todays-schedule`, `:8108/dashboard/medicine-stock`, `:8109/dashboard/billing-summary` |
| Documents upload | File `:8105` |
| Attendance | Attendance `:8106` |
| Notifications | Notification `:8110` |
| Activity logs | Activity `:8107` |

---

## 16. Notes for frontend

1. Dates: `yyyy-MM-dd`. Times: `HH:mm:ss`. DateTimes: ISO local (`2026-08-03T10:00:00`).
2. IDs are UUIDs unless noted (`patientDisplayId`, `patientCode`, `invoiceId`, `therapistCode`, etc.).
3. Soft delete endpoints usually return success with `data: null`.
4. Status PATCH endpoints change `ACTIVE`/`INACTIVE` only — they do **not** soft-delete.
5. Patients page tabs use **booking** status (`statusTab`), not patient master status.
6. Medicine create accepts a single object **or** an array in one POST.
7. Swagger is available per service for live try-out.

---

*Generated from Ayurvedaa-API source (controllers + DTOs). For live schemas, use each service’s Swagger UI.*
