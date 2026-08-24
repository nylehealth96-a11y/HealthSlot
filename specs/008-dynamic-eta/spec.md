# Feature Specification: Dynamic Doctor ETA

**Feature Branch**: `008-dynamic-ETA`
**Created**: 2026-08-24
**Status**: Draft
**Input**: "Doctor running late → calculate current delay → recalculate upcoming patients → new predicted times. Example: 10:00 Rahul, 10:15 Amit, 10:30 Priya; Rahul takes 25 minutes; Amit ETA = 10:25 and Priya ETA = 10:40."

## Clarifications

### Session 2026-08-24

- Q: While a consultation is still active and exceeds its expected end time, should upcoming ETAs continue to refresh as elapsed time increases, or only change when actual start/end events are recorded? → A: Refresh at defined intervals while an active consultation exceeds its expected end.
- Q: How often should the system refresh ETAs while an active consultation is running beyond its expected end time? → A: Every 1 minute.
- Q: What prediction change should count as “material” and require an ETA audit record? → A: Audit the first delay-caused version and any prediction shift of 5+ minutes.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Updated Doctor ETAs (Priority: P1)

Authorized staff view the current predicted start time for each upcoming appointment for a doctor after the doctor is running late.

**Why this priority**: Reception and clinical staff need an accurate operational view to manage patient expectations.

**Independent Test**: Given a doctor whose 10:00 consultation ends at 10:25 instead of its scheduled 10:15 end, staff see 10:25 for the 10:15 appointment and 10:40 for the 10:30 appointment when each scheduled slot is 15 minutes.

**Acceptance Scenarios**:

1. **Given** a doctor's current consultation ends 10 minutes after its scheduled end, **When** authorized staff view the remaining same-day appointments, **Then** each affected upcoming appointment has a recalculated predicted start based on the current delay and its scheduled ordering.
2. **Given** an upcoming appointment is scheduled after enough idle time to absorb an earlier delay, **When** staff view its prediction, **Then** its predicted start does not move earlier than its scheduled start.
3. **Given** no delay exists for a doctor, **When** staff view upcoming predictions, **Then** predicted starts match scheduled starts unless another persisted operational constraint applies.

---

### User Story 2 - Recalculate After Consultation Timing Changes (Priority: P1)

The system recalculates a doctor's upcoming predictions whenever authoritative actual consultation timing establishes or changes the current delay.

**Why this priority**: Predictions must follow real consultation progress rather than stale schedule assumptions.

**Independent Test**: Recording a later actual start or actual end for the current consultation produces one new deterministic prediction version for affected upcoming appointments.

**Acceptance Scenarios**:

1. **Given** an active consultation starts after its scheduled start, **When** its actual start is recorded or it later exceeds its expected end, **Then** the system recalculates affected upcoming predictions using the authoritative delay at that instant and once per minute during the overrun.
2. **Given** an active consultation completes later than its expected end, **When** its actual end is recorded, **Then** the system recalculates affected upcoming predictions using the actual completion time.
3. **Given** concurrent timing updates or ETA reads, **When** they are processed, **Then** readers receive one deterministic published prediction version and no contradictory predicted times.

---

### User Story 3 - Protect ETA Data and Audit Changes (Priority: P2)

Authorized staff can access operational ETA information only within their hospital and branch scope, and meaningful prediction changes are auditable without exposing unnecessary patient information.

**Why this priority**: ETA data derives from patient appointments and consultation timing, so its operational usefulness must not compromise privacy.

**Independent Test**: An out-of-scope request receives no appointment, doctor, patient, or ETA information, while an in-scope recalculation produces a PII-minimized audit record.

**Acceptance Scenarios**:

1. **Given** an in-scope doctor and branch, **When** authorized staff retrieve upcoming ETAs, **Then** they receive only the operational fields permitted by their scope.
2. **Given** an absent or out-of-scope doctor, branch, or appointment, **When** staff request ETA information, **Then** the system returns the same non-disclosing response without changing predictions.
3. **Given** a prediction changes materially because of authoritative consultation timing, **When** recalculation completes, **Then** an audit record captures the trigger and affected scope without patient names or clinical content.

## Edge Cases

- A delay may be absorbed by a later scheduled gap; predictions never move earlier than scheduled starts.
- Only same-day, upcoming appointments for the affected doctor and branch are recalculated; completed, cancelled, and out-of-scope appointments are excluded.
- Predictions use the branch timezone for service-day and display calculations while persisted schedule, actual, and prediction times remain instants.
- A missing configured slot duration or unavailable upstream appointment/consultation timing prevents a prediction from being fabricated; staff receive the standard unavailable/conflict outcome without patient details.
- Concurrent recalculation triggers publish one version whose predictions are reproducible from the same persisted inputs.
- A missed one-minute refresh is recovered by the next trigger using the latest authoritative consultation timing; duplicate triggers for the same doctor, branch, minute, and timing revision are idempotent.
- A recalculation based on a stale consultation-timing revision is discarded and retried from the latest authoritative revision; stale results are never published.
- Recovery is owned by the ETA service. It retries an unpublished prediction/audit transaction idempotently; after the documented retry limit it records an operational failure without exposing patient information.
- Normal request input cannot supply actor, staff, hospital, branch, role, authorization scope, actual consultation timing, or a manual delay override.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST calculate a doctor's current delay from authoritative scheduled and actual consultation timing for that doctor and branch, where expected end is scheduled start plus configured slot duration; a negative delay is treated as zero.
- **FR-002**: The system MUST recalculate predicted starts for all affected upcoming same-day appointments in deterministic scheduled order whenever authoritative actual start or actual end timing changes the current delay and once per minute while an active consultation exceeds its expected end.
- **FR-003**: Each predicted start MUST be the later of its scheduled start and the prior affected appointment's predicted completion, using that appointment's configured slot duration; predictions must never move earlier than scheduled starts.
- **FR-004**: The system MUST use actual completion timing for a completed current consultation and server-clock elapsed actual timing for an active current consultation when calculating the current delay; it MUST not trust a caller-supplied elapsed duration.
- **FR-005**: The system MUST exclude completed, cancelled, and out-of-scope appointments from ETA recalculation and MUST not change their historical timing.
- **FR-006**: The system MUST publish one deterministic ETA prediction version per affected doctor, branch, and branch-local service date; concurrent timing changes and reads must not expose contradictory predicted times.
- **FR-007**: Authorized staff MUST retrieve in-scope upcoming appointment ETAs with operational identifiers, scheduled start, predicted start, current doctor delay, and prediction version, without patient contact or clinical information.
- **FR-008**: ETA retrieval and recalculation MUST enforce trusted server-side staff identity and hospital/branch scope, fail closed in production until real authentication exists, and return non-disclosing results for unknown or out-of-scope resources.
- **FR-009**: The system MUST not trust actor, staff, hospital, branch, role, authorization scope, actual consultation timing, or manual delay values supplied in normal request input.
- **FR-010**: The first delay-caused ETA prediction version and any later version that shifts an affected predicted start by five minutes or more MUST create a PII-minimized audit record with trusted staff/system trigger identity, doctor/branch scope, prior and new prediction version, and no patient names or clinical content.
- **FR-011**: ETA prediction persistence, required audit persistence, and publication of a prediction version MUST share one documented consistency boundary so partial prediction results are not visible.
- **FR-012**: The system MUST return the standard unavailable/conflict outcome without patient information when authoritative slot duration, appointment ordering, consultation timing, or branch timezone is unavailable or inconsistent.
- **FR-013**: The system MUST identify each one-minute refresh by doctor, branch, branch-local minute, and authoritative timing revision; missed triggers use the next refresh with latest inputs, and duplicate triggers must not publish duplicate versions.
- **FR-014**: The system MUST reject stale recalculation inputs and retry from the latest authoritative timing revision before publication; a stale result must never replace a newer prediction version.
- **FR-015**: The ETA service MUST own idempotent recovery of failed unpublished prediction/audit transactions, record terminal operational failure after a documented retry limit, and disclose no patient information through recovery status.

### Key Entities

- **Doctor Delay**: Non-negative duration between the authoritative expected and actual/elapsed timing of the doctor's current consultation.
- **ETA Prediction Version**: Immutable, scoped publication of deterministic predicted starts for a doctor, branch, and local service date.
- **Upcoming Appointment ETA**: Operational appointment identifier with scheduled start and predicted start; it contains no patient contact or clinical information.
- **ETA Recalculation Trigger**: The authoritative consultation timing event that causes an ETA prediction version to be recalculated.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In acceptance testing of the example schedule (10:00, 10:15, and 10:30 with 15-minute slots), a 10:25 actual completion for the 10:00 consultation produces 10:25 and 10:40 predicted starts for the two upcoming appointments.
- **SC-002**: In concurrent timing-update acceptance testing, 100% of retrieved ETA views contain exactly one internally consistent prediction version and no prediction earlier than its scheduled start.
- **SC-003**: In authorization acceptance testing, 100% of out-of-scope ETA requests disclose no patient, appointment, doctor, or timing information.
- **SC-004**: In failure-injection acceptance testing, 100% of failed required audit or prediction persistence attempts leave no partial ETA prediction version visible.
- **SC-005**: In acceptance testing, 100% of affected appointments are recalculated after authoritative actual-start or actual-end timing changes and at each one-minute active-overrun refresh interval, and unaffected appointments retain their existing scheduled timing.
- **SC-006**: In local integration validation with 50 upcoming appointments for one doctor, a warmed-up ETA recalculation completes within 2 seconds at the 95th percentile across 20 sequential recalculations, excluding database/container startup; concurrent readers observe only complete versions.

## Assumptions

- Appointment scheduling supplies authoritative scheduled order and configured slot duration; consultation supplies authoritative actual start/end timing; neither data store is duplicated by this feature.
- This feature is an operational staff view; patient notifications, manual ETA overrides, and public patient access are out of scope.
- The smart-queue feature may consume published ETA predictions, but it remains the owner of queue position and queue-number calculations.
- For local and automated tests only, a clearly named trusted test staff context may provide server-side identity and scope. It is not production authentication.
