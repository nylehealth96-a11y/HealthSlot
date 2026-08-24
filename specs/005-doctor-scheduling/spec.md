# Feature Specification: Doctor Scheduling

**Feature Branch**: `003-doctor-scheduling`
**Created**: 2026-08-24
**Status**: Draft
**Input**: User description: "Recurring doctor schedules; working days, hours, breaks, leave, exceptions, slot duration, and available-slot generation."

## Clarifications

### Session 2026-08-24

- Q: Should a date-specific schedule exception be able to define special working hours as well as mark a doctor unavailable? → A: An exception fully replaces that date’s working periods and breaks; an empty exception marks the day closed.
- Q: Which timezone should determine a doctor’s schedule and generated slots? → A: Use the doctor’s assigned branch timezone.
- Q: When staff update a recurring schedule, when should the change start affecting available slots? → A: Changes apply from a staff-selected effective date forward.
- Q: How should the system handle two staff members editing the same doctor’s schedule at the same time? → A: Reject a stale save with a clear conflict response and require the staff member to reload the latest schedule.
- Q: Which test-only roles should be permitted to access doctor scheduling? → A: SCHEDULING_MANAGER can read and manage schedules; SCHEDULING_VIEWER can read schedules and available slots only.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Configure Recurring Availability (Priority: P1)

Authorized staff configure a doctor's normal working days, hours, breaks, and slot duration so the system can determine the doctor's ordinary availability.

**Why this priority**: A recurring schedule is the baseline for all doctor availability.

**Independent Test**: Staff create a weekly schedule and the system produces the expected available slots on a normal working day.

**Acceptance Scenarios**:

1. **Given** a doctor belongs to a hospital, **When** staff configure working days, one or more working periods, breaks, and a slot duration, **Then** the schedule is saved for that doctor.
2. **Given** a working period contains a break, **When** available slots are generated, **Then** no slot overlaps the break.
3. **Given** invalid or overlapping working periods, breaks, or a slot duration that does not fit, **When** staff save the schedule, **Then** the system rejects it and identifies the conflict.
4. **Given** a doctor has a recurring schedule, **When** staff save a replacement with an effective start date, **Then** it applies from that date forward without altering prior schedule history.

---

### User Story 2 - Manage Leave and Exceptions (Priority: P2)

Authorized staff record doctor leave and date-specific schedule exceptions so ordinary recurring availability can be changed safely.

**Why this priority**: Leave and exceptions prevent staff from offering unavailable times.

**Independent Test**: Staff add leave or an exception for a configured doctor and verify the affected date’s available slots reflect it.

**Acceptance Scenarios**:

1. **Given** a doctor has a recurring schedule, **When** staff record leave for a date or date range, **Then** no slots are available during that leave.
2. **Given** a doctor has a recurring schedule, **When** staff record a date-specific exception, **Then** its working periods and breaks replace the ordinary schedule for that date; an exception with no periods marks the day closed.
3. **Given** leave or an exception conflicts with existing data, **When** staff save it, **Then** the system rejects invalid ranges and explains the conflict.

---

### User Story 3 - View Available Slots (Priority: P3)

Staff retrieve a doctor's available slots for a date range to support later appointment workflows.

**Why this priority**: Availability must be visible before downstream booking can safely use it.

**Independent Test**: For a normal date, a leave date, and an exception date, staff retrieve slots and receive the correct deterministic set.

**Acceptance Scenarios**:

1. **Given** a doctor has a valid schedule, **When** staff request a date range, **Then** the system returns slots within working periods that do not overlap breaks, leave, or exceptions.
2. **Given** a requested date is not a working day, **When** staff request slots, **Then** the system returns no slots for that date.

### Edge Cases

- Working periods that cross midnight are not allowed in this feature.
- A slot that would extend past a working period ends before the slot is created.
- A recurring schedule revision applies only on and after its effective start date; prior schedule history remains unchanged.
- A stale schedule update is rejected without overwriting the current revision.
- Overlapping leave ranges and overlapping exceptions are rejected.
- Exceptions take precedence over recurring schedule; leave takes precedence over both.
- All date and time evaluation uses the doctor's assigned branch timezone, never the server default.
- A schedule is scoped to the selected branch where the doctor practices; a doctor may have separate schedules at multiple branches.
- Local times that do not exist because of a daylight-saving transition are rejected; an ambiguous local time uses the earlier offset.
- Slots start at each working period's start time; any slot that overlaps a break is omitted rather than split.
- A request for available slots may cover at most 31 calendar days.
- Leave may coexist with an exception for the same date, but leave suppresses every slot and staff are informed of that precedence.
- The timezone migration assigns `UTC` to existing branches; invalid or missing timezone data blocks scheduling until corrected, and migration recovery proceeds only through forward-compatible corrective migrations.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Authorized staff MUST configure a doctor's recurring weekly working days and one or more working periods per day.
- **FR-002**: The system MUST store a positive slot duration for each recurring schedule and generate slots of that duration only within working periods.
- **FR-002a**: The system MUST apply each recurring schedule revision from its staff-selected effective start date forward and retain prior revisions for historical slot derivation.
- **FR-003**: Authorized staff MUST configure one or more breaks within a working period; generated slots MUST not overlap breaks.
- **FR-004**: The system MUST reject overlapping or invalid working periods, breaks, leave ranges, and schedule exceptions.
- **FR-005**: Authorized staff MUST record doctor leave for an inclusive date range; no slots may be generated during leave.
- **FR-006**: Authorized staff MUST create a date-specific schedule exception whose working periods and breaks fully replace the recurring schedule for that date; an exception with no working periods MUST mark the date closed.
- **FR-007**: The system MUST apply precedence in this order: leave, date-specific exception, recurring schedule.
- **FR-008**: The system MUST generate deterministic available slots for a doctor and requested date range, excluding all non-working time.
- **FR-009**: The system MUST scope schedule configuration and slot retrieval to the doctor’s hospital and authorized staff access.
- **FR-009a**: In the trusted test context, `SCHEDULING_MANAGER` MAY read and manage schedule rules and `SCHEDULING_VIEWER` MAY read schedules and available slots only; production access MUST fail closed until real authentication provides the same trusted scope.
- **FR-009b**: Actor identity, role, hospital, branch, doctor, and authorization scope MUST NOT be accepted from normal request headers, bodies, query parameters, or path values as proof of authorization.
- **FR-010**: The system MUST use explicitly modeled IANA timezone information from the selected schedule branch for all scheduling calculations; missing or invalid branch timezone data MUST prevent schedule operations.
- **FR-011**: The system MUST retain audit records for schedule, leave, and exception changes with the trusted staff reference, action, target, and change type, without schedule payloads or unnecessary personal information.
- **FR-012**: Appointment booking, reservations, and double-booking prevention are outside this feature.
- **FR-013**: The system MUST reject a schedule, leave, or exception change based on a stale revision and return a clear conflict response without overwriting the latest data.
- **FR-014**: The system MUST treat schedule, leave, and exception updates as full replacements, require their current version, and provide update and delete operations; duplicate effective dates or exception dates are rejected.
- **FR-015**: The system MUST reject local schedule times in a daylight-saving gap and use the earlier offset for an ambiguous local time.
- **FR-016**: The system MUST limit a slot query to 31 calendar days and return slots ordered by start instant, including both UTC instants and branch-local date/time values.
- **FR-017**: Inactive doctors MUST not receive new schedule rules and MUST return no available slots.

### Key Entities

- **Doctor Schedule**: An effective-dated, revisioned record of a doctor's recurring weekly working periods, breaks, slot duration, and selected schedule-branch timezone scope.
- **Schedule Break**: A non-working interval inside a working period.
- **Doctor Leave**: An inclusive date range during which a doctor has no availability.
- **Schedule Exception**: A date-specific replacement containing working periods and breaks; an exception with no working periods closes that date.
- **Available Slot**: A derived time interval eligible for later appointment booking; it is not a reservation.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In a scripted acceptance run of at least 20 valid schedule create-or-update attempts, at least 95% complete in under 5 minutes.
- **SC-002**: With 31 requested calendar days, two recurring revisions, five working days per week, two periods and two breaks per day, and ten leave/exception records, at least 95% of available-slot results return within 2 seconds.
- **SC-003**: In acceptance testing, 100% of slots comply with configured working periods, breaks, leave, exceptions, and precedence rules.
- **SC-004**: In acceptance testing, 100% of invalid overlapping schedule inputs are rejected without changing the previously valid schedule.
- **SC-005**: In acceptance testing, 100% of stale concurrent schedule updates are rejected without overwriting the latest revision.

## Assumptions

- Existing doctor, hospital, and branch structures provide ownership; a doctor may practice at more than one branch and scheduling explicitly selects one such branch.
- Authorized staff are identified through the project’s access-control mechanism.
- Slot duration is expressed in whole minutes and applies to all recurring periods in one doctor schedule.
- This feature derives availability only; appointment booking will later consume the generated slots.
