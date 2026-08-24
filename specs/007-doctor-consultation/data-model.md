# Data Model: Doctor Consultation Lifecycle

## Consultation

| Field | Description | Rules |
|---|---|---|
| `id` | Internal UUID consultation identifier | Immutable primary identifier. |
| `patientId` | Upstream patient reference | Required; validated through the check-in source contract. |
| `doctorId` | Assigned doctor reference | Required; only this doctor may start/complete. |
| `hospitalId` / `branchId` | Ownership scope | Required; must match trusted staff scope and source queue entry. |
| `sourceQueueEntryId` | Authoritative check-in queue-entry reference | Required and unique; direct creation is prohibited. |
| `state` | Lifecycle state | `WAITING`, `CALLED`, `IN_CONSULTATION`, `COMPLETED`. |
| `calledAt` | Call instant | Written only by `WAITING → CALLED`. |
| `actualStartAt` | Actual start instant | Written only by `CALLED → IN_CONSULTATION`. |
| `actualEndAt` | Actual end instant | Written only by `IN_CONSULTATION → COMPLETED`; not earlier than start. |
| `version` | Concurrency token | Incremented on every state mutation. |
| `createdAt` / `updatedAt` | Audit timestamps | Persisted instants. |

## Allowed State Transitions

| From | To | Authority | Required writes |
|---|---|---|---|
| `WAITING` | `CALLED` | Reception staff in trusted scope | State, `calledAt`, audit. |
| `CALLED` | `IN_CONSULTATION` | Assigned doctor in trusted scope | State, `actualStartAt`, audit. |
| `IN_CONSULTATION` | `COMPLETED` | Assigned doctor in trusted scope | State, `actualEndAt`, audit, reliable queue-recalculation hand-off. |

Every other transition is rejected without mutation. The source queue entry may create a consultation only once and is the only permitted origin of `WAITING`.

## Consultation Transition Audit

The existing audit store records action, target, trusted staff reference, hospital, timestamp, and PII-minimized metadata such as prior/next state and branch. It must not record patient names, medical content, phone numbers, or addresses.

## Queue Recalculation Outbox

When queue recalculation cannot share the consultation transaction, persist one idempotent request with consultation, doctor, hospital, branch, event type, occurred instant, delivery status, and retry metadata. A consumer invokes the queue contract and marks delivery only after acknowledged success.

## External Contract Relationships

- Check-in provides the authoritative source queue entry and its `WAITING` eligibility.
- Identity provides the trusted staff context and role/scope assertions.
- Queue owns recalculation and determines updated operational positions/estimates.
- Hospital/branch provides branch timezone for display; consultation stores instants only.
