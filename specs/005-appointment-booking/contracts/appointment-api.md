# Appointment API Contract

All endpoints are versioned beneath `/api/v1`, use JSON, validate request DTOs, and return the existing `ApiError` shape on failure: `timestamp`, `status`, `code`, `message`, and `path`. Production endpoints are guarded by the fail-closed staff-identity adapter until real authentication is integrated. Test identity is configured only by test fixtures; no normal request field establishes identity, role, hospital, branch, or authorization scope.

## Availability

`GET /api/v1/hospitals/{hospitalId}/branches/{branchId}/doctors/{doctorId}/available-slots?from=YYYY-MM-DD&to=YYYY-MM-DD`

- `from` and `to` are inclusive branch-local calendar dates; `from` cannot follow `to`, and the range must span 31 days or fewer, including daylight-saving transitions.
- Returns `200` with `doctorId`, `branchId`, `branchTimezone`, and ordered slot values `{startAt, endAt}`. Times are offset-aware instants.
- The service confirms trusted staff scope and doctor branch eligibility before returning slots.

## Create appointment

`POST /api/v1/hospitals/{hospitalId}/branches/{branchId}/appointments`

```json
{
  "patientId": "uuid",
  "doctorId": "uuid",
  "startAt": "2026-09-01T04:30:00Z",
  "endAt": "2026-09-01T04:45:00Z"
}
```

Returns `201` with `id`, `appointmentNumber`, `hospitalId`, `branchId`, `patientId`, `doctorId`, `startAt`, `endAt`, `status`, and `version`. A slot must exactly match a scheduling-derived slot.

## Read appointment

- `GET /api/v1/hospitals/{hospitalId}/branches/{branchId}/appointments/{appointmentId}`
- `GET /api/v1/hospitals/{hospitalId}/branches/{branchId}/appointments/appointment-number/{appointmentNumber}`

Both return the appointment response with `200` only inside trusted staff scope. Outside scope is non-disclosing `404`.

## Cancel appointment

`POST /api/v1/hospitals/{hospitalId}/branches/{branchId}/appointments/{appointmentId}/cancel`

```json
{
  "version": 3,
  "reason": "Patient requested change"
}
```

`reason` is optional. Returns `200` with status `CANCELLED` and incremented version. Cancellation is permitted only for a `BOOKED` appointment before its start.

## Reschedule appointment

`POST /api/v1/hospitals/{hospitalId}/branches/{branchId}/appointments/{appointmentId}/reschedule`

```json
{
  "version": 3,
  "startAt": "2026-09-02T04:30:00Z",
  "endAt": "2026-09-02T04:45:00Z",
  "reason": "Doctor availability changed"
}
```

`reason` is optional. The requested interval must differ from the current interval. Returns `200` with the same ID and appointment number, a booked replacement interval, and incremented version. The change is atomic.

## Error behavior

| Situation | HTTP | Code |
|---|---:|---|
| Invalid UUID, absent required field, invalid date range/format | 400 | `VALIDATION_ERROR` |
| No real production identity | 401 | `UNAUTHENTICATED` |
| Authenticated identity lacks `APPOINTMENT_SCHEDULER` permission | 403 | `FORBIDDEN` |
| Missing or out-of-scope hospital, branch, doctor, patient, or appointment | 404 | `NOT_FOUND` |
| Unavailable/past/ended slot, stale version, invalid transition, concurrent reservation | 409 | `SLOT_UNAVAILABLE`, `STALE_VERSION`, or `CONFLICT` |
