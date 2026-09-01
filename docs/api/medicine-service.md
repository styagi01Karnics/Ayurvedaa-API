# medicine-service API (Step-2)

| Item | Value |
| --- | --- |
| Port | `8108` |
| Base URL | `http://localhost:8108` |
| Auth | Bearer hospital JWT |
| ~Endpoints | **14** |

Inventory under hospital schema. Seed medicines before prescriptions / invoice medicine lines.

---

## Medicines — `/api/v1/medicines` (13)

### `POST /api/v1/medicines`

Accepts a **single** object, a **JSON array**, or `{ "medicines": [ … ] }`. Response is always `List<MedicineResponse>`.

```json
{
  "medicineName": "Ashwagandha Churna",
  "category": "POWDER",
  "manufacturer": "Himalaya",
  "batchNumber": "B001",
  "quantity": 100,
  "expiryDate": "2027-12-31",
  "purchasePrice": 80.00,
  "sellingPrice": 120.00,
  "lowStockAlertEnabled": true,
  "lowStockThreshold": 10,
  "status": "ACTIVE"
}
```

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": [
    {
      "id": "…",
      "medicineName": "Ashwagandha Churna",
      "category": "POWDER",
      "manufacturer": "Himalaya",
      "batchNumber": "B001",
      "stockQuantity": 100,
      "expiryDate": "2027-12-31",
      "purchasePrice": 80.00,
      "sellingPrice": 120.00,
      "price": 120.00,
      "lowStockAlertEnabled": true,
      "lowStockThreshold": 10,
      "status": "ACTIVE",
      "stockStatus": "IN_STOCK"
    }
  ]
}
```

| Method | Path | Notes |
| --- | --- | --- |
| PUT | `/{medicineId}` | `UpdateMedicineRequest` (same fields as create + status) |
| GET | `/` | Query `medicineName?`, `category?`, `stockStatus?` |
| GET | `/stock/summary` | Totals by category |
| GET | `/stock/category/{category}` | Path `MedicineCategory` |
| GET | `/low-stock` | Below threshold |
| GET | `/meta/categories` | Category enum list |
| GET | `/meta/manufacturers` | Distinct manufacturer names |
| GET | `/meta/names` | `{ id, medicineName }` dropdown |
| GET | `/{medicineId}` | By id |
| POST | `/{medicineId}/stock/deduct` | `{ "quantity": 1 }` |
| POST | `/{medicineId}/stock/restore` | `{ "quantity": 1 }` |
| DELETE | `/{medicineId}` | Soft/hard delete → `null` |

---

## Dashboard — `/api/v1/dashboard` (1)

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/medicine-stock` | Query `lowStockLimit` (default 5) → totals + low-stock items |

← [API_GUIDE.md](../API_GUIDE.md)
