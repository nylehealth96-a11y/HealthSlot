# Feature Specification: Appointment Booking

**Feature Branch**: `004-appointment-booking`
**Created**: 2026-08-24
**Status**: Draft
**Input**: User description: "View available slots; book appointment; cancel; reschedule; appointment status; appointment number; concurrency protection; prevent double booking."

## Clarifications

### Session 2026-08-24

- Q: Until when may authorized staff cancel or reschedule a booked appointment? → A: Only before the appointment start time.
- Q: Must staff supply the appointment’s current version when cancelling or rescheduling it? → A: Yes, for both cancellation and rescheduling.
- Q: Should staff record a reason whenever they cancel or reschedule an appointment? → A: An operational reason is optional for both actions.
- Q: What is the maximum date range staff may request when viewing a doctor’s available slots? → A: Up to 31 calendar days per request.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Book an Available Slot (Priority: P1)

Authorized staff view a doctor's available slots and book one for a patient, receiving an appointment number for operational use.

**Why this priority**: Booking a valid slot is the core outcome of appointment management.

**Independent Test**: Staff select an available slot and create an appointment; a second concurrent attempt for the same doctor, branch, and time is rejected.

**Acceptance Scenarios**:

1. **Given** a patient and doctor have a currently available slot, **When** authorized staff book it, **Then** the system creates an appointment with an internal identifier, a human-readable appointment number, and status `BOOKED`.
2. **Given** two staff members try to book the same slot at the same time, **When** both requests are processed, **Then** exactly one appointment is created and the other receives a conflict response.
3. **Given** a requested time is outside the doctor's available slots, **When** staff try to book it, **Then** the system rejects the request without creating an appointment.

---

### User Story 2 - Cancel or Reschedule an Appointment (Priority: P2)

Authorized staff cancel an appointment or move it to another available slot while preserving a reliable appointment history.

**Why this priority**: Operational changes must release capacity and avoid accidental duplicate bookings.

**Independent Test**: Staff cancel a booked appointment and confirm its slot becomes available; staff reschedule another appointment and confirm only the replacement slot remains occupied.

**Acceptance Scenarios**:

1. **Given** a booked appointment, **When** staff cancel it, **Then** its status becomes `CANCELLED` and its former slot is available again.
2. **Given** a booked appointment and another available slot, **When** staff reschedule it, **Then** the same appointment number is retained, its status remains `BOOKED`, and the original slot is released.
3. **Given** staff try to cancel or reschedule a cancelled appointment, **When** they submit the change, **Then** the system rejects it with a clear state-conflict response.

---

### User Story 3 - View Appointment Details and Status (Priority: P3)

Authorized staff retrieve an appointment by internal identifier or appointment number and view its current status and scheduling details.

**Why this priority**: Staff need a dependable operational reference after booking or changing appointments.

**Independent Test**: Staff retrieve a booked, cancelled, and rescheduled appointment and receive the correct current details without seeing another hospital's appointment.

**Acceptance Scenarios**:

1. **Given** an appointment belongs to the staff member's authorized hospital and branch, **When** staff retrieve it, **Then** they receive its appointment number, patient, doctor, branch, scheduled time, and status.
2. **Given** an appointment is outside the authorized scope, **When** staff retrieve it, **Then** the system does not disclose its details.

### Edge Cases

- An appointment cannot be booked in the past or for a slot that has ended.
- A booking request that spans a different duration from the selected available slot is rejected.
- Cancellation and rescheduling require the appointment's current version; stale changes are rejected with a conflict response.
- A booked appointment cannot be cancelled or rescheduled at or after its start time.
- A reschedule must atomically release the original slot and reserve the replacement slot; if the replacement is unavailable, the original appointment remains unchanged.
- A reschedule request for the appointment's unchanged interval is an invalid change and is rejected without changing its version or audit history.
- Availability is advisory at the time it is viewed; a concurrent booking or schedule change may make a previously returned slot unavailable, and the later booking must receive a conflict response.
- When two changes use the same current appointment version, at most one may succeed; every later change receives a stale-version conflict response.
- Cancellation and rescheduling may include an optional operational reason; when provided, it is retained in the associated audit record.
- Appointment numbers are unique, human-readable, and never reused after cancellation.
- All slot and appointment-time evaluation uses the associated branch timezone, never the server default.
- An available-slot request uses inclusive branch-local calendar dates; `from` must not be after `to`, and the inclusive range cannot cover more than 31 days, including daylight-saving transitions.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST return a doctor's available slots for inclusive branch-local `from` and `to` dates spanning no more than 31 calendar days, excluding every overlapping `BOOKED` appointment.
- **FR-002**: Authorized staff MUST book a patient into one returned available slot.
- **FR-003**: Each appointment MUST have an internal unique identifier and an immutable, globally unique, human-readable appointment number in the `APT-YYYY-########` form; numbers are never reused.
- **FR-004**: A newly booked appointment MUST have status `BOOKED`.
- **FR-005**: The system MUST prevent overlapping active appointments for the same doctor and branch, including under concurrent requests; adjacent non-overlapping intervals remain permitted.
- **FR-006**: The system MUST reject booking requests for unavailable, past, invalid-duration, or unauthorized slots without creating an appointment.
- **FR-007**: Authorized staff MUST cancel a `BOOKED` appointment; cancellation MUST set status `CANCELLED` and release its slot.
- **FR-008**: Authorized staff MUST reschedule a `BOOKED` appointment to a different available slot while retaining its appointment number and recording the reschedule change.
- **FR-009**: The system MUST atomically preserve the original appointment when a reschedule cannot reserve the replacement slot.
- **FR-010**: Authorized staff MUST retrieve appointments by internal identifier and appointment number within their hospital and branch scope.
- **FR-011**: The system MUST validate appointment state transitions, require the current appointment version for cancellation and rescheduling, allow those changes only before the appointment start time, and reject stale, unchanged-interval, or invalid changes with a conflict response.
- **FR-012**: The system MUST use the appointment branch's explicitly modeled timezone for all availability and appointment-time calculations.
- **FR-013**: The system MUST retain PII-minimized audit records for booking, cancellation, and reschedule actions using the trusted staff identity and any optional operational reason supplied for cancellation or rescheduling; audit persistence is part of the same transaction, and audit/error output MUST not disclose patient PII.
- **FR-014**: Production appointment endpoints MUST fail closed until a real authentication/staff-identity integration exists; normal request input MUST NOT establish actor, role, hospital, branch, or authorization scope.
- **FR-015**: Appointment actions require the `APPOINTMENT_SCHEDULER` permission. An authenticated identity without that permission receives `403`; a missing or out-of-scope hospital, branch, doctor, patient, or appointment receives non-disclosing `404`.
- **FR-016**: The system MUST reject a booking or reschedule without creating or changing an appointment when the patient, doctor, branch, or scheduling prerequisite is missing, inconsistent, or not eligible for the requested slot.
- **FR-017**: The system MUST emit PII-minimized operational signals for booking conflicts, stale changes, and fail-closed identity failures sufficient to detect and investigate those conditions.

### Key Entities

- **Appointment**: A patient reservation of a doctor and branch slot, with internal ID, appointment number, start/end time, status, and version.
- **Appointment Status**: `BOOKED` or `CANCELLED`; only valid, documented transitions are permitted.
- **Available Slot**: A derived doctor/branch interval eligible for booking after schedule rules and active appointments are considered.
- **Appointment Number**: An immutable globally unique `APT-YYYY-########` operational reference.
- **Appointment Permission**: `APPOINTMENT_SCHEDULER`, the sole permission required for availability, booking, retrieval, cancellation, and rescheduling; hospital/branch scope is separately required.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Under a 25-concurrent-request acceptance workload with valid prerequisite data, at least 95% of valid booking attempts complete in under 30 seconds from slot selection to confirmation.
- **SC-002**: In concurrent acceptance testing of the same slot, 100% of runs create exactly one active appointment.
- **SC-003**: Under a 25-concurrent-request acceptance workload with a 31-day range and valid prerequisite data, at least 95% of appointment retrieval and available-slot requests return within 2 seconds.
- **SC-004**: In acceptance testing, 100% of cancellation and failed-reschedule cases preserve correct slot availability and appointment status.
- **SC-005**: In acceptance testing, 100% of appointments outside the caller's authorized hospital or branch scope are not disclosed.

## Assumptions

- Patient, doctor, branch, and doctor-schedule data are available from preceding features.
- A patient may have multiple appointments; duplicate appointments at different slots are allowed.
- Appointment booking reserves the complete returned slot duration.
- Initial scope does not include reminders, payments, check-in, queues, waitlists, or recurring appointments.
- Test-only trusted identity provides scheduling/booking role and scope for automated tests; production remains fail-closed until real authentication exists.
- Patient, doctor, branch, and scheduling prerequisites are independently owned; the appointment module consumes their application contracts and rejects missing, inconsistent, or ineligible data without exposing its existence outside trusted scope.
