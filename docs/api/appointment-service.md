# appointment-service API (Step-2)

| Item | Value |
| --- | --- |
| Port | `8103` |
| Base URL | `http://localhost:8103` |
| Auth | Bearer hospital JWT |
| ~Endpoints | **71** across 18 controllers |

Clinical bookings, masters (therapies, categories, doshas, consultation types, treatment plans), assessments, prescriptions, follow-ups, and therapy sessions. All data goes to `hosp_*` via JWT `schemaName`.

**Typical order:** seed masters (consultation types, doshas, treatment categories, therapies, treatment-plan masters) → book appointment → medical assessment → prescription / appointment-therapies → treatments / follow-ups.

---

## 1. Appointment bookings — `/api/v1/appointments` (16)

### `POST /api/v1/appointments`

Create booking (can embed new patient details).

```json
{
  "patient": {
    "fullName": "Ravi Kumar",
    "gender": "MALE",
    "dateOfBirth": "1990-05-12",
    "age": 35,
    "preferredLanguage": "Hindi",
    "mobileNumber": "9876543210",
    "email": "ravi@gmail.com",
    "state": "Delhi",
    "city": "New Delhi",
    "address": "12 Park Street",
    "emergencyContactName": "Sita",
    "emergencyRelationship": "Spouse",
    "emergencyPhoneNumber": "9876500000",
    "idProofType": "AADHAAR",
    "idProofNumber": "xxxx",
    "occupation": "Engineer",
    "insuranceDetails": null
  },
  "registrationDate": "2026-08-31",
  "slotTime": "10:30:00",
  "assignedDoctorId": "…",
  "consultationTypeIds": ["…"]
}
```

Response `AppointmentBookingResponse`: `id`, `patientId`, `patient`, `registrationDate`, `slotTime`, `assignedDoctorId`, `assignedDoctor`, `consultationTypes`, `bookingStatus`.

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/stats` | Month/today counts → `AppointmentStatsResponse` |
| GET | `/patients` | Query: `statusTab` (req), `search?`, `bookingStatus?`, `consultationTypeId?`, `doshaId?`, `doctorId?` |
| GET | `/cancelled` | Cancelled list |
| GET | `/today` | Query `page`, `size` → `DoctorTodayScheduleResponse` |
| GET | `/today/consultation-type/{consultationTypeId}` | Today filtered by visit type |
| GET | `/doctor/{doctorId}/today` | Doctor schedule (paged) |
| GET | `/{bookingId}` | By id |
| GET | `/patient/{patientId}` | By patient |
| GET | `/status/{bookingStatus}` | Path status; `ALL` = no filter |
| GET | `/date/{registrationDate}` | ISO date |
| PUT | `/{bookingId}/reschedule` | Body: `patientId`, `registrationDate`, `slotTime`, `assignedDoctorId`, `consultationTypeIds` |
| PUT | `/{bookingId}/cancel` | Cancel |
| DELETE | `/{bookingId}` | Soft delete |
| PUT | `/{bookingId}/in-consultation` | Start consult |
| PUT | `/{bookingId}/complete` | Complete |

---

## 2. Prescriptions — `/api/v1/prescriptions` (4)

### `POST /api/v1/prescriptions`

```json
{
  "patientId": "…",
  "appointmentBookingId": "…",
  "assignedDoctorId": "…",
  "medicines": [
    {
      "medicineId": "…",
      "medicineName": "Ashwagandha",
      "dosage": "1 tsp",
      "frequency": "BD",
      "duration": "7 days",
      "notes": null
    }
  ],
  "therapySuggestions": [
    {
      "therapyCategoryId": "…",
      "recommendedTherapyIds": ["…"]
    }
  ],
  "nextFollowUp": [
    {
      "setUpRequired": true,
      "schedulingOption": "AFTER_7_DAYS",
      "suggestions": "Review dosha"
    }
  ],
  "diagnosis": "Vata imbalance",
  "notes": "Rest and warm diet"
}
```

| Method | Path | Notes |
| --- | --- | --- |
| PUT | `/{prescriptionId}` | `UpdatePrescriptionRequest` |
| GET | `/patient/{patientId}` | List |
| GET | `/{prescriptionId}` | By id |

---

## 3. Follow-ups — `/api/v1/follow-ups` (5)

| Method | Path | Body / params | Response |
| --- | --- | --- | --- |
| POST | `/` | `patientId`, `assignedDoctorId`, `sourceBookingId`, `visitTypeId`, `appointmentDate`, `schedulingOption`, `smsReminderEnabled`, `status` | `FollowUpResponse` |
| GET | `/` | — | List |
| GET | `/patient/{patientId}` | — | List |
| PUT | `/{followUpId}/status` | `{ "status": "…" }` | `FollowUpResponse` |
| PUT | `/{followUpId}/cancel` | — | `FollowUpResponse` |

---

## 4. Medical assessment — `/api/v1/medical-assessment` (3)

Combined save of all six clinical sections (preferred over calling each section alone).

| Method | Path | Auth | Request | Response |
| --- | --- | --- | --- | --- |
| POST | `/` | Bearer | JSON `CreateMedicalAssessmentRequest` | `MedicalAssessmentResponse` |
| POST | `/with-documents` | Bearer | **multipart:** part `data` (JSON) + optional files `pastMedicalReports`, `prescriptions`, `labReports` | `MedicalAssessmentResponse` |
| GET | `/{patientId}` | Bearer | path | Combined assessment + `documents[]` |

`CreateMedicalAssessmentRequest`: `patientId` + nested `ayurvedicAssessment`, `physicalExamination`, `medicalHistory`, `lifestyleInformation`, `systemicExamination`, `treatmentPlan`.

---

## 5. Assessment sections (12) — POST create + GET by patient

| Base path | Create body highlights |
| --- | --- |
| `/api/v1/ayurvedic-assessments` | `patientId`, `doshaId`, `bodyConstitution`, `currentImbalances` |
| `/api/v1/medical-histories` | `patientId`, past conditions/surgeries, meds, allergies, familyHistory |
| `/api/v1/lifestyle-information` | `patientId`, `dietType`, `sleepPattern`, `exerciseHabits`, `addiction` |
| `/api/v1/physical-examinations` | `patientId`, vitals (weight, height, bp, pulse, …) |
| `/api/v1/systemic-examinations` | `patientId`, cardiovascular, respiratory, nervous, abdomenGi, locomotor |
| `/api/v1/treatment-plans` | `patientId`, `investigationAndPlanSuggested`, `planTaken` |

Each: `POST /` and `GET /{patientId}`.

---

## 6. Therapies master — `/api/v1/therapies` (7)

Code: `{tenantCode}-TH-#####`.

| Method | Path | Notes |
| --- | --- | --- |
| POST | `/` | `name`, `categoryId`, `status`, `durationMinutes`, `price`, `description` |
| GET | `/` | Query `status?` |
| GET | `/category/{categoryId}` | Query `status?` |
| GET | `/{therapyId}` | |
| PUT | `/{therapyId}` | Update fields |
| PATCH | `/{therapyId}/status` | `{ "status": "…" }` |
| DELETE | `/{therapyId}` | Returns `TherapyResponse` |

---

## 7. Appointment therapies — `/api/v1/appointment-therapies` (4)

| Method | Path | Notes |
| --- | --- | --- |
| POST | `/` | `patientId`, `treatmentCategoryId`, `assignedTherapistId`, schedule/session fields, `therapyIds` |
| PUT | `/{appointmentTherapyId}/status` | `{ "therapyStatus": "…" }` |
| GET | `/therapist/{therapistId}/today` | Query `page`, `size` |
| GET | `/{patientId}` | List for patient |

---

## 8. Treatments — `/api/v1/treatments` (5)

| Method | Path | Notes |
| --- | --- | --- |
| POST | `/` | `patientId`, `treatmentPlanId`, dates, sessions, `assignedTherapistId`, `treatmentStatus` |
| GET | `/` | All |
| GET | `/patient/{patientId}` | |
| PUT | `/{treatmentId}` | Update plan/dates/sessions/therapist |
| PUT | `/{treatmentId}/status` | `{ "treatmentStatus": "…" }` |

---

## 9. Dashboard — `/api/v1/dashboard` (1)

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/todays-schedule` | Query `doctorId?` → ongoing / next / remaining |

---

## 10. Masters

### Treatment plan masters — `/api/v1/treatment-plan-masters` (4)

`POST` (`name`, `status`) · `GET /` · `GET /active` · `GET /{treatmentPlanId}`

### Consultation types — `/api/v1/consultation-types` (4)

`POST` (`name`, `status`) · `GET /` · `GET /active` · `GET /{consultationTypeId}`

### Doshas — `/api/v1/doshas` (3)

`POST` (`name`, `elements`, `characteristics`, `status`) · `GET /` · `GET /{doshaId}`

### Treatment categories — `/api/v1/treatment-categories` (3)

`POST` (`categoryName`, `description`, `status`) → includes `categoryCode` (`…-TC-#####`) · `GET /` · `GET /{categoryId}`

---

## Envelope example

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {}
}
```

← [API_GUIDE.md](../API_GUIDE.md)
