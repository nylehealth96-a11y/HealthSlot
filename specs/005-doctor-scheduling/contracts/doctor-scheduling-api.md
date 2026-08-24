# Doctor Scheduling API Contract

All routes are beneath `/api/v1/hospitals/{hospitalId}/branches/{branchId}/doctors/{doctorId}`. Hospital, branch, doctor, role, actor, and authorization scope are derived from path ownership and the trusted identity abstraction; they are never read from normal request input.

Until real authentication is installed, the production `SchedulingAccessContext` fails closed. Only automated tests/local test configuration may install `TrustedTestSchedulingAccessContext` from test sources.

## Endpoints

| Method and path | Purpose | Request / response |
|---|---|---|
| `PUT /schedule` | Create an effective-dated recurring revision | Request has `effectiveStartDate`, positive `slotDurationMinutes`, `days[]`, and `expectedVersion` when replacing an existing revision. Response is the complete revision with `id` and `version`. |
| `GET /schedule?onDate=YYYY-MM-DD` | Retrieve the effective recurring revision | Response contains the selected revision and branch timezone. |
| `POST /leave` | Record inclusive leave | Request has `startDate`, `endDate`; response has `id`, `version`. |
| `PUT /leave/{leaveId}` / `DELETE /leave/{leaveId}` | Replace or remove leave | Update/delete require `expectedVersion`. |
| `POST /exceptions` | Create a date replacement | Request has `date`, `workingPeriods`. An empty `workingPeriods` array closes that date. |
| `PUT /exceptions/{exceptionId}` / `DELETE /exceptions/{exceptionId}` | Replace or remove an exception | Update/delete require `expectedVersion`. |
| `GET /available-slots?from=YYYY-MM-DD&to=YYYY-MM-DD` | Derive availability | Response has `branchTimezone` and ordered slots with `start`, `end` UTC instants plus local date/time fields. |

## Common schedule payload shape

```json
{
  "effectiveStartDate": "2026-09-01",
  "slotDurationMinutes": 30,
  "days": [{
    "dayOfWeek": "MONDAY",
    "workingPeriods": [{
      "startTime": "09:00",
      "endTime": "17:00",
      "breaks": [{"startTime": "12:30", "endTime": "13:00"}]
    }]
  }],
  "expectedVersion": 3
}
```

## Errors

The standard API error envelope is `{timestamp,status,code,message,path}`.

| Status | Code | Meaning |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Invalid values, ranges, periods, or breaks. |
| 401 | `UNAUTHENTICATED` | Production identity integration is unavailable or no identity exists. |
| 403 | `FORBIDDEN` | Trusted identity lacks hospital/branch/role/doctor scope; the response does not disclose resource details. |
| 404 | `NOT_FOUND` | Hospital, branch, doctor, or owned resource does not exist for an otherwise authorized scope. |
| 409 | `CONFLICT` / `STALE_REVISION` | Overlap, duplicate effective/date rule, or stale update. |
