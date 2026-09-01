# activity-log-service API (Step-2)

| Item | Value |
| --- | --- |
| Port | `8107` |
| Base URL | `http://localhost:8107` |
| Prefix | `/api/v1/activity-logs` |
| Auth | Bearer hospital JWT (`schemaName` = `hosp_*`) |
| Endpoints | **3** |

Audit rows are stored in the hospital schema’s `activity_logs` table (PostgreSQL `search_path` from JWT `schemaName`). Clinical services usually write via `ActivityLogPublisher` (common-service); the FE lists/filters with the same Bearer token.

Hospital JWT is required on every endpoint. Super Admin tokens (`schemaName: public`) get **403**. Schema comes from the JWT claim — FE does **not** need `X-Tenant-Schema` (that header is optional for service-to-service publishers).

---

## FE: list / filter (Bearer + hospital schema)

```http
GET http://localhost:8107/api/v1/activity-logs?page=Patients&action=CREATED&search=GAN-DL-PT
Authorization: Bearer <hospitalAccessToken>
```

| Query | Required | Notes |
| --- | --- | --- |
| `page` | no | Exact match on `page` (case-insensitive), e.g. `Patients`, `Appointments`, `Billing` |
| `action` | no | Enum: `VIEWED` \| `CREATED` \| `UPDATED` \| `DELETED` |
| `search` | no | Case-insensitive substring on `page`, `target`, `performedByUserName`, `beforeValue`, `afterValue` |

Omit all query params to return all non-deleted logs (newest `activityTimestamp` first).

```http
GET http://localhost:8107/api/v1/activity-logs/{id}
Authorization: Bearer <hospitalAccessToken>
```

---

## Endpoints

### `POST /api/v1/activity-logs`

Create one audit row. HTTP status **201**; envelope `status` remains `200`.

```http
POST http://localhost:8107/api/v1/activity-logs
Authorization: Bearer <hospitalAccessToken>
Content-Type: application/json
```

```json
{
  "page": "Patients",
  "action": "CREATED",
  "target": "Patient GAN-DL-PT-00001",
  "beforeValue": null,
  "afterValue": "{\"fullName\":\"Ravi Kumar\"}",
  "activityTimestamp": "2026-08-31T10:00:00",
  "performedByUserId": "…",
  "performedByUserName": "Hospital Admin",
  "performedByRole": "ADMIN"
}
```

| Field | Required | Notes |
| --- | --- | --- |
| `page` | yes | Max 100; free-form label used by publishers (e.g. `Patients`, `Settings`) |
| `action` | yes | `VIEWED` \| `CREATED` \| `UPDATED` \| `DELETED` |
| `target` | yes | Max 150 |
| `beforeValue` / `afterValue` | no | Text / JSON string |
| `activityTimestamp` | no | Defaults to server `now` if omitted |
| `performedByUserId` | no | UUID |
| `performedByUserName` | no | Max 150 |
| `performedByRole` | no | Max 50 |

```json
{
  "success": true,
  "status": 200,
  "message": "Activity log created successfully.",
  "data": {
    "id": "…",
    "page": "Patients",
    "action": "CREATED",
    "target": "Patient GAN-DL-PT-00001",
    "before": "-",
    "after": "{\"fullName\":\"Ravi Kumar\"}",
    "timestamp": "2026-08-31T10:00:00",
    "performedByUserId": "…",
    "performedByUserName": "Hospital Admin",
    "performedByRole": "ADMIN"
  }
}
```

Response mapping notes (`ActivityLogResponse`):

- Request `beforeValue` / `afterValue` → response `before` / `after`
- Request `activityTimestamp` → response `timestamp`
- Null/blank before/after values are returned as `"-"`

### `GET /api/v1/activity-logs`

Query filters above → `ApiResponse<List<ActivityLogResponse>>`.

```json
{
  "success": true,
  "status": 200,
  "message": "Activity logs fetched successfully.",
  "data": [
    {
      "id": "…",
      "page": "Patients",
      "action": "CREATED",
      "target": "Patient GAN-DL-PT-00001",
      "before": "-",
      "after": "-",
      "timestamp": "2026-08-31T10:00:00",
      "performedByUserId": null,
      "performedByUserName": null,
      "performedByRole": null
    }
  ]
}
```

### `GET /api/v1/activity-logs/{id}`

Path UUID → single `ActivityLogResponse` (message `"Success"` when found; **404** if missing/soft-deleted).

---

## Service-to-service publishing

`com.ayurveda.common.activity.ActivityLogPublisher` POSTs to
`{services.activity-log.url}/api/v1/activity-logs` (default `http://localhost:8107`) with the same body shape, forwarding the caller’s `Authorization` and optional `X-Tenant-Schema` from `TenantContext`. Failures are logged and do not fail the business call.

Typical `page` labels from live publishers: `Patients`, `Appointments`, `Doctors`, `Medicines`, `Treatments`, `Billing`, `Settings`.

Config: `services.activity-log.url`, `services.activity-log.enabled` (default `true`).

← [API_GUIDE.md](../API_GUIDE.md)
