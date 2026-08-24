# Data Model: Appointment Booking

## Appointment

| Field | Rule |
|---|---|
| id | UUID primary key; immutable internal identifier |
| appointment_number | Required, immutable, globally unique `APT-YYYY-########` reference |
| hospital_id | Required; ownership/scope and audit reference |
| branch_id | Required; belongs to hospital and supplies timezone |
| doctor_id | Required; must be eligible at the branch through scheduling |
| patient_id | Required; must belong to the hospital through the patient contract |
| start_at / end_at | Required UTC instants; `end_at` must be after `start_at` and exactly match a derived available slot |
| status | Required enum: `BOOKED` or `CANCELLED`; initial state is `BOOKED` |
| version | Required optimistic-lock version; cancel/reschedule require the current value |
| created_at / updated_at | Required audit timestamps |

`BOOKED` to `CANCELLED` is allowed only before `start_at`. A `CANCELLED` appointment cannot be cancelled or rescheduled. Rescheduling preserves the ID and number, requires a different interval, and changes the booked interval only in one successful transaction.

## Appointment audit event

The append-only `audit_events` store records `APPOINTMENT_BOOKED`, `APPOINTMENT_CANCELLED`, and `APPOINTMENT_RESCHEDULED` with appointment target, hospital, trusted staff actor reference, and PII-minimized metadata. Optional cancellation/reschedule reason and scheduling identifiers may be included; patient names, contact data, and clinical data may not. Audit persistence is transactional with the appointment change.

## Derived available slot

| Field | Rule |
|---|---|
| doctor_id / branch_id | Identifies the schedule context |
| start_at / end_at | UTC instants derived from branch-local schedule rules |
| branch_timezone | IANA timezone used for conversion and local-date range evaluation |

The scheduling feature creates candidates from working hours, breaks, leave, and exceptions. Appointment availability excludes all overlapping `BOOKED` reservations. Requests use inclusive branch-local dates, cover at most 31 days, and retain these rules through daylight-saving changes.

## Persistence invariants

- Foreign keys reference hospital, branch, doctor, and patient records; ownership is validated through application contracts before the appointment is persisted.
- `start_at < end_at`; status is limited to the two documented values; appointment numbers conform to `APT-[0-9]{4}-[0-9]{8}`.
- A PostgreSQL partial exclusion constraint prevents overlapping `BOOKED` intervals for the same doctor and branch: `EXCLUDE USING gist (doctor_id WITH =, branch_id WITH =, tstzrange(start_at, end_at, '[)') WITH &&) WHERE (status = 'BOOKED')`.
- Supporting indexes cover active doctor/branch time-range filtering, branch lookup, patient lookup, and appointment-number lookup.
- `@Version`/versioned updates detect stale cancellation and rescheduling. The exclusion constraint remains the final concurrent-booking authority.

## Trusted staff identity port

`StaffAccessContext` supplies trusted `staffId`, the `APPOINTMENT_SCHEDULER` permission, and authorized hospital/branch scope. It authorizes against server-resolved resource ownership. It is not an HTTP payload or request-derived claim. The production adapter returns an authentication/authorization failure until a real integration exists; `TrustedTestStaffAccessContext` exists only under test sources.
