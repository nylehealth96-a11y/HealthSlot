# Patient API Contract

All endpoints are versioned beneath `/api/v1/hospitals/{hospitalId}`. The caller must be authenticated reception staff authorized for the path hospital. Failures use the existing global error format.

## Register patient

`POST /api/v1/hospitals/{hospitalId}/patients` creates a patient and returns `201 Created` with the complete profile including `id`, `patientNumber`, and `version`.

Required request fields: `firstName`, `lastName`, `dateOfBirth` (`YYYY-MM-DD`), `gender`, and `mobileNumber`. Optional: `email`, `address`, and `emergencyContact`; an emergency contact contains required `name`, `relationship`, and `mobileNumber` fields.

## Retrieve patient

- `GET /api/v1/hospitals/{hospitalId}/patients/{patientId}` retrieves by UUID.
- `GET /api/v1/hospitals/{hospitalId}/patients/by-number/{patientNumber}` retrieves by patient number.

Both return `200 OK` with the profile or `404 Not Found` if it is not in the path hospital. An unauthenticated caller receives `401 Unauthorized`; an authenticated caller without authorization receives `404 Not Found` to avoid disclosing the record.

## Search patients

`GET /api/v1/hospitals/{hospitalId}/patients?query={text}&page={zeroBasedPage}&size={pageSize}`

`query` is required, trimmed, and matched case-insensitively against patient number, name, and mobile number. Return a bounded page of summaries with `id`, `patientNumber`, names, date of birth, and mobile number. No matches or an out-of-range page return `200 OK` with an empty page.

## Update basic information

`PUT /api/v1/hospitals/{hospitalId}/patients/{patientId}` requires all basic fields and the current `version`; immutable `id`, `hospitalId`, and `patientNumber` are not update fields. On success return `200 OK` with an incremented version. A stale version returns `409 Conflict` and tells staff to refresh and review. Invalid fields return `400`; a patient outside the hospital returns `404`.

## Common rules

- Do not log request bodies or emit patient PII in audit metadata.
- Authorization uses trusted staff identity, reception-staff role, branch affiliation, the path hospital, and patient hospital ownership; request input cannot supply these authorization facts.
- Repeated mobile numbers are valid and never identify, merge, or overwrite a patient.
- Validation and authorization failures use the project’s standard error response shape. Unauthenticated callers receive `401`; authenticated callers without hospital/patient authorization receive non-disclosing `404` responses.
