# Feature Specification: Appointment Check-In

**Feature Branch**: `005-check-in`
**Created**: 2026-08-24
**Status**: Draft
**Input**: User description: "Appointment check-in; reception check-in; walk-in registration; queue entry creation."

## Clarifications

### Session 2026-08-24

- Q: Until when may reception staff check in a booked appointment? → A: From the scheduled date's branch-local start until the appointment end time.
- Q: When two queue entries have the same recorded arrival time, how should their order be decided? → A: Order by arrival time, then queue reference.
- Q: Must a walk-in be accepted only when the doctor has an available scheduled slot at that branch? → A: Yes, require an available scheduled slot.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Check In an Appointment (Priority: P1)

Reception staff locate a booked appointment and record the patient's arrival, creating one queue entry for that visit.

**Why this priority**: It is the primary operational handoff from appointment booking to patient flow.

**Independent Test**: Staff check in a booked, in-scope appointment and receive its queue reference and `WAITING` queue-entry status; a repeat attempt does not create a second entry while appointment status remains `BOOKED`.

**Acceptance Scenarios**:

1. **Given** an in-scope `BOOKED` appointment that has not been checked in, **When** authorized reception staff check it in, **Then** its operational check-in is represented by exactly one linked queue entry while its appointment status remains `BOOKED`.
2. **Given** an appointment already has a queue entry, **When** staff repeat check-in, **Then** the system returns the existing entry without creating another one.
3. **Given** a cancelled, out-of-scope, or unknown appointment, **When** staff attempt check-in, **Then** the system does not create a queue entry or disclose an out-of-scope record.

---

### User Story 2 - Register a Walk-In Queue Entry (Priority: P2)

Reception staff register an existing patient as a walk-in for a doctor and branch, creating a queue entry without an appointment.

**Why this priority**: Walk-ins are a common reception workflow and must enter the same controlled queue.

**Independent Test**: Staff select an in-scope patient, doctor, and branch and create one walk-in queue entry with a queue reference.

**Acceptance Scenarios**:

1. **Given** an existing patient and an eligible doctor at an authorized branch, **When** staff register a walk-in, **Then** the system creates one queue entry marked as a walk-in.
2. **Given** invalid, ineligible, or out-of-scope patient, doctor, or branch data, **When** staff register a walk-in, **Then** no queue entry is created.

---

### User Story 3 - View Queue Entry Status (Priority: P3)

Reception staff retrieve a queue entry using its internal identifier or queue reference and see its current status and visit context.

**Why this priority**: Staff need a reliable reference after check-in or walk-in registration.

**Independent Test**: Staff retrieve in-scope appointment and walk-in entries; an out-of-scope entry is not disclosed.

### Edge Cases

- A check-in is valid only for a booked appointment from the branch-local start of its scheduled calendar date until its scheduled end time.
- Repeated check-in must be idempotent: it returns the existing queue entry and never creates a duplicate.
- A patient may not have more than one active queue entry for the same doctor and branch on the same branch-local calendar date.
- A walk-in cannot be created unless the doctor has an available scheduled slot at the branch.
- If appointment check-in and walk-in registration concurrently target the same patient, doctor, branch, and branch-local visit date, the transaction that first establishes the active queue entry wins; the other request receives a non-disclosing conflict and creates no entry.
- If availability changes before a walk-in transaction commits, availability is revalidated at the authoritative creation boundary; an unavailable request creates no entry and receives a conflict that may be retried after fresh availability is obtained.
- A queue reference collision retries reference allocation within the same transaction; any unrecoverable persistence failure rolls back the queue entry, appointment linkage, and audit record.
- If an appointment changes state, is cancelled, or reaches its end time while check-in is in flight, the authoritative appointment transition re-reads its current state and window before commit; it either commits the complete valid check-in or returns a non-disclosing conflict with no partial state.
- Queue references are globally unique, human-readable, immutable, and never reused.
- Queue order is determined by the persisted arrival/check-in time, then ascending immutable queue reference.
- All visit-date and queue-order calculations use the branch timezone.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Authorized reception staff MUST check in an in-scope `BOOKED` appointment only from the branch-local start of its scheduled calendar date until its scheduled end time, creating exactly one linked queue entry.
- **FR-002**: Appointment check-in MUST be idempotent and return the existing linked queue entry on a repeat request.
- **FR-003**: Authorized reception staff MUST create a walk-in queue entry for an existing in-scope patient, doctor, and branch only when the doctor has an available scheduled slot there, without creating an appointment.
- **FR-004**: Each queue entry MUST have an internal unique identifier and a human-readable globally unique queue reference.
- **FR-005**: The system MUST prevent duplicate active queue entries for the same patient, doctor, branch, and branch-local visit date, including concurrent requests.
- **FR-006**: Queue entries MUST retain whether they originated from appointment check-in or walk-in registration and, when applicable, their linked appointment.
- **FR-007**: Authorized staff MUST retrieve a queue entry by internal identifier and queue reference within hospital and branch scope.
- **FR-008**: The system MUST not disclose out-of-scope appointment, patient, or queue-entry information.
- **FR-009**: The system MUST use branch timezone for check-in eligibility and visit date, and MUST order queue entries by persisted arrival time then ascending immutable queue reference.
- **FR-010**: The system MUST retain PII-minimized audit records for appointment check-in and walk-in queue creation using trusted staff identity.
- **FR-011**: Production endpoints MUST fail closed until real staff authentication is integrated; request data MUST NOT establish actor, role, hospital, branch, or scope.
- **FR-012**: Appointment check-in is an appointment-module-owned operational transition from `BOOKED` with no linked queue entry to `BOOKED` with exactly one linked queue entry; it MUST NOT introduce a `CHECKED_IN` appointment status. `CANCELLED` appointments and any non-`BOOKED` state are invalid check-in transitions and receive the standard non-disclosing conflict response.
- **FR-013**: The appointment check-in state/link mutation, queue-entry creation, and mandatory audit record MUST commit in one transaction. If any part fails, all parts roll back and no partial check-in, active queue entry, or audit record is retained.
- **FR-014**: Walk-in availability MUST be revalidated by the authoritative scheduling capability in the same consistency boundary immediately before queue-entry commit. If it is unavailable, the transaction creates no queue entry or partial visit and returns the standard non-disclosing conflict response.
- **FR-015**: The system assigns `arrivedAt` from the trusted server clock at transaction start and persists it as an instant. `visitDate` is derived using the branch IANA timezone, including daylight-saving rules; queue order is `arrivedAt ASC`, then immutable `queueReference ASC`.
- **FR-016**: For an out-of-scope or unknown protected resource, the HTTP status, error code, response shape, audit metadata, and application logs MUST not reveal resource existence or patient information. A repeated appointment check-in after a client timeout is resolved by the existing scoped appointment link and returns the same queue entry.

### Key Entities

- **Queue Entry**: A patient visit waiting for a doctor at a branch, with a queue reference, source, visit date, arrival time, and status.
- **Queue Source**: `APPOINTMENT_CHECK_IN` or `WALK_IN`.
- **Queue Reference**: An immutable human-readable operational reference.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In a local PostgreSQL Testcontainers validation on a warmed JVM with a branch containing 100 patients, 10 eligible doctors, and 200 eligible booked appointments, measure each valid appointment check-in and each valid walk-in from application-service invocation through committed transaction completion, excluding fixture setup and first-run warm-up. For 100 sequential valid operations of each type, the 95th percentile MUST be under 2 seconds and no operation may exceed 5 seconds.
- **SC-002**: In 50 repeated local PostgreSQL Testcontainers races of two simultaneous duplicate check-ins, two duplicate walk-ins, and one check-in versus one walk-in for the same patient, doctor, branch, and local date, exactly one `WAITING` entry commits; repeated appointment check-in returns it and losing non-idempotent attempts receive `409`.
- **SC-003**: In the same warmed local PostgreSQL Testcontainers environment, measure scoped retrieval by UUID and queue reference from application-service invocation through response mapping, excluding fixture setup and first-run warm-up. Across 50 UUID and 50 queue-reference retrievals at 10 concurrent callers against at least 200 queue entries, the 95th percentile MUST be under 2 seconds and no operation may exceed 5 seconds.
- **SC-004**: In acceptance testing, 100% of unknown and out-of-scope protected-resource requests have the same `404` status, error code, response shape, PII-free log fields, and opaque audit metadata, revealing no visit, patient, appointment, or resource-existence information.

## Assumptions

- Patient, doctor, branch timezone, and appointment data are provided by preceding features.
- The initial and only queue status in this feature is `WAITING`; booking, triage, consultation, no-show, billing, and queue calling are outside this feature. “Checked in” means the appointment's linked queue-entry operational state, not a new appointment status.
- A reception permission and trusted test-only staff identity are available for automated tests; production remains fail-closed.
