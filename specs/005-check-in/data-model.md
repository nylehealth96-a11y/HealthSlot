# Data Model: Appointment Check-In

## QueueEntry

| Field | Type | Rules |
|---|---|---|
| `id` | UUID | Immutable primary key. |
| `queueReference` | String | Required, immutable, globally unique, human-readable, never reused. |
| `hospitalId`, `branchId` | UUID | Required; branch must belong to hospital and determines timezone. |
| `doctorId`, `patientId` | UUID | Required; must exist and be in trusted scope. |
| `appointmentId` | UUID nullable | Required only for `APPOINTMENT_CHECK_IN`; unique when non-null. |
| `source` | Enum | `APPOINTMENT_CHECK_IN` or `WALK_IN`; immutable. |
| `visitDate` | LocalDate | Required; derived from arrival instant in branch timezone. |
| `arrivedAt` | Instant | Required trusted-server-clock instant assigned at transaction start; not client supplied. |
| `status` | Enum | Initially and currently only `WAITING`. |
| `createdAt`, `updatedAt` | Instant | Required audit timestamps. |
| `version` | Integer | Optional JPA optimistic lock; does not replace DB constraints. |

### Relationships and constraints

- One entry belongs to one hospital, branch, doctor, and patient.
- Appointment source has exactly one appointment; walk-in source has none.
- Unique: `queue_reference`; unique non-null `appointment_id`; partial unique `(patient_id, doctor_id, branch_id, visit_date)` where `status = 'WAITING'`.
- Scoped retrieval indexes: `(hospital_id, branch_id, id)` and `(hospital_id, branch_id, queue_reference)`.
- Future queue ordering index: `(branch_id, doctor_id, visit_date, arrived_at, queue_reference)`.
- Check constraint: appointment ID is non-null iff source is appointment check-in.

## Enums

| Enum | Values |
|---|---|
| `QueueSource` | `APPOINTMENT_CHECK_IN`, `WALK_IN` |
| `QueueStatus` | `WAITING` |

## Audit events and invariants

Use the existing audit-store pattern with `APPOINTMENT_CHECKED_IN` and `WALK_IN_QUEUE_ENTRY_CREATED`. Store staff UUID, opaque queue/appointment/scope IDs, source, and instant; exclude names, contacts, and clinical content.

- Check-in requires scoped `BOOKED` appointment in the branch-local window.
- An appointment check-in changes appointment operational linkage from no queue entry to exactly one linked queue entry while status remains `BOOKED`; `CANCELLED` and every non-`BOOKED` state are invalid. Appointment linkage, queue row, and audit row are one transaction and roll back together.
- Exactly one active `WAITING` entry per patient, doctor, branch, and local date.
- Concurrent appointment and walk-in attempts use the active-visit constraint: the first committed entry wins and the other transaction rolls back with a non-disclosing conflict.
- Walk-in availability is revalidated by its scheduling owner immediately before commit; a changed/unavailable result rolls back with no queue or audit row.
- Retrieval scopes first and does not disclose out-of-scope existence.
- Queue ordering is `arrivedAt ASC`, then `queueReference ASC`.
