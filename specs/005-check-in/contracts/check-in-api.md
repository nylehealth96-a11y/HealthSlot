# Check-In API Contract

Base path: `/api/v1`. IDs are UUIDs. Clients never provide identity, role, hospital, branch, or authorization scope. Until real authentication is integrated, production endpoints reject access fail closed. Errors use the global envelope; absent and out-of-scope resources are indistinguishable in status, error code, body shape, audit metadata, and PII-free log fields.

## Check in appointment

`POST /api/v1/hospitals/{hospitalId}/branches/{branchId}/appointments/{appointmentId}/check-in`

No request body. Requires trusted reception access and an in-scope, eligible `BOOKED` appointment with no linked queue entry. The appointment module owns the operational transition from `BOOKED` without a queue link to `BOOKED` with one queue link; no `CHECKED_IN` appointment status is created. That linkage/state mutation, queue entry, and required audit record are one transaction: any failure rolls back every part.

| Result | HTTP |
|---|---:|
| New entry | `201 Created` with `QueueEntryResponse` |
| Repeated successful check-in | `200 OK` with existing response |
| Malformed input | `400 Bad Request` |
| No production identity | `401 Unauthorized` |
| Lacks reception permission | `403 Forbidden` |
| Unknown/out-of-scope appointment | `404 Not Found` |
| `CANCELLED`/non-`BOOKED` state, existing conflicting active visit, closed window, or concurrent walk-in race | `409 Conflict` without protected-resource disclosure |

## Register walk-in

`POST /api/v1/hospitals/{hospitalId}/branches/{branchId}/queue-entries/walk-ins`

Request body: `{ "patientId": "<uuid>", "doctorId": "<uuid>" }`.

Requires trusted reception access, scoped patient/doctor, valid branch membership, and a scheduling-owner-confirmed available doctor slot. Availability is revalidated immediately before the transaction commits. It creates no appointment.

| Result | HTTP |
|---|---:|
| Created | `201 Created` with `QueueEntryResponse` |
| Missing/malformed body | `400 Bad Request` |
| No production identity | `401 Unauthorized` |
| Lacks permission | `403 Forbidden` |
| Unknown/out-of-scope patient, doctor, or branch | `404 Not Found` |
| No available slot at authoritative revalidation, active duplicate, or concurrent appointment-check-in race | `409 Conflict` without protected-resource disclosure |

## Retrieve entry

`GET /api/v1/hospitals/{hospitalId}/branches/{branchId}/queue-entries/{queueEntryId}`

`GET /api/v1/hospitals/{hospitalId}/branches/{branchId}/queue-entries/by-reference/{queueReference}`

Both require trusted scoped reception access; no unscoped global-reference lookup exists.

## QueueEntryResponse

```json
{
  "id":"31b026aa-da2d-4238-8301-26bdef472ca0",
  "queueReference":"Q-20260824-J7M9K2",
  "hospitalId":"c18d2169-b037-43f1-b90b-68bf1f21a1cc",
  "branchId":"0e5f7d22-8c94-44b9-a947-a9e02349e17e",
  "doctorId":"c6fe1f5a-590d-4ea3-8f12-15f5c1b47a36",
  "patientId":"0c6b38bd-47ae-420b-9df1-fb639f7d55f3",
  "appointmentId":"9bfcdd77-c854-4a02-8465-3730d052ad65",
  "source":"APPOINTMENT_CHECK_IN",
  "visitDate":"2026-08-24",
  "arrivedAt":"2026-08-24T09:45:00Z",
  "status":"WAITING"
}
```

`appointmentId` is omitted for walk-ins. Responses contain operational IDs only, never patient contact/demographic or clinical data.
