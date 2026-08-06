# Latest API Changes

Documentation for masters and mapping updates on branch `fixes-development` (commit `d97cdd6` and related work).

## Summary

| Area | Change |
|------|--------|
| Consultation type | Enum removed → master table + booking list of IDs |
| Treatment plan name | Free text removed → master table + `treatmentPlanId` |
| Package name | Free text removed → master table with **price** + `packageMasterId` |
| Patient mobile | Duplicate mobile check removed (parent/child may share a number) |

---

## 1. Consultation Type Master (appointment-service)

**Table:** `mst_consultation_type`  
**Base path:** `/api/v1/consultation-types`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/consultation-types` | Create |
| `GET` | `/api/v1/consultation-types` | All (ACTIVE + INACTIVE) |
| `GET` | `/api/v1/consultation-types/active` | ACTIVE only |
| `GET` | `/api/v1/consultation-types/{id}` | By id |

### Create body

```json
{
  "name": "CONSULTATION",
  "status": "ACTIVE"
}
```

`status` is optional (defaults to `ACTIVE`). Values: `ACTIVE` | `INACTIVE`.

### Seed data

On startup, `CONSULTATION` and `THERAPY` are seeded if missing.

### Booking mapping

Table `appointment_consultation_types`: one row per type per booking (`bookingId` + `consultationTypeId`).

**Create / reschedule appointment** — send IDs (not enum names):

```json
{
  "patient": { },
  "registrationDate": "2026-08-06",
  "slotTime": "10:00:00",
  "assignedDoctorId": "uuid",
  "consultationTypeIds": [
    "uuid-consultation",
    "uuid-therapy"
  ]
}
```

**Response** includes:

```json
"consultationTypes": [
  { "id": "uuid", "name": "CONSULTATION" },
  { "id": "uuid", "name": "THERAPY" }
]
```

### Related endpoint changes

| Old | New |
|-----|-----|
| Filter `consultationType=CONSULTATION` | `consultationTypeId={uuid}` |
| `GET .../appointments/today/{CONSULTATION\|THERAPY}` | `GET .../appointments/today/consultation-type/{consultationTypeId}` |
| Follow-up `visitType`: enum | `visitTypeId`: UUID; response also has `visitTypeName` |

---

## 2. Treatment Plan Master (appointment-service)

**Table:** `mst_treatment_plan`  
**Base path:** `/api/v1/treatment-plan-masters`  
*(Not `/api/v1/treatment-plans` — that path is used by appointment medical-assessment treatment plans.)*

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/treatment-plan-masters` | Create |
| `GET` | `/api/v1/treatment-plan-masters` | All |
| `GET` | `/api/v1/treatment-plan-masters/active` | ACTIVE only |
| `GET` | `/api/v1/treatment-plan-masters/{id}` | By id |

### Create body

```json
{
  "name": "Panchakarma Detox",
  "status": "ACTIVE"
}
```

### Treatment mapping

`Treatment` stores `treatmentPlanId` instead of `treatmentPlanName`.

**Create treatment:**

```json
{
  "patientId": "uuid",
  "treatmentPlanId": "uuid",
  "startDate": "2026-08-01",
  "endDate": "2026-08-30",
  "totalSessions": 10,
  "completedSessions": 0,
  "assignedTherapistId": "uuid",
  "treatmentStatus": "SCHEDULED"
}
```

**Response** includes both `treatmentPlanId` and resolved `treatmentPlanName`.

Patient treatment APIs (unchanged paths):

| Method | Path |
|--------|------|
| `POST` | `/api/v1/treatments` |
| `GET` | `/api/v1/treatments` |
| `GET` | `/api/v1/treatments/patient/{patientId}` |
| `PUT` | `/api/v1/treatments/{treatmentId}` |
| `PUT` | `/api/v1/treatments/{treatmentId}/status` |

---

## 3. Package Master (billing-service)

**Table:** `mst_package`  
**Base path:** `/api/v1/package-masters`

Fields: `name`, `packagePrice`, `status`.

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/package-masters` | Create |
| `GET` | `/api/v1/package-masters` | All |
| `GET` | `/api/v1/package-masters/active` | ACTIVE only |
| `GET` | `/api/v1/package-masters/{id}` | By id |

### Create body

```json
{
  "name": "Gold Membership",
  "packagePrice": 15000.00,
  "status": "ACTIVE"
}
```

### Patient package mapping

`PatientPackage` stores `packageMasterId` instead of `packageName`.

**Create patient package** (`POST /api/v1/packages`):

```json
{
  "patientId": "uuid",
  "packageMasterId": "uuid",
  "validity": "2027-08-06",
  "status": "SCHEDULED",
  "discountApplied": 500.00
}
```

**Response** includes `packageMasterId`, `packageName`, `packagePrice` (from master), plus validity / discount / status.

| Method | Path |
|--------|------|
| `POST` | `/api/v1/packages` |
| `GET` | `/api/v1/packages` |
| `GET` | `/api/v1/packages/patient/{patientId}` |
| `PUT` | `/api/v1/packages/{packageId}` |
| `PUT` | `/api/v1/packages/{packageId}/status` |

---

## 4. Patient mobile — no duplicate check (patient-service)

Creating a patient (including via appointment booking) **no longer fails** when the mobile number already exists.

Email uniqueness is unchanged.

Use case: child appointments using the parent’s phone number.

---

## Frontend migration checklist

1. Load masters before booking / treatment / package screens:
   - `GET /api/v1/consultation-types/active`
   - `GET /api/v1/treatment-plan-masters/active`
   - `GET /api/v1/package-masters/active`
2. Replace string enums / free-text names with UUID fields:
   - `consultationTypes: string[]` → `consultationTypeIds: uuid[]`
   - `treatmentPlanName` → `treatmentPlanId`
   - `packageName` → `packageMasterId`
   - Follow-up `visitType` → `visitTypeId`
3. Update filters and “today by type” URL to use consultation type UUID.
4. Display names/prices from response fields (`consultationTypes[].name`, `treatmentPlanName`, `packageName`, `packagePrice`).

---

## DB notes

With Hibernate `ddl-auto=update`, new tables/columns are added automatically. Existing rows that still store old enum strings or free-text names will **not** auto-migrate to UUIDs — clear or re-create those rows after deploy if needed.

| New / changed | Detail |
|---------------|--------|
| `mst_consultation_type` | Master |
| `appointment_consultation_types.consultation_type_id` | UUID (was enum string) |
| `mst_treatment_plan` | Master |
| `treatments.treatment_plan_id` | UUID (was `treatment_plan_name`) |
| `mst_package` | Master (`name` + `package_price`) |
| `patient_packages.package_master_id` | UUID (was `package_name`) |
| `follow_ups.visit_type_id` | UUID (was enum `visit_type`) |
