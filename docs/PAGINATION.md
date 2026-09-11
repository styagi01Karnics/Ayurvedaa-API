# List API pagination

High-volume GET list APIs now return a shared paged envelope (unless noted).

## Query params
| Param | Default | Max |
|-------|---------|-----|
| `page` | `0` | — |
| `size` | `20` | `100` |

## Response shape (`com.ayurveda.common.dto.PagedResponse`)
```json
{
  "success": true,
  "data": {
    "content": [ /* rows */ ],
    "page": 0,
    "size": 20,
    "totalElements": 125,
    "totalPages": 7
  }
}
```

**Frontend:** use `data.content` (not `data` as an array).

## Updated endpoints
| Service | Endpoints |
|---------|-----------|
| patient | `GET /api/v1/patients/get-all-patients` |
| doctor | `GET /api/v1/doctors`, `GET /api/v1/doctors/active` |
| therapist | `GET /api/v1/therapists` |
| appointment | `/patients`, `/cancelled`, `/status/{status}`, `/date/{date}`, `/patient/{id}`, `/today/consultation-type/{id}` |
| follow-up | `GET /api/v1/follow-ups`, `/patient/{id}` |
| medicine | `GET /api/v1/medicines`, `/low-stock` |
| billing | `GET /api/v1/invoices`, `/invoices/patient/{id}`, `/billings`, `/billings/patient/{id}` |
| sales | `GET /api/v1/sales` (rows in `sales` + `page/size/totalElements/totalPages`) |

Already paginated before: appointment `/today`, `/doctor/{id}/today`, therapist today schedule; auth `/users/paged`.

## Dropdown tip
For doctor/therapist pickers that need many rows, call with `size=100`.

## Left unpaged (small masters)
Dosha, consultation types, therapy catalog, package masters, medicine name dropdown, UI pages, roles.
