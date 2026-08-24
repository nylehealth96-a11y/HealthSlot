# Feature Specification: Smart Waiting Queue

**Feature Branch**: `006-smart-queue`
**Created**: 2026-08-24
**Status**: Draft
**Input**: "Waiting queue; queue number; patients ahead; priority; predicted start; estimated wait; queue recalculation; doctor current status."

## Clarifications

### Session 2026-08-24

- Q: Which priority levels should staff be allowed to assign to waiting patients? → A: Normal, Priority, and Urgent.
- Q: What should the initial predicted start and estimated wait use when the doctor is available? → A: The doctor’s configured appointment slot duration.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View a Live Waiting Queue (Priority: P1)

Reception staff view the waiting queue for a doctor and branch, including each permitted patient's queue number, patients ahead, predicted start, and estimated wait.

**Why this priority**: Staff need an operational queue view before they can manage patient flow.

**Independent Test**: Given waiting entries and doctor availability, staff retrieve a scoped queue view with deterministic positions and estimates.

**Acceptance Scenarios**:

1. **Given** waiting entries for an in-scope doctor and branch, **When** authorized staff view the queue, **Then** they receive the ordered entries, queue numbers, patients-ahead counts, predicted starts, and estimated waits.
2. **Given** no waiting entries, **When** staff view the queue, **Then** they receive an empty queue with the doctor’s current status and no patient data.

---

### User Story 2 - Prioritize a Waiting Patient (Priority: P2)

Authorized reception staff assign or remove an operational priority for a waiting patient, so the queue recalculates predictably.

**Why this priority**: Exceptional operational needs must be visible without losing deterministic order.

**Independent Test**: A priority change for an in-scope waiting entry recalculates positions and estimates while preserving the entry’s immutable queue number.

**Acceptance Scenarios**:

1. **Given** an in-scope waiting entry, **When** authorized staff set its priority, **Then** it is ordered before lower-priority waiting entries and the queue is recalculated.
2. **Given** an absent, out-of-scope, or non-waiting entry, **When** staff attempt a priority change, **Then** no queue data changes or is disclosed.

---

### User Story 3 - Update Doctor Current Status (Priority: P3)

Authorized staff record a doctor’s current operational status so predicted starts and waits reflect whether the queue can progress.

**Why this priority**: Queue estimates must not imply progress when a doctor is unavailable.

**Independent Test**: Changing a doctor from available to unavailable recalculates affected predictions without changing queue order.

## Edge Cases

- Queue order uses priority first, then persisted arrival time, then immutable queue reference.
- A recalculation caused by concurrent updates produces one consistent published queue version.
- When a doctor is unavailable, predicted start and estimated wait are reported as unavailable rather than fabricated.
- All patient-facing operational values use the branch timezone; out-of-scope records are not disclosed.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Authorized staff MUST retrieve an in-scope doctor/branch waiting queue with a human-readable queue number, patients ahead, predicted start, estimated wait, priority, and doctor current status.
- **FR-002**: Queue numbers MUST be immutable, unique within their branch-local service date and doctor, and must not contain patient information.
- **FR-003**: The system MUST order waiting entries by operational priority, then persisted arrival time, then immutable queue reference; patients-ahead counts use that same order.
- **FR-004**: Authorized staff MUST be able to set `NORMAL`, `PRIORITY`, or `URGENT` operational priority only for an in-scope `WAITING` entry; this recalculates the affected queue without changing queue numbers.
- **FR-005**: The system MUST maintain a doctor current status of `AVAILABLE`, `BUSY`, or `UNAVAILABLE`; unavailable status makes predictions and estimates unavailable while retaining queue order.
- **FR-006**: Predicted start and estimated wait MUST be recalculated after a relevant queue, priority, or doctor-status change using persisted queue state, the doctor’s configured appointment slot duration, and branch timezone.
- **FR-007**: Concurrent queue, priority, and doctor-status changes MUST result in one deterministic queue version and no duplicate or contradictory positions.
- **FR-008**: Queue retrieval and updates MUST enforce trusted staff hospital/branch scope, fail closed without production authentication, and not disclose out-of-scope queue, patient, or doctor information.
- **FR-009**: Queue-priority and doctor-status changes MUST create PII-minimized audit records using trusted staff identity.

### Key Entities

- **Queue Number**: Immutable operational number for a doctor, branch, and local service date.
- **Queue Projection**: Ordered waiting-queue view containing position, patients ahead, predicted start, and estimated wait.
- **Operational Priority**: `NORMAL`, `PRIORITY`, or `URGENT` ordering level applied to a `WAITING` queue entry.
- **Doctor Current Status**: `AVAILABLE`, `BUSY`, or `UNAVAILABLE` operational availability state.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In acceptance testing, 95% of in-scope queue views are available to staff within 2 seconds.
- **SC-002**: In concurrent queue-update acceptance testing, 100% of resulting views have one deterministic order and no duplicate positions.
- **SC-003**: In acceptance testing, 100% of unavailable-doctor queues show unavailable estimates rather than a predicted time.
- **SC-004**: In acceptance testing, 100% of out-of-scope queue requests disclose no patient or queue information.

## Assumptions

- Check-in/queue-entry, branch timezone, and trusted staff identity capabilities are provided by earlier features.
- This feature manages operational waiting order and estimates; consultation completion, billing, and clinical triage are outside scope.
- Doctor scheduling provides each doctor’s configured appointment slot duration for estimates.
