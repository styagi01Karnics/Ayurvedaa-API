# Ayurvedaa API — Frontend Guide

**Updated:** 4 Aug 2026  
**Branch baseline:** `fixes-development`

All APIs (except file download) wrap data in:

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {}
}
```

| Field | Type |
|---|---|
| `success` | boolean |
| `status` | number |
| `message` | string |
| `data` | object / array / null |

---

## 1. Service ports

| Service | Port | Base URL |
|---|---|---|
| Auth | **8111** | `http://HOST:8111` |
| Patient | **8101** | `http://HOST:8101` |
| Doctor | **8102** | `http://HOST:8102` |
| Appointment | **8103** | `http://HOST:8103` |
| Therapist | **8104** | `http://HOST:8104` |
| File Upload | **8105** | `http://HOST:8105` |
| Attendance | **8106** | `http://HOST:8106` |
| Activity Log | **8107** | `http://HOST:8107` |
| Medicine | **8108** | `http://HOST:8108` |
| Billing | **8109** | `http://HOST:8109` |
| Notification | **8110** | `http://HOST:8110` |

Swagger: `http://HOST:PORT/swagger-ui/index.html`

---

## 2. Common enums

| Enum | Values |
|---|---|
| `UserRole` (auth) | `SUPER_ADMIN`, `ADMIN`, `MANAGER`, `RECEPTIONIST`, `DIETICIAN`, `DOCTOR`, `CHEMIST` |
| Status (most masters) | `ACTIVE`, `INACTIVE` |
| `Gender` | `MALE`, `FEMALE`, `OTHER` |
| `BookingStatus` | `SCHEDULED`, `CANCELLED`, `RESCHEDULED`, `IN_CONSULTATION`, `COMPLETED` |
| `ConsultationType` | `CONSULTATION`, `THERAPY` |
| `PatientListTab` | `ACTIVE`, `INACTIVE` |
| `MedicineCategory` | `TABLET`, `SYRUP`, `POWDER` |
| `MedicineStockStatus` | `IN_STOCK`, `LOW_STOCK`, `OUT_OF_STOCK` |
| `TherapyStatus` | `SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |
| `DocumentType` | `PAST_MEDICAL_REPORT`, `PRESCRIPTION`, `LAB_REPORT` |
| `InvoiceStatus` | `UNPAID`, `ONGOING`, `COMPLETED` |
| `BillingPeriod` | `WEEKLY`, `MONTHLY`, `YEARLY` |
| `IdProofType` | `AADHAAR`, `PAN`, `PASSPORT`, `DRIVING_LICENSE`, `VOTER_ID` |

---

## 3. Auth (`:8111`)

### Login — `POST /api/v1/auth/login`

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
    "role": "ADMIN",
    "status": "ACTIVE"
  },
  "tenant": {
    "id": "uuid",
    "tenantCode": "GAN",
    "name": "Ganesha Clinic",
    "email": "clinic@example.com",
    "phone": "9876543210",
    "address": "Mumbai",
    "status": "ACTIVE"
  }
}
```

| Method | Path |
|---|---|
| POST | `/api/v1/auth/signup` |
| POST | `/api/v1/auth/forgot-password` |
| POST | `/api/v1/auth/reset-password` |
| GET | `/api/v1/auth/me` |
| GET | `/api/v1/auth/users` |
| POST | `/api/v1/auth/register-user` |
| POST | `/api/v1/tenants/register` |

### Register user — `POST /api/v1/auth/register-user` (ADMIN / SUPER_ADMIN)

```json
{
  "fullName": "Dr. Meera",
  "username": "meera",
  "email": "meera@clinic.com",
  "password": "Secret@123",
  "role": "DOCTOR"
}
```

`role` must be one of: `ADMIN`, `MANAGER`, `RECEPTIONIST`, `DIETICIAN`, `DOCTOR`, `CHEMIST` (`SUPER_ADMIN` is blocked).

Tenant register creates the first user as `ADMIN`. Public signup defaults to `RECEPTIONIST`.

---

## 4. Patient (`:8101`)

### Create — `POST /api/v1/patients/create-patient`

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
  "emergencyContactName": "Ramesh Shroff",
  "emergencyRelationship": "Father",
  "emergencyPhoneNumber": "9876543210",
  "idProofType": "AADHAAR",
  "idProofNumber": "123456789012",
  "occupation": "Student",
  "insuranceDetails": "None"
}
```

**Response `data` (key fields)**
```json
{
  "id": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
  "patientDisplayId": "#PT458652",
  "patientCode": "GAN2026-0001",
  "fullName": "Khushi Shroff",
  "mobileNumber": "9205061339",
  "status": "ACTIVE"
}
```

| Method | Path |
|---|---|
| GET | `/api/v1/patients/get-patient/{patientId}` |
| GET | `/api/v1/patients/get-all-patients` |
| GET | `/api/v1/patients/get-patient-count` |
| DELETE | `/api/v1/patients/delete-patient/{patientId}` |

---

## 5. Doctor (`:8102`)

### Create — `POST /api/v1/doctors`

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

| Method | Path | Notes |
|---|---|---|
| GET | `/api/v1/doctors` | All non-deleted (ACTIVE + INACTIVE) |
| GET | `/api/v1/doctors/active` | **ACTIVE only** (use for booking dropdown) |
| GET | `/api/v1/doctors/{doctorId}` | By id |
| PATCH | `/api/v1/doctors/{doctorId}/status` | `{ "status": "INACTIVE" }` |
| DELETE | `/api/v1/doctors/{doctorId}` | Soft delete |

---

## 6. Appointment (`:8103`)

### 6.1 Book appointment — `POST /api/v1/appointments`

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
    "address": "Andheri West",
    "emergencyContactName": "Ramesh Shroff",
    "emergencyRelationship": "Father",
    "emergencyPhoneNumber": "9876543210",
    "idProofType": "AADHAAR",
    "idProofNumber": "123456789012",
    "occupation": "Student",
    "insuranceDetails": "None"
  },
  "registrationDate": "2026-10-15",
  "slotTime": "10:30:00",
  "assignedDoctorId": "doctor-uuid",
  "consultationTypes": ["CONSULTATION"]
}
```

`consultationTypes`: `CONSULTATION` and/or `THERAPY`.

### 6.2 Patients page — `GET /api/v1/appointments/patients`

| Query | Required | Values |
|---|---|---|
| `statusTab` | Yes | `ACTIVE` = SCHEDULED + RESCHEDULED; `INACTIVE` = COMPLETED + CANCELLED |
| `search` | No | id / code / name / mobile |
| `bookingStatus` | No | must belong to tab |
| `consultationType` | No | `CONSULTATION` / `THERAPY` |
| `doshaId` | No | UUID |
| `doctorId` | No | UUID |

**Response row**
```json
{
  "bookingId": "uuid",
  "patientId": "uuid",
  "patientDisplayId": "#PT458652",
  "patientCode": "GAN2026-0001",
  "patientFullName": "Khushi Shroff",
  "patientMobileNumber": "9205061339",
  "assignedDoctorId": "uuid",
  "doctorName": "Dr. Sheekha",
  "consultationTypes": ["CONSULTATION"],
  "appointmentDate": "2026-10-15",
  "slotTime": "10:30:00",
  "bookingTime": "2026-10-14T12:00:00",
  "doshaId": "uuid",
  "doshaName": "Vata",
  "bookingStatus": "COMPLETED"
}
```

### 6.3 Reschedule — `PUT /api/v1/appointments/{bookingId}/reschedule`

```json
{
  "patientId": "patient-uuid",
  "registrationDate": "2026-10-16",
  "slotTime": "11:00:00",
  "assignedDoctorId": "doctor-uuid",
  "consultationTypes": ["CONSULTATION"]
}
```

| Method | Path |
|---|---|
| PUT | `/api/v1/appointments/{bookingId}/cancel` |
| PUT | `/api/v1/appointments/{bookingId}/in-consultation` |
| PUT | `/api/v1/appointments/{bookingId}/complete` |
| GET | `/api/v1/appointments/stats` |
| GET | `/api/v1/dashboard/todays-schedule?doctorId=` |

---

## 7. Master data order (before therapy booking)

```
1. Treatment Category  →  2. Therapy Master  →  3. Therapist  →  4. Appointment Therapy
```

Also for assessment: create **Dosha** before medical assessment.

### 7.1 Treatment category — `POST /api/v1/treatment-categories`

```json
{
  "categoryName": "Massage Therapy",
  "description": "Therapeutic oil and herbal massage",
  "status": "ACTIVE"
}
```

**Response**
```json
{
  "id": "f2f1cfc0-b8be-4cf6-af36-3462b4ec9c48",
  "categoryCode": "TC002",
  "categoryName": "Massage Therapy",
  "description": "Therapeutic oil and herbal massage",
  "status": "ACTIVE"
}
```

### 7.2 Therapy master — `POST /api/v1/therapies`

```json
{
  "name": "Podikizhi",
  "categoryId": "f2f1cfc0-b8be-4cf6-af36-3462b4ec9c48",
  "status": "ACTIVE",
  "durationMinutes": 45,
  "price": 1800,
  "description": "Therapeutic oil and herbal massage treatments"
}
```

**Response**
```json
{
  "id": "cd219c01-af02-4f61-98a3-c20cc85d761a",
  "name": "Podikizhi",
  "therapyName": "Podikizhi",
  "therapyCode": "TH002",
  "categoryId": "f2f1cfc0-b8be-4cf6-af36-3462b4ec9c48",
  "categoryName": "Massage Therapy",
  "status": "ACTIVE",
  "durationMinutes": 45,
  "price": 1800,
  "description": "Therapeutic oil and herbal massage treatments"
}
```

| Method | Path |
|---|---|
| GET | `/api/v1/therapies` |
| GET | `/api/v1/therapies/{therapyId}` |
| PUT | `/api/v1/therapies/{therapyId}` |
| PATCH | `/api/v1/therapies/{therapyId}/status` |
| DELETE | `/api/v1/therapies/{therapyId}` |

### 7.3 Dosha — `POST /api/v1/doshas`

```json
{
  "name": "Vata",
  "elements": "Air + Ether",
  "characteristics": "Dry, light, cold, rough, subtle, mobile",
  "status": "ACTIVE"
}
```

```json
{
  "name": "Pitta",
  "elements": "Fire + Water",
  "characteristics": "Hot, sharp, light, oily, liquid, spreading",
  "status": "ACTIVE"
}
```

```json
{
  "name": "Kapha",
  "elements": "Earth + Water",
  "characteristics": "Heavy, slow, cold, oily, smooth, stable",
  "status": "ACTIVE"
}
```

`GET /api/v1/doshas` → list for dropdown.

---

## 8. Therapist (`:8104`)

### Create — `POST /api/v1/therapists`

```json
{
  "name": "Dr. Rahul Verma",
  "status": "ACTIVE",
  "assignedTherapyIds": [
    "cd219c01-af02-4f61-98a3-c20cc85d761a",
    "e50fd59b-6bf2-4d9f-874f-09d673a2df32"
  ]
}
```

**Response `data`**
```json
{
  "id": "0d117be7-15ad-4a0f-ad23-50ea0486481f",
  "name": "Dr. Rahul Verma",
  "therapistName": "Dr. Rahul Verma",
  "therapistCode": "THP-0002",
  "status": "ACTIVE",
  "assignedTherapies": [
    { "id": "cd219c01-af02-4f61-98a3-c20cc85d761a", "name": "Podikizhi" },
    { "id": "e50fd59b-6bf2-4d9f-874f-09d673a2df32", "name": "Kayakalpa" }
  ]
}
```

| Method | Path |
|---|---|
| GET | `/api/v1/therapists` |
| GET | `/api/v1/therapists/{id}` |
| GET | `/api/v1/therapists/by-therapies?therapyIds=` |
| PUT | `/api/v1/therapists/{id}` |
| PATCH | `/api/v1/therapists/{id}/status` |
| DELETE | `/api/v1/therapists/{id}` |

---

## 9. Appointment therapy (`:8103`)

**Prerequisites:** patient with booking + treatment category + therapy master + therapist.

### Create — `POST /api/v1/appointment-therapies`

```json
{
  "patientId": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
  "treatmentCategoryId": "f2f1cfc0-b8be-4cf6-af36-3462b4ec9c48",
  "assignedTherapistId": "0d117be7-15ad-4a0f-ad23-50ea0486481f",
  "scheduleDate": "2026-08-28",
  "scheduleTime": "10:00:00",
  "sessionDuration": 45,
  "sessionFrequency": 1,
  "therapyInstructions": "Arrive 10 minutes early. Wear loose clothing.",
  "remarks": "Morning session",
  "therapyIds": [
    "cd219c01-af02-4f61-98a3-c20cc85d761a"
  ]
}
```

**Response `data` (important shapes)**
```json
{
  "therapyId": "uuid",
  "patient": { "...PatientSummary..." },
  "treatmentCategory": {
    "id": "f2f1cfc0-b8be-4cf6-af36-3462b4ec9c48",
    "categoryCode": "TC002",
    "categoryName": "Massage Therapy",
    "description": "...",
    "status": "ACTIVE"
  },
  "assignedTherapist": {
    "id": "0d117be7-15ad-4a0f-ad23-50ea0486481f",
    "name": "Dr. Rahul Verma",
    "therapistName": "Dr. Rahul Verma",
    "therapistCode": "THP-0002",
    "status": "ACTIVE"
  },
  "scheduleDate": "2026-08-28",
  "scheduleTime": "10:00:00",
  "sessionDuration": 45,
  "sessionFrequency": 1,
  "therapyInstructions": "Arrive 10 minutes early. Wear loose clothing.",
  "remarks": "Morning session",
  "therapyStatus": "SCHEDULED",
  "therapies": [
    {
      "id": "cd219c01-af02-4f61-98a3-c20cc85d761a",
      "name": "Podikizhi",
      "therapyName": "Podikizhi",
      "therapyCode": "TH002",
      "categoryId": "f2f1cfc0-b8be-4cf6-af36-3462b4ec9c48",
      "categoryName": "Massage Therapy",
      "status": "ACTIVE",
      "durationMinutes": 45,
      "price": 1800,
      "description": "..."
    }
  ]
}
```

> `assignedTherapist` does **not** include specialization / mobile / email / qualification / therapyRoom.

| Method | Path |
|---|---|
| GET | `/api/v1/appointment-therapies/{patientId}` |
| GET | `/api/v1/appointment-therapies/therapist/{therapistId}/today` |

---

## 10. Medical assessment (`:8103`)

### Save (no documents) — `POST /api/v1/medical-assessment`

```json
{
  "patientId": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
  "ayurvedicAssessment": {
    "patientId": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
    "doshaId": "546e538f-5e54-48b2-957a-765fd905a2c5",
    "bodyConstitution": "Vata-Pitta",
    "currentImbalances": "Vata elevated, mild Pitta"
  },
  "physicalExamination": {
    "patientId": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
    "weight": 62.5,
    "height": 165.0,
    "ibw": 58.0,
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
    "patientId": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
    "pastMedicalConditions": "None",
    "pastSurgeries": "None",
    "currentMedications": "None",
    "allergies": "No known allergies",
    "familyHistory": "Father - diabetes"
  },
  "lifestyleInformation": {
    "patientId": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
    "dietType": "Vegetarian",
    "sleepPattern": "6-7 hours",
    "exerciseHabits": "Walking daily",
    "addiction": "None"
  },
  "systemicExamination": {
    "patientId": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
    "cardiovascular": "NAD",
    "respiratory": "NAD",
    "nervous": "NAD",
    "abdomenGi": "Soft, non-tender",
    "locomotor": "NAD"
  },
  "treatmentPlan": {
    "patientId": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
    "investigationAndPlanSuggested": "CBC, Lipid profile",
    "planTaken": "Abhyanga + Podikizhi for 5 days"
  }
}
```

**Response:** same sections with generated `id`s.  
**No `documents` field** on this endpoint (empty array is not returned).

### With documents — `POST /api/v1/medical-assessment/with-documents`  
`multipart/form-data`: `data` (JSON) + optional files `pastMedicalReports`, `prescriptions`, `labReports`.  
`documents` appears only if files were uploaded.

`GET /api/v1/medical-assessment/{patientId}`

---

## 11. Medicine (`:8108`)

### Create — `POST /api/v1/medicines`  
(Accepts one object or array)

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

### Update — `PUT /api/v1/medicines/{medicineId}`

Preferred body (same as create fields):

```json
{
  "medicineName": "Ashwagandha",
  "category": "POWDER",
  "manufacturer": "Ayur Co",
  "batchNumber": "B001",
  "quantity": 90,
  "expiryDate": "2027-12-31",
  "purchasePrice": 80,
  "sellingPrice": 120,
  "lowStockAlertEnabled": true,
  "lowStockThreshold": 20,
  "status": "ACTIVE"
}
```

Also accepts GET-response aliases: `stockQuantity` → `quantity`, `price` → `sellingPrice`. Extra fields like `id` / `stockStatus` are ignored.

**Response `data`**
```json
{
  "id": "uuid",
  "medicineName": "Ashwagandha",
  "category": "POWDER",
  "manufacturer": "Ayur Co",
  "batchNumber": "B001",
  "stockQuantity": 90,
  "quantity": 90,
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

| Method | Path |
|---|---|
| GET | `/api/v1/medicines` |
| GET | `/api/v1/medicines/meta/names` → `[{ "id", "medicineName" }]` |
| GET | `/api/v1/medicines/meta/categories` |
| GET | `/api/v1/medicines/meta/manufacturers` |
| GET | `/api/v1/medicines/low-stock` |
| GET | `/api/v1/dashboard/medicine-stock?lowStockLimit=5` |
| POST | `/api/v1/medicines/{id}/stock/deduct` → `{ "quantity": 2 }` |
| POST | `/api/v1/medicines/{id}/stock/restore` → `{ "quantity": 2 }` |
| DELETE | `/api/v1/medicines/{id}` |

---

## 12. Billing (`:8109`)

### Create invoice — `POST /api/v1/invoices`

```json
{
  "patientId": "aad1dd39-8ff5-4e6b-95cd-e2aef1a56d2b",
  "patientDisplayId": "PT458652",
  "patientCode": "GAN2026-0001",
  "patientName": "Khushi Shroff",
  "contactNumber": "9205061339",
  "invoiceDate": "2026-08-04",
  "visitType": "CONSULTATION",
  "serviceFees": 500,
  "packageType": null,
  "packageCharges": 0,
  "medicines": [
    {
      "medicineId": "medicine-uuid",
      "quantity": 2,
      "unitPrice": 120
    }
  ],
  "therapies": [
    {
      "itemName": "Podikizhi",
      "quantity": 1,
      "unitPrice": 1800,
      "assignedTherapistId": "0d117be7-15ad-4a0f-ad23-50ea0486481f",
      "assignedTherapistName": "Dr. Rahul Verma",
      "scheduleDate": "2026-08-28",
      "scheduleTime": "10:00:00",
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

| Method | Path |
|---|---|
| GET | `/api/v1/invoices?patientId=&status=` |
| GET | `/api/v1/invoices/{invoiceId}` |
| POST | `/api/v1/invoices/{invoiceId}/payments` → `{ "amountPaid", "paymentMethod", "remarks" }` |
| GET | `/api/v1/sales` |
| GET | `/api/v1/dashboard/billing-summary?period=MONTHLY` |

---

## 13. File upload (`:8105`)

`POST /api/v1/documents/upload` — multipart: `bookingId`, `documentType`, `file`

| Method | Path |
|---|---|
| GET | `/api/v1/documents/{bookingId}` |
| GET | `/api/v1/documents/{documentId}/download` (raw file) |
| DELETE | `/api/v1/documents/{documentId}` |

---

## 14. Attendance (`:8106`) / Notification (`:8110`) / Activity (`:8107`)

### Attendance check-in — `POST /api/v1/attendances/check-in`
```json
{
  "empId": "EMP001",
  "empName": "Ravi Kumar",
  "staffType": "HELPER",
  "attendanceDate": "2026-08-04",
  "checkInTime": "2026-08-04T09:05:00",
  "status": "PRESENT"
}
```

### Notification create — `POST /api/v1/notifications`
```json
{
  "recipientUserId": "user-uuid",
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

### Activity log — `POST /api/v1/activity-logs`
```json
{
  "page": "Patients",
  "action": "UPDATED",
  "target": "Patient #PT458652",
  "beforeValue": "ACTIVE",
  "afterValue": "INACTIVE",
  "activityTimestamp": "2026-08-04T12:00:00",
  "performedByUserId": "user-uuid",
  "performedByUserName": "Rahul Sharma",
  "performedByRole": "SUPER_ADMIN"
}
```

---

## 15. Frontend screen → API map

| Screen | API |
|---|---|
| Login | Auth `:8111` |
| Patients Active/Inactive | `GET :8103/api/v1/appointments/patients?statusTab=` |
| Book appointment | `POST :8103/api/v1/appointments` + `GET :8102/api/v1/doctors/active` |
| Doctors | Doctor `:8102` |
| Therapists | Therapist `:8104` |
| Therapies / categories / doshas | Appointment `:8103` |
| Appointment therapy | `POST :8103/api/v1/appointment-therapies` |
| Medical assessment | `POST :8103/api/v1/medical-assessment` |
| Medicine inventory | Medicine `:8108` |
| Billing | Billing `:8109` |
| Dashboard | todays-schedule `:8103`, medicine-stock `:8108`, billing-summary `:8109` |

---

## 16. Notes

1. Dates: `yyyy-MM-dd`. Times: `HH:mm:ss`.
2. Replace example UUIDs with live values from GET APIs.
3. Soft delete ≠ status. Status PATCH only changes ACTIVE/INACTIVE.
4. Patients page tabs use **booking** status, not patient master status.
5. Auth port is **8111** (not 8100).

---

*Source of truth: service controllers + DTOs on `fixes-development`. Prefer Swagger per service for live try-out.*
