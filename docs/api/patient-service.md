# patient-service API (Step-2)

| Item | Value |
| --- | --- |
| Port | `8101` |
| Base URL | `http://localhost:8101` |
| Prefix | `/api/v1/patients` |
| Auth | Bearer hospital JWT (`schemaName` = `hosp_*`) |

Data is stored in the hospital schema from JWT. Business code: `patientCode` = `{tenantCode}-PT-#####` — see [CODES.md](../CODES.md). There is **no** `patientDisplayId`.

---

## Endpoints (5)

### `POST /api/v1/patients/create-patient`

Create patient.

```http
POST http://localhost:8101/api/v1/patients/create-patient
Authorization: Bearer <hospitalAccessToken>
Content-Type: application/json
```

```json
{
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
  "emergencyContactName": "Sita Kumar",
  "emergencyRelationship": "Spouse",
  "emergencyPhoneNumber": "9876500000",
  "idProofType": "AADHAAR",
  "idProofNumber": "xxxx",
  "occupation": "Engineer",
  "insuranceDetails": null
}
```

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "id": "…",
    "patientCode": "GAN-DL-PT-00001",
    "fullName": "Ravi Kumar",
    "gender": "MALE",
    "dateOfBirth": "1990-05-12",
    "age": 35,
    "mobileNumber": "9876543210",
    "email": "ravi@gmail.com",
    "status": "ACTIVE",
    "createdAt": "…",
    "updatedAt": "…"
  }
}
```

### `GET /api/v1/patients/get-patient/{patientId}`

| Auth | Path | Response |
| --- | --- | --- |
| Bearer | `patientId` UUID | `PatientResponse` |

### `GET /api/v1/patients/get-all-patients`

| Auth | Response |
| --- | --- |
| Bearer | `List<PatientResponse>` |

### `GET /api/v1/patients/get-patient-count`

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "totalPatients": 42,
    "activePatients": 40,
    "inactivePatients": 2
  }
}
```

### `DELETE /api/v1/patients/delete-patient/{patientId}`

Soft-delete. Response `data` is `null`.

---

## `PatientResponse` fields

`id`, `patientCode`, `firstName`, `lastName`, `fullName`, `gender`, `dateOfBirth`, `age`, `preferredLanguage`, `email`, `mobileNumber`, `state`, `city`, `address`, emergency contact fields, `idProofType`, `idProofNumber`, `occupation`, `insuranceDetails`, `status`, `createdAt`, `updatedAt`.

**Note:** No update endpoint in the live controller — create / get / list / count / delete only.

← [API_GUIDE.md](../API_GUIDE.md)
