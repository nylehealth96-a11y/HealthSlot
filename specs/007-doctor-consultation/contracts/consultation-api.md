# Doctor Consultation API Contract

All routes are beneath `/api/v1/hospitals/{hospitalId}/branches/{branchId}`. Path identifiers locate a resource but do not grant authority: the service verifies trusted server-side staff identity, hospital, branch, role, and doctor ownership. Until real authentication is integrated, production routes fail closed; only a clearly named test-only trusted context may be used in automated tests/local verification.

## Call Patient

`POST /consultations/{consultationId}/call`

- Authority: trusted reception staff in the consultation's hospital/branch scope.
- Preconditions: consultation is `WAITING` and originates from the authoritative check-in queue entry.
- Success: `200 OK` with the lifecycle view in state `CALLED` and its `calledAt` instant.
- Failures: `400` for malformed identifiers; `404` for absent/out-of-scope resources; `409` for invalid state or a concurrent duplicate call.

## Start Consultation

`POST /consultations/{consultationId}/start`

- Authority: only the assigned doctor in trusted scope.
- Preconditions: consultation is `CALLED`.
- Success: `200 OK` with state `IN_CONSULTATION` and `actualStartAt`.
- Failures: `400` for malformed identifiers; `404` for absent/out-of-scope resources; `409` for invalid state or concurrent transition.

## Complete Consultation

`POST /consultations/{consultationId}/complete`

- Authority: only the assigned doctor in trusted scope.
- Preconditions: consultation is `IN_CONSULTATION`.
- Success: `200 OK` with state `COMPLETED`, `actualStartAt`, `actualEndAt`, and confirmation that the queue-recalculation hand-off was accepted.
- Failures: `400` for malformed identifiers; `404` for absent/out-of-scope resources; `409` for invalid state/concurrency; the standard server failure response only when the documented transactional/recovery boundary cannot be established.

## Lifecycle View

The response contains consultation ID, state, doctor ID, branch ID, `calledAt`, `actualStartAt`, `actualEndAt`, and version. It excludes patient names, contact details, clinical content, and staff identity details.

## Error and Idempotency Semantics

The existing global error shape is used. Unknown and out-of-scope resources both return the same non-disclosing `404`. Requests do not accept actor, role, staff, hospital, branch, or authorization scope in their body. Repeating a completed transition is not success-idempotent: it receives `409`, while the persisted lifecycle state remains unchanged.
