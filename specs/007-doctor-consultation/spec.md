# Feature Specification: Doctor Consultation Lifecycle

**Feature Branch**: `007-doctor-consultation`
**Created**: 2026-08-24
**Status**: Draft
**Input**: "CALL PATIENT → START CONSULTATION → IN_CONSULTATION → COMPLETE → actual start/end saved → queue recalculated."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Call the Next Patient (Priority: P1)

An authorized reception staff member calls a waiting patient for a doctor, making that patient's consultation the current next consultation without exposing other branches' queue information.

**Why this priority**: Calling a patient is the first operational step that moves a waiting visit into a consultation.

**Independent Test**: An eligible waiting consultation is called once; the resulting state is visible only within the staff member's authorized scope.

**Acceptance Scenarios**:

1. **Given** an eligible waiting consultation in the receptionist's authorized doctor, hospital, and branch scope, **When** the receptionist calls the patient, **Then** the consultation transitions to `CALLED` and its queue position is preserved until consultation starts or is otherwise resolved.
2. **Given** an absent, out-of-scope, or non-waiting consultation, **When** staff attempt to call it, **Then** no state changes and no patient or queue information is disclosed.
3. **Given** concurrent attempts to call the same waiting consultation, **When** they are processed, **Then** exactly one succeeds and the other receives the standard conflict response.

---

### User Story 2 - Start and Track a Consultation (Priority: P1)

The assigned doctor starts a called consultation, and the system records the actual start instant as the consultation becomes active.

**Why this priority**: Clinicians need a reliable, auditable indication that a patient is currently being seen.

**Independent Test**: A called consultation can transition to `IN_CONSULTATION` exactly once and retains its recorded actual start instant.

**Acceptance Scenarios**:

1. **Given** a called consultation for the assigned doctor, **When** that doctor starts it, **Then** it transitions to `IN_CONSULTATION` and the actual start instant is recorded.
2. **Given** a consultation that is not `CALLED`, **When** a start is attempted, **Then** the system rejects the invalid transition without changing timestamps or queue state.
3. **Given** concurrent start attempts, **When** they are processed, **Then** exactly one transition and one actual start instant are persisted.

---

### User Story 3 - Complete a Consultation and Recalculate the Queue (Priority: P2)

The assigned doctor completes an active consultation, recording the actual end instant and causing the affected waiting queue to recalculate from persisted information.

**Why this priority**: Completion closes the active clinical visit and keeps the next patients' operational queue accurate.

**Independent Test**: Completing an in-consultation visit records its end instant once and produces a recalculated queue that no longer includes the completed visit.

**Acceptance Scenarios**:

1. **Given** an in-consultation visit, **When** the assigned doctor completes it, **Then** it transitions to `COMPLETED`, records an actual end instant after or equal to its actual start instant, and the affected queue is recalculated.
2. **Given** a consultation not in `IN_CONSULTATION`, **When** completion is attempted, **Then** the system rejects the invalid transition without partial completion or queue changes.
3. **Given** a queue recalculation cannot be completed, **When** completion is attempted, **Then** the consultation completion, audit record, and queue update are all rolled back or reliably recover according to the established queue integration contract.

## Edge Cases

- A consultation cannot be called, started, or completed more than once; concurrent requests must not create contradictory state or timestamps.
- Actual start and end are persisted as instants; displayed operational times use the authorized branch timezone.
- A completed consultation must no longer occupy an active waiting or in-consultation slot.
- Out-of-scope and unknown consultation identifiers produce the same non-disclosing response.
- A caller cannot supply staff identity, role, hospital, branch, or authorization scope in normal request input.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST maintain the consultation lifecycle `WAITING` → `CALLED` → `IN_CONSULTATION` → `COMPLETED` and reject every other transition with the standard invalid-state response.
- **FR-002**: The system MUST allow an authorized reception staff member to call an eligible waiting consultation exactly once and MUST protect the transition against concurrent duplicate calls.
- **FR-003**: The system MUST allow only the consultation's assigned doctor to start a called consultation exactly once, transition it to `IN_CONSULTATION`, and persist its actual start instant atomically with that transition.
- **FR-004**: The system MUST allow only the consultation's assigned doctor to complete an in-consultation visit exactly once, transition it to `COMPLETED`, and persist its actual end instant atomically with that transition.
- **FR-005**: The system MUST ensure actual end is not earlier than actual start and MUST preserve timestamps as instants; user-facing operational time uses the relevant branch timezone.
- **FR-006**: The system MUST recalculate the affected doctor and branch waiting queue after a successful completion, using the established queue capability, without leaving a partial consultation completion or queue update.
- **FR-007**: Consultation lifecycle actions MUST enforce trusted server-side staff identity and hospital, branch, doctor, and role scope; production endpoints MUST fail closed until real authentication is integrated.
- **FR-008**: Lifecycle actions MUST never trust actor, staff, hospital, branch, role, or authorization scope supplied in normal request input.
- **FR-009**: Calling, starting, and completing consultations MUST create PII-minimized audit records with trusted staff identity and the resulting state transition.
- **FR-010**: Each consultation MUST have an internal UUID identifier and be associated with exactly one patient, doctor, hospital, branch, and source queue/appointment context.
- **FR-011**: Consultation state mutation, timestamp persistence, required audit persistence, and required queue recalculation MUST use one documented consistency boundary so partial success is not visible.
- **FR-012**: Only trusted reception staff in scope may call a patient; only the assigned doctor in scope may start or complete that patient's consultation.
- **FR-013**: `WAITING` consultations MUST originate only from the authoritative check-in queue-entry workflow; this feature MUST reject direct creation or any other source to prevent bypassing check-in and duplicate active consultations.

### Key Entities

- **Consultation**: A patient visit managed through the waiting, called, in-consultation, and completed lifecycle, with internal identity and actual timing.
- **Consultation State Transition**: An auditable, authorized change from one permitted lifecycle state to the next.
- **Actual Consultation Timing**: The persisted start and end instants of the clinical consultation.
- **Queue Recalculation Request**: The scoped, reliable request to refresh the affected waiting queue after a completed consultation.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In lifecycle acceptance testing, 100% of valid call, start, and complete requests produce exactly the permitted next state and required audit record.
- **SC-002**: In concurrent lifecycle acceptance testing, 100% of duplicate call, start, or complete attempts result in one persisted transition and no contradictory timestamps.
- **SC-003**: In acceptance testing, 100% of completed consultations have persisted actual start and end instants with end not earlier than start and no longer appear as active queue entries.
- **SC-004**: In authorization acceptance testing, 100% of out-of-scope lifecycle requests disclose no consultation, patient, doctor, or queue information.
- **SC-005**: In failure-injection acceptance testing, 100% of failed required queue recalculations leave no partial consultation completion, timestamp, or required audit record visible.

## Assumptions

- The check-in queue-entry workflow is the sole authoritative source of `WAITING` consultations, and the queue feature provides recalculation capabilities; this feature does not duplicate either capability.
- Patient, appointment, doctor, hospital, branch, audit, and trusted-identity capabilities are provided by upstream modules through intentional contracts.
- Cancellation, no-show handling, clinical notes, diagnoses, prescriptions, billing, and notifications are outside this feature.
- For local and automated testing only, a clearly named trusted test staff context may supply server-side identity and scope; it must not be usable as production authentication.
