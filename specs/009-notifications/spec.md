# Feature Specification: Appointment Notifications

**Feature Branch**: `009-notifications`
**Created**: 2026-08-24
**Status**: Draft
**Input**: "Appointment booked; appointment reminder; doctor delayed; patient nearly due; patient called; cancellation; reschedule. Initially mock the notification provider. Do not immediately integrate WhatsApp, SMS, Firebase, or email providers. Make a clean notification abstraction first."

## Clarifications

### Session 2026-08-24

- Q: Which notification recipients are required for each event type? → A: Patients receive booking, reminder, doctor-delay, nearly-due, cancellation, and reschedule updates; reception receives patient-called updates.
- Q: What is the reminder timing policy relative to the branch-local appointment time? → A: One reminder is due 24 hours before the branch-local appointment time.
- Q: Should a notification delivery failure block its source workflow? → A: No; notification delivery is recoverable asynchronously and must not block booking, queue, consultation, cancellation, or rescheduling workflows.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Receive Appointment Updates (Priority: P1)

Patients receive a notification when an appointment is booked, cancelled, or rescheduled so they have current visit information.

**Why this priority**: Appointment lifecycle changes directly affect whether a patient attends at the right time.

**Independent Test**: A lifecycle event creates one appropriate pending notification through the mock delivery provider without exposing another patient's details.

**Acceptance Scenarios**:

1. **Given** an appointment is successfully booked, **When** its booking event is accepted, **Then** one appointment-booked notification is prepared for the appointment recipient.
2. **Given** an appointment is cancelled or rescheduled, **When** its lifecycle change is accepted, **Then** one notification reflects the final cancellation or revised appointment time.

---

### User Story 2 - Receive Timely Operational Updates (Priority: P1)

Patients receive relevant reminder, doctor-delay, and nearly-due updates before or during their visit; reception receives the patient-called update.

**Why this priority**: Timely updates reduce missed appointments and uncertainty while waiting.

**Independent Test**: Each approved operational event produces the matching notification once, with no duplicate delivery for a repeated event.

**Acceptance Scenarios**:

1. **Given** an appointment reaches 24 hours before its branch-local start, **When** the reminder is triggered, **Then** the patient receives one reminder for that appointment occurrence.
2. **Given** a doctor-delay or nearly-due event is published for an active visit, **When** the patient is eligible, **Then** the corresponding update is prepared once; when a patient-called event is published, one reception update is prepared.

---

### User Story 3 - Safely Manage Notification Delivery (Priority: P2)

Authorized staff can view operational delivery status without seeing unnecessary patient details, while administrators can replace the mock provider later without changing notification business rules.

**Why this priority**: Delivery must be observable and recoverable without prematurely coupling the system to a communications vendor.

**Independent Test**: Staff can view an in-scope notification status; production delivery remains unavailable except through the mock provider; swapping a provider boundary does not change event behavior.

**Acceptance Scenarios**:

1. **Given** an authorized staff member requests notification status for an in-scope appointment, **When** a notification exists, **Then** they see its type, status, and timestamps without contact details or message content.
2. **Given** a mock delivery attempt fails, **When** it is retried according to the delivery policy, **Then** the notification records a deterministic terminal status without duplicate successful delivery.

### Edge Cases

- A repeated upstream event, retry, or concurrent delivery attempt must not create more than one successful notification for the same appointment occurrence and notification type.
- An event received after cancellation, completion, or an invalid lifecycle transition must not send an obsolete notification.
- Missing or invalid contact details must produce a safe non-delivery status without exposing contact data through status endpoints or logs.
- A reschedule supersedes any unsent reminder for the previous appointment time.
- If the provider is unavailable, the notification remains recoverable according to the delivery policy and no appointment lifecycle operation is partially rolled back solely because delivery is delayed.
- Notification times and appointment times use the branch timezone for recipient-facing wording while stored event and delivery timestamps remain instants.
- Normal request input must not supply recipient identity, contact destination, staff identity, role, hospital/branch scope, provider result, or authorization scope.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST create notifications for successful appointment booking, cancellation, and rescheduling events.
- **FR-002**: The system MUST create patient notifications for appointment reminders, doctor-delay, and patient-nearly-due events and a reception notification for patient-called events when each event is authorized by its owning workflow.
- **FR-003**: The system MUST use a provider-neutral notification delivery boundary and a clearly identified mock provider for this feature; WhatsApp, SMS, Firebase, email, and other real provider integrations are out of scope.
- **FR-004**: The system MUST persist each notification with an internal unique identifier, source event identity, appointment/visit reference, notification type, delivery status, creation time, attempt history, and non-sensitive failure reason where applicable.
- **FR-005**: The system MUST prevent duplicate successful delivery for the same source event and notification type under retries or concurrent processing.
- **FR-006**: The system MUST reject or safely suppress obsolete notifications when the associated appointment or visit is cancelled, completed, superseded by rescheduling, or otherwise ineligible.
- **FR-007**: The system MUST prevent recipient contact details and full notification content from being returned by operational status views or written to routine logs.
- **FR-008**: Notification delivery and status retrieval MUST enforce trusted server-side identity and hospital/branch/resource scope, fail closed in production until real authentication exists, and return non-disclosing results for unknown or out-of-scope resources.
- **FR-009**: The system MUST not trust caller-supplied patient/recipient identity, contact destination, staff identity, hospital, branch, role, authorization scope, provider result, appointment state, or delivery status.
- **FR-010**: The system MUST audit notification creation, delivery attempts, terminal delivery outcomes, manual retries, and suppression decisions using PII-minimized metadata.
- **FR-011**: The system MUST atomically record the notification intent with its source workflow event where that workflow supports the same consistency boundary; delivery is asynchronous and a delivery failure MUST NOT block booking, queue, consultation, cancellation, or rescheduling workflows.
- **FR-012**: The system MUST retry an eligible mock-provider failure no more than three times, then record a terminal failure; retries must not create duplicate successful delivery.
- **FR-013**: The system MUST use the associated branch timezone for recipient-facing appointment, reminder, delay, nearly-due, and called timing.
- **FR-014**: Status views MUST expose only the minimum operational fields required to determine notification state and must not disclose whether an out-of-scope appointment or patient exists.
- **FR-015**: The system MUST accept notification triggers only from intentional server-side contracts owned by appointment booking, consultation/ETA, queue/check-in, or scheduling workflows; it MUST not duplicate their state or persistence.
- **FR-016**: The system MUST send booking, 24-hour reminder, doctor-delay, patient-nearly-due, cancellation, and reschedule notifications to the patient recipient selected by the owning workflow, and patient-called notifications to in-scope reception staff.

### Key Entities

- **Notification**: A PII-minimized, auditable record of one intended recipient update from an authorized workflow event.
- **Notification Event**: The idempotent source occurrence that requests one type of notification for an appointment or active visit.
- **Delivery Attempt**: A timestamped result of an attempt by the mock provider to deliver a notification.
- **Provider Boundary**: The stable delivery contract that permits a later provider integration without changing notification rules.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In acceptance testing, 100% of supported, eligible booking, cancellation, reschedule, reminder, delay, nearly-due, and called events create exactly one notification record for their source occurrence.
- **SC-002**: In concurrent and retry acceptance testing, 100% of duplicate source events and delivery attempts result in no more than one successful delivery for each notification type and source event.
- **SC-003**: In access-control acceptance testing, 100% of out-of-scope status requests disclose no appointment, patient, contact, message, or notification information.
- **SC-004**: In failure-injection acceptance testing, 100% of mock provider failures reach a documented retry or terminal state without losing the source notification record.
- **SC-005**: In local acceptance validation, staff can determine the delivery status of an in-scope notification in under 2 seconds for a workload of 50 notification records.

## Assumptions

- Appointment, consultation/ETA, queue/check-in, and scheduling modules remain the authoritative owners of their events and state; this feature consumes intentional contracts only.
- A mock-provider delivery failure is retried at most three times; all delivery occurs asynchronously from the source workflow.
- A clearly named mock provider is permitted for automated tests and local verification only; it is not a production communications integration.
- No real messaging provider credentials, templates, contact synchronization, push tokens, or patient-facing notification preference management are included in this feature.
- Contact selection and recipient permissions remain subject to the existing patient-data and authentication boundaries once upstream contracts exist.
