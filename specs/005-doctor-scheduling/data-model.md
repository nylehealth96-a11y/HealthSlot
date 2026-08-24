# Data Model: Doctor Scheduling

All primary keys are UUIDs. Times use the associated branch's IANA timezone; timestamps are UTC instants.

## Existing changes

`branches.timezone` — required IANA zone identifier. The migration gives existing rows an explicit safe default (`UTC`) and branch creation/update contracts must ultimately provide a valid IANA identifier.

## New entities

| Entity | Core fields | Rules / relationships |
|---|---|---|
| DoctorScheduleRevision | `id`, `doctor_id`, `branch_id`, `effective_from`, `slot_duration_minutes`, `version`, timestamps | Unique `(doctor_id, branch_id, effective_from)`. Doctor must have a department in branch. Latest effective revision wins. |
| WorkingPeriod | `id`, `schedule_revision_id`, `day_of_week`, `start_time`, `end_time` | `start < end`; same-day only; periods for one day cannot overlap. |
| ScheduleBreak | `id`, `working_period_id`, `start_time`, `end_time` | Must lie wholly within parent period; no overlap with another break in that period. |
| DoctorLeave | `id`, `doctor_id`, `branch_id`, `start_date`, `end_date`, `version`, timestamps | Inclusive range; `start_date <= end_date`; no overlapping ranges for doctor/branch. |
| ScheduleException | `id`, `doctor_id`, `branch_id`, `exception_date`, `version`, timestamps | Unique doctor/branch/date. Child periods and breaks replace recurring rules; no periods means closed. |
| ExceptionWorkingPeriod / ExceptionBreak | Same shapes as recurring period/break, linked to exception | Same containment and no-overlap validation. |
| AvailableSlot | derived only: `start`, `end`, `branch_timezone`, source type | Not persisted and not a reservation. |

## Relationships and projection

`Doctor -> Department -> Branch` verifies that a doctor may be scheduled at the requested branch. Each schedule rule is explicitly branch-scoped. For each local date: if leave covers date return none; otherwise use exception if present; otherwise select the effective recurring revision; generate duration-aligned periods after subtracting breaks. The final partial interval is omitted.

## Validation and concurrency

- Slot duration is a positive whole number of minutes.
- All rule changes require the resource version where a mutable resource already exists; stale versions return `STALE_REVISION` and preserve the current data.
- Database constraints protect keys, dates, foreign keys, and basic ranges. Application/domain validation protects interval containment/overlap and branch membership in a transaction.
- Scheduling only covers active doctors; inactive doctors return no available slots and cannot receive new schedule rules.
