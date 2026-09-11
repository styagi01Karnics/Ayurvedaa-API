# Changes: Public Tenant Locations & Medical History Options

Branch: `fixes-development`

---

## Why

- Frontends need **active hospital tenant codes + location** without a bearer token (clinic picker / public flows).
- Intake forms need a **stable medical-history option catalog** filtered by patient gender (common + gender-specific diseases, surgeries, medications, allergies).

---

## 1. Public tenant locations (auth-service)

| Method | Path | Auth |
|--------|------|------|
| `GET` | `/api/v1/public/tenants` | None (`SecurityConfig` permitAll; OpenAPI `@SecurityRequirements`) |

**Behavior**

- Returns active, non-platform, non-deleted hospitals ordered by `createdAt` desc.
- Response fields: `tenantCode`, `state`, `city` only.

**Example response**

```json
{
  "success": true,
  "status": 200,
  "message": "Tenant locations fetched successfully.",
  "data": [
    {
      "tenantCode": "GAN-DL",
      "state": "Delhi",
      "city": "New Delhi"
    }
  ]
}
```

**Files**

- `PublicTenantController`, `PublicTenantService`, `PublicTenantServiceImpl`
- `PublicTenantLocationResponse`
- `AuthMapper.toPublicTenantLocationResponse`
- `AuthMessages.PUBLIC_TENANT_LOCATIONS_FETCHED`
- `SecurityConfig` GET permitAll for `/api/v1/public/tenants`
- Test: `PublicTenantServiceImplTest`

**Migration notes:** none (reads existing `tenants` table).

---

## 2. Medical history options (patient-service)

| Method | Path | Auth | Query |
|--------|------|------|-------|
| `GET` | `/api/v1/medical-history/options` | Bearer hospital JWT | **Required** `gender` = `MALE` \| `FEMALE` \| `OTHER` |

**Filtering rules**

- Each catalog entry has `ApplicableGender`: `ALL`, `MALE`, or `FEMALE`.
- `FEMALE` → `ALL` + `FEMALE`; `MALE` → `ALL` + `MALE`; `OTHER` → `ALL` only.
- Catalog enums: `DiseaseCode`, `SurgeryCode`, `MedicationCode`, `AllergyCode`.
- Shared filter: `MedicalHistoryGenderFilter`.
- Patient `Gender` enum used for registration and option filtering.
- Missing / null gender → `400` with `PatientMessages.GENDER_REQUIRED`.

**Example request**

```http
GET /api/v1/medical-history/options?gender=FEMALE
Authorization: Bearer <hospitalAccessToken>
```

**Example response (shape)**

```json
{
  "success": true,
  "status": 200,
  "message": "Medical history options fetched successfully.",
  "data": {
    "gender": "FEMALE",
    "pastMedicalConditions": [
      { "code": "DIABETES_TYPE_2", "name": "Diabetes Mellitus Type 2" },
      { "code": "PCOS", "name": "PCOS" }
    ],
    "pastSurgeries": [
      { "code": "APPENDICECTOMY", "name": "…" }
    ],
    "currentMedications": [
      { "code": "METFORMIN", "name": "…" }
    ],
    "allergies": [
      { "code": "PENICILLIN", "name": "…" }
    ]
  }
}
```

**Files**

- Controller: `MedicalHistoryController`
- Service: `MedicalHistoryService` / `MedicalHistoryServiceImpl`
- DTOs: `MedicalHistoryOptionResponse`, `MedicalHistoryOptionsResponse`
- Constants: `PatientMessages` (`MEDICAL_HISTORY_OPTIONS_FETCHED`, `GENDER_REQUIRED`)
- Enums: `Gender`, `ApplicableGender`, `DiseaseCode`, `SurgeryCode`, `MedicationCode`, `AllergyCode`, `MedicalHistoryGenderFilter`
- Logger + JavaDoc on controller / service / enums
- Test: `MedicalHistoryServiceImplTest`

**Migration notes:** none (fixed enums; no DB master table).

---

## Gaps / not in this commit

Other local work may still be uncommitted (hospital mail, PayU UPI QR / refunds, dashboard trends, notification SMTP providers, `docs/_svc_logs`, `*.factorypath`, `target/`). Those are intentionally excluded from this push unless staged separately later.

---

## Excluded from commit

- `*.factorypath`
- `**/target/**`
- `docs/_svc_logs/**`
- Live credentials / hardcoded mailbox or PayU secrets
