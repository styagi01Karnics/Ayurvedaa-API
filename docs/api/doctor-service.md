# doctor-service API (Step-2)

| Item | Value |
| --- | --- |
| Port | `8102` |
| Base URL | `http://localhost:8102` |
| Prefix | `/api/v1/doctors` |
| Auth | Bearer hospital JWT |

Master data for hospital doctors. Entity stores `doctorCode` (`{tenantCode}-DOC-#####`); list/get responses expose fields from `DoctorResponse` below.

---

## Endpoints (6)

### `POST /api/v1/doctors`

```http
POST http://localhost:8102/api/v1/doctors
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "name": "Dr. Mehta",
  "specialization": "Panchakarma",
  "status": "ACTIVE",
  "consultationFees": 500.00,
  "followUpFees": 300.00,
  "availability": "Mon-Fri 10:00-14:00"
}
```

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "id": "…",
    "name": "Dr. Mehta",
    "specialization": "Panchakarma",
    "qualification": null,
    "mobileNumber": null,
    "status": "ACTIVE",
    "consultationFees": 500.00,
    "followUpFees": 300.00,
    "availability": "Mon-Fri 10:00-14:00"
  }
}
```

**Request (`CreateDoctorRequest`):** `name`, `specialization`, `status?`, `consultationFees`, `followUpFees`, `availability`.  
(`qualification` / `mobileNumber` are not on the create DTO; response may return null.)

### `GET /api/v1/doctors`

List all doctors → `List<DoctorResponse>`.

### `GET /api/v1/doctors/active`

Active doctors only → `List<DoctorResponse>`.

### `GET /api/v1/doctors/{doctorId}`

Path UUID → `DoctorResponse`.

### `PATCH /api/v1/doctors/{doctorId}/status`

```json
{ "status": "INACTIVE" }
```

→ `DoctorResponse`.

### `DELETE /api/v1/doctors/{doctorId}`

Soft/hard delete per service impl → `data: null`.

← [API_GUIDE.md](../API_GUIDE.md)
