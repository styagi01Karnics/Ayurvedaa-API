# file-upload-service API (Step-2)

| Item | Value |
| --- | --- |
| Port | `8105` |
| Base URL | `http://localhost:8105` |
| Prefix | `/api/v1/documents` |
| Auth | Bearer hospital JWT |
| Endpoints | **4** |

Upload patient clinical documents into the hospital schema. Prefer early in Step-2 when registration needs attachments; medical assessment can also attach via appointment-service multipart.

---

## Endpoints

### `POST /api/v1/documents/upload` (multipart)

```http
POST http://localhost:8105/api/v1/documents/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

| Part | Type | Notes |
| --- | --- | --- |
| `patientId` | UUID | Required |
| `documentType` | enum | `PAST_MEDICAL_REPORT` \| `PRESCRIPTION` \| `LAB_REPORT` |
| `file` | file | Multipart file |

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "id": "…",
    "patientId": "…",
    "documentType": "LAB_REPORT",
    "fileName": "cbc.pdf",
    "fileType": "application/pdf",
    "fileSize": 12345,
    "downloadUrl": "/api/v1/documents/…/download"
  }
}
```

### `GET /api/v1/documents/{patientId}`

List documents for patient → `List<AppointmentDocumentResponse>`.

### `GET /api/v1/documents/{documentId}/download`

Returns **file stream** (`ResponseEntity<Resource>`), **not** `ApiResponse`.

### `DELETE /api/v1/documents/{documentId}`

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": "…"
}
```

← [API_GUIDE.md](../API_GUIDE.md)
