# Ayurvedaa API flow diagrams

All services except attendance. JWT on every hospital API unless marked public.

Companion: [FULL_API_DOCUMENT.md](./FULL_API_DOCUMENT.md)

---

## 1. Request path (every hospital API)

```mermaid
flowchart TD
  ui([UI or client]) --> login{Have JWT?}
  login -->|No| authLogin["POST auth :8111 /auth/login"]
  authLogin --> token[Store accessToken]
  login -->|Yes| token
  token --> call["Call service PORT + path with Authorization Bearer"]
  call --> jwt[JWT parsed schemaName hosp_*]
  jwt --> sa{schemaName hospital?}
  sa -->|No Super Admin| forbid[403 clinical APIs]
  sa -->|Yes| db[(Hospital schema tables)]
  db --> envelope[ApiResponse success status message data]
```

---

## 2. Auth: who logs in where

```mermaid
flowchart TD
  start([Open app]) --> tenants["GET :8111 /api/v1/public/tenants"]
  tenants --> pick[Pick tenantCode]
  pick --> who{User type?}
  who -->|Hospital admin or staff| hLogin["POST /auth/login tenantCode + email + password"]
  who -->|Super Admin| sLogin["POST /auth/login email + password NO tenantCode"]
  hLogin --> hOk{Hospital ACTIVE?}
  hOk -->|INACTIVE SUSPENDED| h403[403]
  hOk -->|Yes| hJwt[JWT schemaName hosp_* pageCodes]
  hJwt --> clinical[Patient Doctor Appointment Billing Medicine Payment File Therapist Notification Activity]
  sLogin --> sJwt[JWT platform public schema]
  sJwt --> platform["/api/v1/platform/hospitals mail status"]
```

---

## 3. Super Admin hospital lifecycle

```mermaid
flowchart LR
  sa([Super Admin JWT]) --> listH["GET /platform/hospitals"]
  sa --> onboard["POST /platform/hospitals"]
  onboard --> schema[Provision hosp_* schema]
  schema --> mail["PUT /platform/hospitals/id/mail"]
  mail --> extra["POST /hospitals/id/admins"]
  listH --> status["PUT /hospitals/id/status ACTIVE INACTIVE SUSPENDED"]
  listH --> retry["POST /hospitals/id/retry-provision"]
```

---

## 4. Hospital users and roles

```mermaid
flowchart TD
  admin([Hospital ADMIN JWT]) --> me["GET PUT /auth/me"]
  admin --> pwd["PUT /auth/change-password"]
  admin --> users["GET /auth/users"]
  admin --> reg["POST /auth/register-user"]
  admin --> pages["GET /ui-pages"]
  pages --> roles["POST GET PUT DELETE /roles"]
  roles --> assign[User tenantRoleId + pageCodes]
```

---

## 5. Masters before booking

```mermaid
flowchart TD
  setup([Hospital JWT]) --> ct["POST GET /consultation-types CONSULTATION THERAPY"]
  setup --> doc["POST GET /doctors :8102"]
  setup --> cat["POST GET /treatment-categories"]
  cat --> th["POST GET /therapies"]
  th --> therapist["POST GET /therapists assignedTherapyIds"]
  setup --> dosha["POST GET /doshas"]
  setup --> tpm["POST GET /treatment-plan-masters"]
  setup --> pkg["POST GET /package-masters"]
  setup --> med["POST GET /medicines"]
  ct --> ready([Ready to book])
  doc --> ready
  therapist --> ready
```

---

## 6. Appointment booking (creates patient)

```mermaid
flowchart TD
  start([Book visit]) --> body["POST :8103 /appointments nested patient + doctor + consultationTypeIds"]
  body --> dup{Email or mobile exists?}
  dup -->|Yes| e409[409 do not pre-create]
  dup -->|No| pCreate["patient-service create-patient"]
  pCreate --> slot[Check doctor slot]
  slot --> save[Save booking SCHEDULED]
  save --> types[Save appointment_consultation_types]
  types --> mail([Email patient best effort])
  save --> get["GET /appointments/today stats patients doctor/id/today"]
  get --> resched["PUT /id/reschedule"]
  get --> cancel["PUT /id/cancel"]
  get --> incons["PUT /id/in-consultation"]
  incons --> done["PUT /id/complete"]
```

---

## 7. In-clinic visit (appointment APIs)

```mermaid
flowchart TD
  booked([SCHEDULED]) --> startC["PUT /appointments/id/in-consultation"]
  startC --> assess["POST /medical-assessment JSON or with-documents"]
  assess --> secs[Optional section APIs ayurvedic physical history lifestyle systemic treatment-plan]
  assess --> docs["POST :8105 /documents/upload"]
  startC --> rx["POST /prescriptions"]
  rx --> fu["POST /follow-ups"]
  startC --> at["POST /appointment-therapies"]
  at --> tstat["PUT /appointment-therapies/id/status"]
  startC --> treat["POST /treatments"]
  treat --> complete["PUT /appointments/id/complete"]
```

---

## 8. Billing and payment

```mermaid
flowchart TD
  patient([patientId]) --> pend["Optional POST :8109 /billings PENDING"]
  pend --> gen["POST /billings/id/generate-invoice"]
  patient --> inv["POST /invoices visitType CONSULTATION FOLLOW_UP THERAPY PACKAGE"]
  gen --> invDone[Invoice UNPAID leftAmount]
  inv --> invDone
  invDone --> how{Pay how?}
  how -->|Cash| cash["POST /invoices/id/payments CASH"]
  cash --> full{Full amount?}
  full -->|Yes| done[COMPLETED]
  full -->|No| ongoing[ONGOING]
  ongoing --> link
  how -->|Online email or QR| link["POST :8112 /payments/links sendEmail or upiQr"]
  link --> email[Patient opens payUrl]
  email --> pub["GET /payments/public/token"]
  pub --> payu["POST /payments/public/token/initiate"]
  payu --> cb["PayU /payments/payu/success"]
  cb --> kafka[Kafka PAYMENT_UPDATED]
  kafka --> done
  done --> refundC["Cash POST /invoices/id/refunds"]
  done --> refundP["Online POST /payments/id/refund"]
```

---

## 9. Service map (which port)

```mermaid
flowchart LR
  ui([Client]) --> a8111[Auth 8111]
  ui --> p8101[Patient 8101]
  ui --> d8102[Doctor 8102]
  ui --> ap8103[Appointment 8103]
  ui --> t8104[Therapist 8104]
  ui --> f8105[File 8105]
  ui --> l8107[Activity 8107]
  ui --> m8108[Medicine 8108]
  ui --> b8109[Billing 8109]
  ui --> n8110[Notification 8110]
  ui --> pay8112[Payment 8112]
  ap8103 -.-> p8101
  ap8103 -.-> d8102
  b8109 -.-> m8108
  pay8112 -.-> b8109
  pay8112 -.-> n8110
```

---

## 10. Side effects (email, logs, stock)

```mermaid
flowchart TD
  createP[Patient create or appointment book] -.-> n1["notification-service email"]
  createP -.-> a1["activity-log CREATED"]
  invPay[Invoice cash or PayU success] -.-> n2[Payment and invoice emails]
  invPay -.-> a2[activity-log]
  sellMed[Invoice with medicines] -.-> deduct["POST /medicines/id/stock/deduct"]
  delInv[Delete invoice with medicines] -.-> restore["POST /medicines/id/stock/restore"]
```

Attendance (`:8106`) is excluded.
