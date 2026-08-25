# Feature Specification: Initial Role-Based Frontend

**Feature Branch**: `010-react-frontend`
**Created**: 2026-08-24
**Status**: Draft
**Input**: "After the API foundation works, create a React, TypeScript, Vite frontend with Patient, Doctor, and Reception/Admin interfaces."

## Clarifications

### Session 2026-08-24

- Q: Should this first UI version use real authenticated sessions only, or may it use a clearly labeled test/local identity mode? → A: Use a clearly named test/local trusted identity mode only until authentication exists.
- Q: Which backend API contracts must be complete before frontend implementation may begin? → A: Start UI work for available APIs; keep incomplete workflows visibly disabled and never simulate success.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Patient Appointment Journey (Priority: P1)

Patients can find doctors, view available slots, book an appointment, and view their appointment ETA.

**Why this priority**: It is the primary self-service appointment journey.

**Independent Test**: A patient can complete the supported search-to-book flow using authoritative API responses and view the resulting ETA without another patient's data.

**Acceptance Scenarios**:

1. **Given** a patient searches for doctors, **When** matching doctors are available, **Then** the patient can select a doctor and view available slots.
2. **Given** a patient selects an available slot, **When** booking succeeds, **Then** the patient receives the authoritative appointment result and can view its ETA when available.

---

### User Story 2 - Doctor Consultation Workspace (Priority: P1)

Doctors can view today’s assigned queue and progress the current patient through call, consultation start, and completion.

**Why this priority**: The workspace supports safe, timely clinical flow.

**Independent Test**: An assigned doctor can view only today’s in-scope queue and perform each allowed transition with the authoritative result reflected in the UI.

**Acceptance Scenarios**:

1. **Given** an assigned doctor opens today’s queue, **When** active entries exist, **Then** the doctor sees only permitted operational queue data.
2. **Given** the current queue entry is eligible, **When** the doctor calls, starts, or completes the consultation, **Then** the UI sends the appropriate action and refreshes from the authoritative result.

---

### User Story 3 - Reception Operations Workspace (Priority: P1)

Reception/Admin staff can register a patient, book an appointment, check in an appointment, and add a walk-in through one operational workspace.

**Why this priority**: Reception needs a single safe workflow for arrivals and scheduling.

**Independent Test**: An in-scope staff member completes each workflow through the authoritative APIs and sees validation/conflict feedback without cross-scope data exposure.

**Acceptance Scenarios**:

1. **Given** a reception staff member registers a patient or books an appointment, **When** submitted data is valid, **Then** the UI shows the authoritative created record.
2. **Given** an appointment arrives or a walk-in is eligible, **When** reception checks in or adds the walk-in, **Then** the UI shows the authoritative queue outcome.

### Edge Cases

- The UI must not offer an action when the authoritative API reports an unavailable slot, invalid transition, or conflict.
- A workflow whose required authoritative API is unavailable must be visibly disabled with a non-sensitive explanation and no simulated result.
- Loading, empty, validation, conflict, unauthorized, unavailable, and retry states must be distinguishable without exposing sensitive details.
- A repeated submit, slow response, or stale view must not cause the UI to present a duplicate booking, check-in, or consultation action as successful.
- Patient views must not expose staff-only queue data; staff views must not expose unnecessary patient contact or clinical data.
- Browser-provided hospital, branch, role, staff identity, authorization scope, consultation timing, or queue state must never be treated as authoritative.
- No matching doctors or available slots must produce an explicit empty state without enabling booking.
- Browser refresh/back navigation must not replay a state-changing action or treat stale data as current.
- Expired/invalid test identity or missing scope must produce a non-disclosing denied state and disable protected actions.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The feature MUST provide separate patient, doctor, and reception/admin interfaces with only the workflows authorized for each user type.
- **FR-002**: The patient interface MUST support doctor search, available-slot viewing, appointment booking, and appointment ETA viewing from authoritative APIs.
- **FR-003**: The doctor interface MUST support today’s assigned queue, patient call, consultation start, and consultation completion through authoritative APIs.
- **FR-004**: The reception/admin interface MUST support patient registration, appointment booking, appointment check-in, and walk-in creation through authoritative APIs.
- **FR-005**: The interface MUST render loading, empty, validation, conflict, unavailable, and non-disclosing access-denied states for every supported workflow.
- **FR-006**: The interface MUST prevent duplicate submit actions while a request is pending and must refresh the view from the authoritative result after a state-changing action.
- **FR-007**: The interface MUST not make browser-supplied identity, role, hospital, branch, authorization scope, patient contact destination, consultation timing, slot availability, or queue state authoritative.
- **FR-008**: The interface MUST display only the minimum patient and operational information returned for the signed-in role and must not persist sensitive data in browser storage or routine logs.
- **FR-009**: The interface MUST use the hospital/branch timezone and presentation values returned by authoritative APIs; it must not calculate appointment, ETA, or queue timing from browser-local assumptions.
- **FR-010**: The interface MUST remain a consumer of the API foundation and must not duplicate booking, check-in, queue, consultation, patient registration, scheduling, or ETA business rules.
- **FR-011**: The frontend MAY implement a workflow only when its required backend API contract is available and validated; unavailable workflows MUST remain visibly disabled and must not simulate results.
- **FR-012**: Until real authentication exists, the frontend MUST use only a clearly named test/local trusted identity mode that cannot be mistaken for production authentication.
- **FR-013**: The frontend MUST replace the test/local identity mode with real server-side authenticated identity before production use and must not send identity or authorization scope as normal authority-bearing request input.
- **FR-014**: All interactive controls MUST be keyboard reachable, have programmatic labels, expose visible focus, and announce loading, validation, conflict, unavailable, and denied status changes to assistive technology.
- **FR-015**: The first UI version MUST support the project-approved evergreen desktop browsers; browser support exclusions MUST be documented before release.
- **FR-016**: An API capability is considered available only when its versioned contract, authorization/scope behavior, error responses, and representative local fixtures are validated; otherwise its workflow remains disabled.
- **FR-017**: Read-only API requests MAY retry up to two times after transient failure with a bounded timeout; state-changing requests MUST NOT be automatically retried and require an explicit fresh user action.
- **FR-018**: The specification MUST define a minimum field set for each role: patients see only their own appointment/ETA fields; doctors see operational queue identifiers and allowed timing/status fields; reception sees only fields required for registration, booking, check-in, and walk-in operations.
- **FR-019**: The frontend MUST use one shared mapping for validation, non-disclosing not-found/denied, conflict/unavailable, and unexpected API responses across all workspaces.
- **FR-020**: The test/local identity mode MUST display a persistent development-only label, use fixed local fixtures, and be excluded from production builds and deployment configuration.
- **FR-021**: The frontend MUST keep patient contact data, clinical content, authorization scope, and identity credentials out of browser storage and routine logs.
- **FR-022**: Each enabled API contract MUST define an allowlisted response shape for the role, including identifiers, status, timestamps, and server-provided presentation values; the client MUST not infer omitted business fields.

### Key Entities

- **Role Workspace**: The patient, doctor, or reception/admin view with role-appropriate navigation and data.
- **Authoritative API Result**: The server response that determines what the interface may display or do.
- **Workflow Action**: A user-initiated booking, check-in, walk-in, consultation, or registration request with pending and result states.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In acceptance testing, 100% of the listed patient, doctor, and reception workflows complete through their authoritative API contract or show a clear safe error state.
- **SC-002**: In repeated-submit testing, 100% of pending state-changing actions produce at most one client request.
- **SC-003**: In role/scope acceptance testing, 100% of unavailable or denied views disclose no data beyond the API response permitted to that role.
- **SC-004**: In local usability validation with 50 representative records and warmed-up browser sessions, each listed workflow reaches its first actionable screen within 2 seconds after a successful authoritative response at the 95th percentile; startup and dependency installation are excluded.

## Assumptions

- The frontend will be placed under `frontend/` and use React, TypeScript, and Vite after the API prerequisite gate is satisfied.
- Available API contracts may be wired incrementally; unavailable workflows remain disabled rather than mocked.
- Visual design, real-time push updates, offline support, notifications, reporting, and provider integrations are out of scope for this first UI version.
- Backend APIs remain authoritative for validation, authorization, state transitions, conflicts, audit, timezone, and concurrency.
