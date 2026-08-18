# Latest API Changes

Branch: `fixes-development`  
Latest feature commit: `d7074f7` (billing drafts, prescriptions, active patient follow-up rules).  
Earlier masters work: `d97cdd6` and related.

---

## Summary (newest)

| Area | Change |
|------|--------|
| Doctor billing draft | New `billings` table — PENDING → COMPLETED; receptionist generates invoice |
| Invoice from billing | Same body as `POST /invoices` (medicine / therapy / discount / GST) |
| Prescription | New prescription APIs (medicines, therapy suggestions, next follow-up) |
| Active / Inactive patients | ACTIVE = non-closed bookings **or** closed with follow-up; INACTIVE = closed without follow-up |
| Doctor response | Adds `qualification`, `mobileNumber` |

Older masters (consultation type, treatment plan, package + price, shared mobile) remain documented below.

---

## A. Doctor billing drafts (billing-service)

**Tables:** `billings`, `billing_service_items`  
**Statuses:** `PENDING` \| `COMPLETED`  
**Base path:** `/api/v1/billings`  
**Port:** `8109`

Doctor saves **section 3 screenshot data only** (services + optional package master).  
No medicine, therapy, discount, or GST on billing.

### APIs

| Method | Path | Who | Description |
|--------|------|-----|-------------|
| `POST` | `/api/v1/billings` | Doctor | Create PENDING billing |
| `GET` | `/api/v1/billings` | Both | List (`?status=PENDING\|COMPLETED`) |
| `GET` | `/api/v1/billings/{billingId}` | Both | Detail + services (joins `mst_package`) |
| `GET` | `/api/v1/billings/patient/{patientId}` | Both | By patient |
| `POST` | `/api/v1/billings/{billingId}/generate-invoice` | Reception | Create invoice; mark billing COMPLETED |

### Create billing body (doctor)

```json
{
  "patientId": "uuid",
  "patientName": "Khushi Shroff",
  "contactNumber": "9876543210",
  "billingDate": "2026-10-15",
  "services": [
    {
      "serviceType": "Consultation",
      "serviceFees": 800,
      "packageMasterId": null,
      "packageType": null,
      "packageCharges": null
    }
  ]
}
```

No `patientDisplayId` / `patientCode` / `visitType` on billings.  
Package vs consultation is only from `services[]` — package fields optional (null for consultation-only).  
If `packageMasterId` is set, name/price can fill from `mst_package` when type/charges omitted.  
Invoice keeps existing `visitType` (unchanged for frontend).

### Generate invoice from billing (receptionist)

**Same body as** `POST /api/v1/invoices`.

```json
{
  "patientId": "uuid",
  "patientName": "Khushi Shroff",
  "contactNumber": "9876543210",
  "invoiceDate": "2026-10-15",
  "visitType": "CONSULTATION",
  "serviceFees": 800,
  "packageMasterId": "uuid-from-mst_package",
  "packageType": "Basic Package",
  "packageCharges": 800,
  "medicines": [
    { "medicineId": "uuid", "quantity": 2, "unitPrice": 200 }
  ],
  "therapies": [
    {
      "itemName": "Panchakarma",
      "quantity": 1,
      "unitPrice": 800
    }
  ],
  "discount": 100,
  "taxEnabled": true,
  "cgstPercent": 3,
  "sgstPercent": 3,
  "amountPaid": 0
}
```

- Patient / service can be omitted or partial — filled from PENDING billing when missing.  
- `packageMasterId` optional (same as billing); copied from billing when generating invoice if omitted.  
- Medicine / therapy / discount / GST belong on **invoice**, not billing.  
- Creates `INV-xxxx`, sets billing `COMPLETED`, saves `invoiceId` / `invoiceNumber`.  
- `amountPaid` omitted or `0` → invoice status **`UNPAID`**. If `amountPaid` > 0, amount is saved and status becomes ONGOING/COMPLETED.

### Normal invoice (unchanged)

`POST /api/v1/invoices` — same body; creates invoice immediately (no billing draft).

Discount / GST appear on:

- `GET /api/v1/invoices/{invoiceId}`
- Response of generate-invoice / create invoice

Patient billing aggregate (existing):

- `GET /api/v1/billing/patient/{patientId}`
- `GET /api/v1/invoices/patient/{patientId}`

Section 1 patient packages (`/api/v1/packages`) — **unchanged**.

---

## B. Prescriptions (appointment-service)

**Tables:** `prescriptions`, `prescription_medicines`, `prescription_therapy_suggestions`, `prescription_therapy_suggestion_items`  
**Base path:** `/api/v1/prescriptions`  
**Port:** `8103`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/prescriptions` | Generate prescription |
| `GET` | `/api/v1/prescriptions/{prescriptionId}` | By id (print-view enriched) |
| `GET` | `/api/v1/prescriptions/patient/{patientId}` | All for patient |

### Create body

```json
{
  "patientId": "uuid",
  "appointmentBookingId": "uuid",
  "assignedDoctorId": "uuid",
  "medicines": [
    {
      "medicineId": "uuid",
      "medicineName": "Tab OCRIS 200",
      "dosage": "2 tablets",
      "frequency": "twice a week",
      "duration": "5 days",
      "notes": "After food"
    }
  ],
  "therapySuggestions": [
    {
      "therapyCategoryId": "uuid",
      "recommendedTherapyIds": ["uuid", "uuid"]
    }
  ],
  "nextFollowUp": {
    "setUpRequired": true,
    "schedulingOption": "7_DAYS",
    "suggestions": "optional text"
  },
  "diagnosis": "Mental health issue, Migraine",
  "notes": "optional free text"
}
```

Needs at least one medicine **or** one therapy suggestion.

### GET response (enriched)

Includes nested:

- `patient` — display id, name, age, gender, weight, height, dietType  
- `treatment` — consultation types, consultation/next appointment datetime, visit display  
- `consultant` — name, specialization, qualification, contactNumber  
- `diagnosis`, `notes`  
- `medicines` (with `instruction` = notes)  
- `therapySuggestions`, `nextFollowUp`

Doctor Feign/summary now exposes `qualification` and `mobileNumber` (doctor-service + appointment DTO).

---

## C. Active / Inactive patient list (appointment-service)

**Endpoint:** `GET /api/v1/appointments/patients?statusTab=ACTIVE|INACTIVE`  
(Still appointment **bookings**, not full patient table.)

| Tab | Rule |
|-----|------|
| **ACTIVE** | Booking status is **not** `CANCELLED` / `COMPLETED` (`SCHEDULED`, `RESCHEDULED`, `IN_CONSULTATION`), **or** cancelled/completed **with** a follow-up linked via `sourceBookingId` |
| **INACTIVE** | `CANCELLED` or `COMPLETED` **with no** follow-up for that booking |

Optional filters unchanged: `search`, `bookingStatus`, `consultationTypeId`, `doshaId`, `doctorId`.

---

## D. Earlier masters (still in effect)

### Consultation type master

**Path:** `/api/v1/consultation-types`  
Booking uses `consultationTypeIds: uuid[]`; response `consultationTypes: [{ id, name }]`.

### Treatment plan master

**Path:** `/api/v1/treatment-plan-masters`  
*(Not `/api/v1/treatment-plans` — that is medical-assessment free-text plans.)*  
Treatment uses `treatmentPlanId`.

### Package master

**Path:** `/api/v1/package-masters`  
Fields: `name`, `packagePrice`, `status`.  
Patient package uses `packageMasterId`.

### Patient mobile

Duplicate mobile check removed (parent/child may share). Email uniqueness kept.

---

## Frontend checklist (new)

1. Doctor Billing Details → `POST /api/v1/billings` (services + optional `packageMasterId`).  
2. Billing list Status = Pending → `GET /api/v1/billings?status=PENDING`.  
3. Receptionist Start Invoice → `POST /api/v1/billings/{id}/generate-invoice` with full invoice body (medicine/therapy/discount/GST).  
4. Direct Generate Invoice button can still use `POST /api/v1/invoices`.  
5. Prescription step → `POST /api/v1/prescriptions`; print view → GET by prescription id.  
6. Patients Active/Inactive tabs → same `/appointments/patients` with new ACTIVE/INACTIVE meaning.

---

## DB notes

With `ddl-auto=update`, new tables/columns are created on startup:

| Table / column | Purpose |
|----------------|---------|
| `billings` | Doctor PENDING/COMPLETED draft |
| `billing_service_items` | Service + package lines (`package_master_id`) |
| `prescriptions` | Prescription header + follow-up fields + diagnosis/notes |
| `prescription_medicines` | Rx medicine lines |
| `prescription_therapy_suggestions` | Therapy suggestion rows |
| `prescription_therapy_suggestion_items` | Recommended therapy IDs |

Do **not** commit local `application.yml` DB overrides.
