# Frontend page permissions (React)

How hospital `pageCodes` drive sidebar tabs and route access. Backend stores **codes only**; the React app maps codes → paths.

Deep API bodies: [auth-service/docs/AUTH_API_GUIDE.md](../auth-service/docs/AUTH_API_GUIDE.md) · Master index: [API_GUIDE.md](./API_GUIDE.md)

---

## What

- Auth returns `user.pageCodes` (e.g. `["DASHBOARD","PATIENTS"]`) on login / `GET /auth/me`.
- Each code is a **module toggle** from the Figma Role Permissions modal (seeded in `UiPageSeeder`).
- FE uses those codes to:
  1. Show/hide **sidebar** items
  2. Guard **routes** (redirect or 403 if the user lacks the code)
- Role admin UI sends `pageCodes` arrays to create/update roles — **never URLs**.

---

## Step-by-step (React)

### 1. Login → persist token + pageCodes

1. `POST http://localhost:8111/api/v1/auth/login`
2. From `data`: save `accessToken`, `user` (at least `id`, `role`, `pageCodes`), and `tenant` (`tenantCode`, `schemaName`, …).
3. Prefer localStorage/sessionStorage or your auth store; every clinical API call needs `Authorization: Bearer <accessToken>`.

### 2. Optional refresh

- On app load / hard refresh: `GET /api/v1/auth/me` with Bearer → replace stored `user` (including `pageCodes`).
- If roles changed server-side, refresh `pageCodes` before rebuilding the nav.

### 3. Build sidebar from allowed codes only

- Keep a static menu config keyed by `pageCode`.
- Render: `menuItems.filter(item => pageCodes.includes(item.pageCode))`.
- Do not show tabs the user cannot open.

### 4. Protect routes with a pageCode guard

- Map each protected path to one `pageCode` (see table below).
- Wrap pages in `<ProtectedRoute pageCode="…">` (or equivalent).
- If missing: redirect to first allowed route or a “no access” screen.
- Auth-only routes (login, reset-password) stay public.

### 5. Role admin UI (hospital ADMIN)

1. `GET /api/v1/ui-pages` → list of `{ pageCode, pageName, … }` for toggles.
2. Toggle On/Off per module; collect **codes** into an array.
3. Create: `POST /api/v1/roles` with `{ roleName, pageCodes, … }`.
4. Update: `PUT /api/v1/roles/{roleId}` with the same `pageCodes` shape.
5. Assign staff via `POST /api/v1/auth/register-user` (`tenantRoleId` for non-ADMIN).

Requires Bearer + `ADMIN` / `SUPER_ADMIN` / `PAGE_SETTINGS`. Full request/response shapes: [AUTH_API_GUIDE.md](../auth-service/docs/AUTH_API_GUIDE.md#frontend-page-permissions).

---

## Mapping: pageCode ↔ Figma ↔ suggested React path

Codes are fixed by `UiPageSeeder` — do not invent new ones.

| pageCode | Figma toggle | Suggested React path |
| --- | --- | --- |
| `DASHBOARD` | Dashboard | `/dashboard` |
| `PATIENTS` | Patients | `/patients` |
| `DOCTORS` | Doctors | `/doctors` |
| `APPOINTMENTS` | Appointments | `/appointments` |
| `TREATMENTS` | Treatments | `/treatments` |
| `MEDICINES` | Medicines | `/medicines` |
| `SALES` | Sales | `/sales` |
| `ACTIVITY_LOG` | Activity Log | `/activity-log` |
| `BILLING` | Billing | `/billing` |
| `SETTINGS` | Settings | `/settings` |

Exact path strings are FE-owned; **codes** are the contract with auth-service.

---

## React samples (compact)

### `PAGE_ROUTES` constant

```ts
export const PAGE_ROUTES = {
  DASHBOARD: "/dashboard",
  PATIENTS: "/patients",
  DOCTORS: "/doctors",
  APPOINTMENTS: "/appointments",
  TREATMENTS: "/treatments",
  MEDICINES: "/medicines",
  SALES: "/sales",
  ACTIVITY_LOG: "/activity-log",
  BILLING: "/billing",
  SETTINGS: "/settings",
} as const;

export type PageCode = keyof typeof PAGE_ROUTES;
```

### `ProtectedRoute`

```tsx
import { Navigate } from "react-router-dom";
import type { PageCode } from "./pageRoutes";

export function ProtectedRoute({
  pageCode,
  pageCodes,
  children,
}: {
  pageCode: PageCode;
  pageCodes: string[];
  children: React.ReactNode;
}) {
  if (!pageCodes.includes(pageCode)) {
    return <Navigate to="/dashboard" replace />; // or first allowed route
  }
  return <>{children}</>;
}
```

### `AppRoutes` map

```tsx
import { Route, Routes } from "react-router-dom";
import { PAGE_ROUTES, type PageCode } from "./pageRoutes";
import { ProtectedRoute } from "./ProtectedRoute";

const PAGES: { code: PageCode; element: React.ReactNode }[] = [
  { code: "DASHBOARD", element: <DashboardPage /> },
  { code: "PATIENTS", element: <PatientsPage /> },
  { code: "DOCTORS", element: <DoctorsPage /> },
  { code: "APPOINTMENTS", element: <AppointmentsPage /> },
  { code: "TREATMENTS", element: <TreatmentsPage /> },
  { code: "MEDICINES", element: <MedicinesPage /> },
  { code: "SALES", element: <SalesPage /> },
  { code: "ACTIVITY_LOG", element: <ActivityLogPage /> },
  { code: "BILLING", element: <BillingPage /> },
  { code: "SETTINGS", element: <SettingsPage /> },
];

export function AppRoutes({ pageCodes }: { pageCodes: string[] }) {
  return (
    <Routes>
      {PAGES.map(({ code, element }) => (
        <Route
          key={code}
          path={PAGE_ROUTES[code]}
          element={
            <ProtectedRoute pageCode={code} pageCodes={pageCodes}>
              {element}
            </ProtectedRoute>
          }
        />
      ))}
    </Routes>
  );
}
```

### Sidebar filter

```tsx
import { PAGE_ROUTES, type PageCode } from "./pageRoutes";

const NAV: { code: PageCode; label: string }[] = [
  { code: "DASHBOARD", label: "Dashboard" },
  { code: "PATIENTS", label: "Patients" },
  { code: "DOCTORS", label: "Doctors" },
  { code: "APPOINTMENTS", label: "Appointments" },
  { code: "TREATMENTS", label: "Treatments" },
  { code: "MEDICINES", label: "Medicines" },
  { code: "SALES", label: "Sales" },
  { code: "ACTIVITY_LOG", label: "Activity Log" },
  { code: "BILLING", label: "Billing" },
  { code: "SETTINGS", label: "Settings" },
];

export function Sidebar({ pageCodes }: { pageCodes: string[] }) {
  const items = NAV.filter((n) => pageCodes.includes(n.code));
  return (
    <nav>
      {items.map((n) => (
        <a key={n.code} href={PAGE_ROUTES[n.code]}>
          {n.label}
        </a>
      ))}
    </nav>
  );
}
```

---

## API pointers (auth-service `:8111`)

| Use | Method / path | Auth |
| --- | --- | --- |
| Login → `user.pageCodes` | `POST /api/v1/auth/login` | Public |
| Refresh user | `GET /api/v1/auth/me` | Bearer |
| Role toggle catalog | `GET /api/v1/ui-pages` | Bearer + ADMIN / SUPER_ADMIN / PAGE_SETTINGS |
| Create / update role | `POST /api/v1/roles`, `PUT /api/v1/roles/{roleId}` | Same |
| List roles | `GET /api/v1/roles` | Same |

Base: `http://localhost:8111` · prefix `/api/v1` · envelope `ApiResponse<T>`.

Full examples and security notes: [AUTH_API_GUIDE.md — Frontend: page permissions](../auth-service/docs/AUTH_API_GUIDE.md#frontend-page-permissions).
