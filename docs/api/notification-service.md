# notification-service API (Step-2 / shared)

| Item | Value |
| --- | --- |
| Port | `8110` |
| Base URL | `http://localhost:8110` |
| Prefix | `/api/v1/notifications` |
| Endpoints | **8** |

Unlike clinical services, notification-service does **not** enable hospital `schemaName` routing by default (shared/public). There is no Spring `SecurityConfig` in this service — treat as internal/open unless a gateway adds auth. Auth-service forgot-password calls email here.

---

## Endpoints

### `POST /api/v1/notifications/email`

Service-to-service email send.

```json
{
  "to": "user@gmail.com",
  "subject": "Password reset",
  "body": "<p>Reset link…</p>"
}
```

→ `ApiResponse` with `data: null`.

### `POST /api/v1/notifications`

Create in-app notification.

```json
{
  "recipientUserId": "…",
  "recipientUserName": "Ravi",
  "recipientRole": "ADMIN",
  "title": "Appointment reminder",
  "message": "You have a visit at 10:30",
  "type": "APPOINTMENT",
  "priority": "NORMAL",
  "referenceId": "…",
  "referenceType": "BOOKING"
}
```

Response `NotificationResponse`: `id`, recipient fields, `title`, `message`, `type`, `priority`, `referenceId`, `referenceType`, `read`, `readAt`, `createdAt`.

| Method | Path | Params / body | Notes |
| --- | --- | --- | --- |
| GET | `/` | Query **`userId`** (req), `unreadOnly?`, `type?` | List for user |
| GET | `/unread-count` | Query **`userId`** | `{ "unreadCount": n }` |
| GET | `/{notificationId}` | path | By id |
| PUT | `/{notificationId}/read` | — | Mark one read |
| PUT | `/read-all` | Query **`userId`** | Mark all read |
| DELETE | `/{notificationId}` | — | Soft delete |

← [API_GUIDE.md](../API_GUIDE.md)
