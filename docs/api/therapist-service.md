# therapist-service API (Step-2)

| Item | Value |
| --- | --- |
| Port | `8104` |
| Base URL | `http://localhost:8104` |
| Prefix | `/api/v1/therapists` |
| Auth | Bearer hospital JWT |

Business code: `{tenantCode}-THP-#####`. Assign therapies by UUID (create therapies in appointment-service first).

---

## Endpoints (7)

### `POST /api/v1/therapists`

```json
{
  "name": "Anita Sharma",
  "status": "ACTIVE",
  "assignedTherapyIds": ["…-therapy-uuid-…"]
}
```

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "id": "…",
    "name": "Anita Sharma",
    "therapistName": "Anita Sharma",
    "therapistCode": "GAN-DL-THP-00001",
    "status": "ACTIVE",
    "assignedTherapies": [{ "id": "…", "name": "Abhyanga" }]
  }
}
```

### `PUT /api/v1/therapists/{therapistId}`

Body `UpdateTherapistRequest`: `name`, `status`, `assignedTherapyIds` → `TherapistResponse`.

### `GET /api/v1/therapists`

List all → `List<TherapistResponse>`.

### `GET /api/v1/therapists/{therapistId}`

By id → `TherapistResponse`.

### `GET /api/v1/therapists/by-therapies`

Query: `therapyIds` (list of UUIDs) → therapists who can perform those therapies.

### `PATCH /api/v1/therapists/{therapistId}/status`

```json
{ "status": "INACTIVE" }
```

### `DELETE /api/v1/therapists/{therapistId}`

→ `data: null`.

← [API_GUIDE.md](../API_GUIDE.md)
