# billing-service API (Step-2)

| Item | Value |
| --- | --- |
| Port | `8109` |
| Base URL | `http://localhost:8109` |
| Auth | Bearer hospital JWT |
| ~Endpoints | **24** |

Invoice business code: `{tenantCode}-INV-#####` — [CODES.md](../CODES.md).

**Typical flow:** create package masters → optional patient packages → doctor `POST /billings` (PENDING) → `POST …/generate-invoice` **or** create invoice directly → part payments → sales/dashboard.

---

## 1. Billings — `/api/v1/billings` (5)

### `POST /api/v1/billings`

Doctor-side pending billing.

```json
{
  "patientId": "…",
  "patientName": "Ravi Kumar",
  "contactNumber": "9876543210",
  "billingDate": "2026-08-31",
  "services": [
    {
      "serviceType": "CONSULTATION",
      "serviceFees": 500.00,
      "packageMasterId": null,
      "packageType": null,
      "packageCharges": null
    }
  ]
}
```

Response `BillingResponse`: `id`, patient fields, `status`, `invoiceId`, `invoiceNumber`, `totalAmount`, `services[]`, timestamps.

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/` | Query `status?` → `List<BillingListResponse>` |
| GET | `/patient/{patientId}` | |
| GET | `/{billingId}` | |
| POST | `/{billingId}/generate-invoice` | Body `CreateInvoiceRequest` → `InvoiceResponse` |

---

## 2. Invoices — `/api/v1/invoices` (6)

### `POST /api/v1/invoices`

```json
{
  "patientId": "…",
  "patientName": "Ravi Kumar",
  "contactNumber": "9876543210",
  "invoiceDate": "2026-08-31",
  "visitType": "OPD",
  "serviceFees": 500.00,
  "packageMasterId": null,
  "packageType": null,
  "packageCharges": null,
  "medicines": [
    { "medicineId": "…", "quantity": 2, "unitPrice": 120.00 }
  ],
  "therapies": [
    {
      "itemName": "Abhyanga",
      "quantity": 1,
      "unitPrice": 800.00,
      "assignedTherapistId": "…",
      "assignedTherapistName": "Anita",
      "scheduleDate": "2026-09-01",
      "scheduleTime": "11:00:00",
      "sessionDuration": 45,
      "sessionFrequency": "ONCE"
    }
  ],
  "discount": 0,
  "taxEnabled": true,
  "cgstPercent": 2.5,
  "sgstPercent": 2.5,
  "amountPaid": 500.00,
  "paymentMethod": "CASH",
  "paymentRemarks": null
}
```

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "id": "…",
    "invoiceId": "GAN-DL-INV-00001",
    "patientId": "…",
    "totalAmount": 1500.00,
    "paidAmount": 500.00,
    "leftAmount": 1000.00,
    "status": "PARTIAL",
    "items": [],
    "payments": []
  }
}
```

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/` | Query `patientId?`, `status?` → list |
| GET | `/patient/{patientId}` | Query `status?` |
| GET | `/{invoiceId}` | Full `InvoiceResponse` |
| POST | `/{invoiceId}/payments` | `{ "amountPaid", "paymentMethod", "remarks" }` |
| DELETE | `/{invoiceId}` | Soft delete |

---

## 3. Patient packages — `/api/v1/packages` (5)

| Method | Path | Body / notes |
| --- | --- | --- |
| POST | `/` | `patientId`, `packageMasterId`, `validity`, `status`, `discountApplied` |
| GET | `/` | All |
| GET | `/patient/{patientId}` | |
| PUT | `/{packageId}` | `packageMasterId`, `validity`, `discountApplied` |
| PUT | `/{packageId}/status` | `{ "status": "…" }` |

---

## 4. Package masters — `/api/v1/package-masters` (4)

| Method | Path | Notes |
| --- | --- | --- |
| POST | `/` | `name`, `packagePrice`, `status` |
| GET | `/` | All |
| GET | `/active` | Active only |
| GET | `/{packageMasterId}` | |

---

## 5. Sales — `/api/v1/sales` (2)

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/` | Query `serviceType?`, `dateCreated?` → `SalesPageResponse` |
| GET | `/revenue/month` | Query `year?`, `month?` → `MonthlyRevenueResponse` |

---

## 6. Dashboard — `/api/v1/dashboard` (1)

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/billing-summary` | Query `period` = `WEEKLY` \| `MONTHLY` \| `YEARLY` (default MONTHLY) |

---

## 7. Patient billing aggregate — `/api/v1/billing` (1)

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/patient/{patientId}` | Query `status?` → summary + invoices + packages |

← [API_GUIDE.md](../API_GUIDE.md)
