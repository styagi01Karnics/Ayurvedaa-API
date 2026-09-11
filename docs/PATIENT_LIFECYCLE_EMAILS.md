# Patient lifecycle emails (hospital SMTP)

Patient-facing emails go through **notification-service** using the hospital mailbox
(`PUT /api/v1/platform/hospitals/{hospitalId}/mail`). If the patient has **no email**, the event still succeeds and no mail is sent.

## Events that email the patient

| Event | Service | When email is sent |
|--------|---------|--------------------|
| Patient registered | patient-service | `POST .../create-patient` and email present |
| Appointment booked | appointment-service | After booking; uses patient email |
| Appointment rescheduled | appointment-service | After reschedule |
| Appointment cancelled | appointment-service | After cancel |
| Follow-up scheduled | appointment-service | After follow-up create |
| Follow-up cancelled | appointment-service | After follow-up cancel |
| Invoice created | billing-service | When request includes `patientEmail` |
| Payment link | payment-service | `sendEmail=true` / resend (existing) |
| Payment success (PayU) | payment-service | First transition to SUCCESS |
| Payment failed / cancelled | payment-service | Existing |
| Refund confirmation | billing / payment | When patient email provided (existing) |
| Password reset | auth-service | Existing (staff/user, not patient chart) |

## Frontend note (invoice)

`CreateInvoiceRequest` accepts optional:

```json
"patientEmail": "patient@example.com"
```

Pass the patient’s registered email so invoice confirmation can be sent. Without it, invoice create still works with no email.

## Failure behaviour (API never fails)

Email is **best-effort**. Patient create, appointment, invoice, payment, etc. **always succeed** even when mail fails.

| Outcome | HTTP from notification `/email` | Business API |
|---------|----------------------------------|--------------|
| SMTP ok | 200, `deliveryStatus=SENT` | Success |
| SMTP fails after 3 retries | 200, `deliveryStatus=FAILED` + `errorMessage` | **Still success** |
| Notification down / network error | Publisher logs warn, swallows exception | **Still success** |
| No / blank patient email | Skipped (no call) | Success |

Delivery attempts are stored in `email_delivery_logs` (`SENT` / `FAILED` / `SKIPPED`) for ops review. Wrong-but-valid addresses may still show `SENT` (SMTP accepted; bounce is outside the app).
