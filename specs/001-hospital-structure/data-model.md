# Data Model: Hospital Structure Management

All IDs are UUIDs. Timestamps are `timestamptz` mapped to Java `Instant`. Duplicate-sensitive
display values are trimmed; canonical trimmed/lowercase values are used for uniqueness.

| Entity | Key fields and rules |
|---|---|
| Hospital | `id`, required `name`, timestamps; canonical name globally unique. |
| Branch | `id`, required immutable `hospital_id`, required name; canonical name unique per hospital. |
| Department | `id`, required immutable `branch_id`, required name; canonical name unique per branch. Hospital derives through branch. |
| Doctor | `id`, immutable `hospital_id`, required doctor code/name/specialization/registration number, `ACTIVE` or `INACTIVE`, timestamps; code unique per hospital and registration number globally unique. |
| Doctor Department Membership | Required `doctor_id` and `department_id`; pair unique; every department must derive to the doctor's hospital; at least one membership on registration. |
| Audit Event | `id`, occurred time, action, target type/id, hospital context, nullable actor until auth, minimal non-medical metadata. |

## Integrity and Lifecycle Rules

- A branch belongs to one hospital, a department to one branch, and a doctor to one hospital.
- Nested reads and writes verify the hospital ownership chain. Unrelated IDs return the same safe
  not-found response without disclosing the unrelated record.
- Foreign keys prevent dangling records; database unique constraints/indexes prevent canonical
  duplicates under concurrent requests.
- Doctor transitions are `ACTIVE -> INACTIVE`, `INACTIVE -> ACTIVE`, and idempotent same-state
  requests; a same-state request creates no status-change audit event. Directory reads include
  both states. Deletion is not implemented.
- `V1__create_hospital_structure.sql` owns these foundational tables, constraints, supporting
  ownership indexes, and audit-event persistence; later features add migrations rather than
  altering this feature's scope.
