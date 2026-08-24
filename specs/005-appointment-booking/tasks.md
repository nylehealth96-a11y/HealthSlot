# Tasks: Appointment Booking

**Input**: Design documents from `/specs/005-appointment-booking/`

**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [appointment-api.md](contracts/appointment-api.md), and [quickstart.md](quickstart.md)

**Tests**: Required by the specification and constitution. Write focused tests before each corresponding implementation task and run them after implementation.

## Phase 1: Setup and Dependency Contract

**Purpose**: Establish the feature boundaries and confirm the prerequisite modules needed by appointment booking.

- [X] T001 Review and reconcile appointment prerequisites with patient and doctor-scheduling feature contracts in `specs/002-patient-registration/`, `specs/005-doctor-scheduling/`, and `specs/005-appointment-booking/plan.md`.
- [ ] T002 [P] Define the appointment-owned package layout in `src/main/java/com/hospital/smartqueue/appointment/{api,application,domain,infrastructure}/`.
- [ ] T003 [P] Define appointment test fixture conventions in `src/test/java/com/hospital/smartqueue/appointment/` and `src/test/java/com/hospital/smartqueue/support/`.

---

## Phase 2: Foundational Safety, Persistence, and Security

**Purpose**: Deliver prerequisites that block all appointment user stories.

**⚠️ CRITICAL**: No appointment endpoint may be exposed before this phase is complete.

- [X] T004 Implement a generic trusted staff identity port, `APPOINTMENT_SCHEDULER` permission and scope authorization rules, and a fail-closed production adapter in `src/main/java/com/hospital/smartqueue/common/security/StaffAccessContext.java` and `src/main/java/com/hospital/smartqueue/common/security/FailClosedStaffAccessContext.java`.
- [ ] T005 Implement a clearly named test-only trusted staff identity fixture with hospital/branch scope and scheduling/reception role support in `src/test/java/com/hospital/smartqueue/support/TrustedTestStaffAccessContext.java`.
- [X] T006 Update trusted-actor audit recording and global error mapping for `UNAUTHENTICATED` (401), `FORBIDDEN` (403), non-disclosing `NOT_FOUND` (404), and conflicts (409) in `src/main/java/com/hospital/smartqueue/common/infrastructure/AuditService.java` and `src/main/java/com/hospital/smartqueue/common/api/GlobalExceptionHandler.java`.
- [ ] T007 Create the next ordered Flyway migration for appointments in `src/main/resources/db/migration/`, including UUID/FK/check constraints, immutable globally unique appointment number, optimistic version, `btree_gist`, partial exclusion constraint for overlapping `BOOKED` intervals, and supporting indexes.
- [ ] T008 Implement `Appointment`, `AppointmentStatus`, and repository support for scoped lookup and active-time filtering in `src/main/java/com/hospital/smartqueue/appointment/domain/Appointment.java`, `src/main/java/com/hospital/smartqueue/appointment/domain/AppointmentStatus.java`, and `src/main/java/com/hospital/smartqueue/appointment/infrastructure/AppointmentRepository.java`.
- [ ] T009 Define application ports for scheduling-derived slot validation and patient/doctor/branch ownership validation in `src/main/java/com/hospital/smartqueue/appointment/application/DoctorAvailabilityProvider.java` and `src/main/java/com/hospital/smartqueue/appointment/application/AppointmentOwnershipValidator.java`.
- [ ] T010 Add focused tests for fail-closed production identity, test-only identity isolation, scope/role decisions, and trusted audit actor handling in `src/test/java/com/hospital/smartqueue/common/security/StaffAccessContextTest.java` and `src/test/java/com/hospital/smartqueue/common/infrastructure/AuditServiceTest.java`.

**Checkpoint**: Database-level reservation invariant, trusted identity boundary, audit behavior, error mapping, and cross-feature ports are ready.

---

## Phase 3: User Story 1 - Book an Available Slot (Priority: P1) 🎯 MVP

**Goal**: Authorized staff can view a doctor's scoped available slots and create exactly one reservation for a selected slot, even under concurrent attempts.

**Independent Test**: A trusted test staff fixture requests a valid ≤31-day range, creates one `BOOKED` appointment from an exact returned slot, and two simultaneous identical booking attempts yield one success and one conflict.

### Tests for User Story 1

- [ ] T011 [P] [US1] Add unit tests for inclusive availability-range validation, branch-timezone/DST conversion, exact-slot matching, past/ended rejection, advisory stale availability, prerequisite rejection, and trusted scope checks in `src/test/java/com/hospital/smartqueue/appointment/application/AppointmentBookingServiceTest.java`.
- [ ] T012 [P] [US1] Add PostgreSQL Testcontainers integration coverage for the partial exclusion constraint and simultaneous same-slot booking outcome in `src/test/java/com/hospital/smartqueue/appointment/AppointmentBookingConcurrencyIntegrationTest.java`.
- [ ] T013 [P] [US1] Add API integration coverage for available-slot and booking request validation, fail-closed production access, and no client-supplied identity acceptance in `src/test/java/com/hospital/smartqueue/appointment/AppointmentBookingApiIntegrationTest.java`.

### Implementation for User Story 1

- [ ] T014 [US1] Implement appointment-number generation and transactional booking/availability application logic using trusted scope and scheduling/ownership ports in `src/main/java/com/hospital/smartqueue/appointment/application/AppointmentBookingService.java`.
- [ ] T015 [P] [US1] Create validated availability and booking request/response records in `src/main/java/com/hospital/smartqueue/appointment/api/AvailableSlotsResponse.java`, `src/main/java/com/hospital/smartqueue/appointment/api/CreateAppointmentRequest.java`, and `src/main/java/com/hospital/smartqueue/appointment/api/AppointmentResponse.java`.
- [ ] T016 [US1] Implement scoped availability and create-appointment endpoints without identity/scope request fields in `src/main/java/com/hospital/smartqueue/appointment/api/AppointmentController.java`.
- [ ] T017 [US1] Record PII-minimized booking audit events with trusted staff identity in `src/main/java/com/hospital/smartqueue/appointment/application/AppointmentBookingService.java`.
- [ ] T018 [US1] Run focused unit/API/concurrency tests and resolve booking failures in `src/test/java/com/hospital/smartqueue/appointment/`.

**Checkpoint**: Booking a valid slot is independently functional, protected by PostgreSQL under concurrency, scoped, audited, and test-validated.

---

## Phase 4: User Story 2 - Cancel or Reschedule an Appointment (Priority: P2)

**Goal**: Authorized staff can cancel before start or atomically reschedule a current-version booked appointment while retaining its number.

**Independent Test**: A cancellation releases a slot; a successful reschedule preserves the appointment number; a failed concurrent/invalid replacement leaves the original booking unchanged.

### Tests for User Story 2

- [ ] T019 [P] [US2] Add unit tests for allowed transitions, before-start rule, unchanged-interval rejection, simultaneous same-version conflicts, optional reasons, transactional audit failure rollback, and PII-minimized cancellation/reschedule audit metadata in `src/test/java/com/hospital/smartqueue/appointment/application/AppointmentLifecycleServiceTest.java`.
- [ ] T020 [P] [US2] Add integration tests proving cancellation releases the interval and failed rescheduling rolls back to the original `BOOKED` interval in `src/test/java/com/hospital/smartqueue/appointment/AppointmentLifecycleIntegrationTest.java`.
- [ ] T021 [P] [US2] Add API integration coverage for cancel/reschedule validation, scoped access, stale-version conflicts, and cancelled/past appointment rejections in `src/test/java/com/hospital/smartqueue/appointment/AppointmentLifecycleApiIntegrationTest.java`.

### Implementation for User Story 2

- [ ] T022 [US2] Implement transactional cancellation and same-aggregate atomic rescheduling with version, state, time, availability, and scope checks in `src/main/java/com/hospital/smartqueue/appointment/application/AppointmentLifecycleService.java`.
- [ ] T023 [P] [US2] Create validated cancellation and rescheduling request records with optional bounded operational reason in `src/main/java/com/hospital/smartqueue/appointment/api/CancelAppointmentRequest.java` and `src/main/java/com/hospital/smartqueue/appointment/api/RescheduleAppointmentRequest.java`.
- [ ] T024 [US2] Add scoped cancel and reschedule endpoints to `src/main/java/com/hospital/smartqueue/appointment/api/AppointmentController.java`.
- [ ] T025 [US2] Record trusted-actor, PII-minimized cancellation and reschedule audit events in `src/main/java/com/hospital/smartqueue/appointment/application/AppointmentLifecycleService.java`.
- [ ] T026 [US2] Run focused lifecycle unit/API/integration tests and resolve failures in `src/test/java/com/hospital/smartqueue/appointment/`.

**Checkpoint**: Cancellation and rescheduling are independently functional, conflict-safe, atomic, audited, and test-validated.

---

## Phase 5: User Story 3 - View Appointment Details and Status (Priority: P3)

**Goal**: Authorized staff can retrieve a scoped appointment by internal ID or appointment number without disclosing out-of-scope records.

**Independent Test**: A trusted in-scope fixture retrieves booked/cancelled/rescheduled details by both identifiers; an out-of-scope fixture receives non-disclosing `404`.

### Tests for User Story 3

- [ ] T027 [P] [US3] Add unit tests for scoped ID/appointment-number lookup and non-disclosure behavior in `src/test/java/com/hospital/smartqueue/appointment/application/AppointmentQueryServiceTest.java`.
- [ ] T028 [P] [US3] Add API integration coverage for both retrieval routes, response fields, and cross-hospital/branch non-disclosure in `src/test/java/com/hospital/smartqueue/appointment/AppointmentQueryApiIntegrationTest.java`.

### Implementation for User Story 3

- [ ] T029 [US3] Implement scoped appointment query service using server-resolved ownership and trusted staff access in `src/main/java/com/hospital/smartqueue/appointment/application/AppointmentQueryService.java`.
- [ ] T030 [US3] Add identifier and appointment-number retrieval endpoints to `src/main/java/com/hospital/smartqueue/appointment/api/AppointmentController.java`.
- [ ] T031 [US3] Run focused query unit/API tests and resolve failures in `src/test/java/com/hospital/smartqueue/appointment/`.

**Checkpoint**: All appointment lifecycle records can be retrieved within scope with non-disclosure outside it.

---

## Phase 6: Cross-Cutting Validation and Documentation

**Purpose**: Validate feature-wide correctness and retain operational design constraints.

- [ ] T032 Update appointment API contract and quickstart examples to match the implemented DTO validation, status/error behavior, and fail-closed boundary in `specs/005-appointment-booking/contracts/appointment-api.md` and `specs/005-appointment-booking/quickstart.md`.
- [ ] T033 [P] Add branch-timezone/DST boundary and 31-day availability-range integration coverage in `src/test/java/com/hospital/smartqueue/appointment/AppointmentAvailabilityTimezoneIntegrationTest.java`.
- [ ] T034 [P] Add feature-wide privacy/audit assertions ensuring no patient PII enters appointment audit metadata or error messages in `src/test/java/com/hospital/smartqueue/appointment/AppointmentPrivacyAuditIntegrationTest.java`.
- [ ] T035 Run the focused appointment suite and then the full Maven suite, documenting commands/results in `specs/005-appointment-booking/quickstart.md`.
- [ ] T036 Review `specs/005-appointment-booking/checklists/booking-security.md` against implemented behavior and mark only evidence-backed items complete.
- [ ] T037 [P] Add a 25-concurrent-request acceptance performance harness and PII-minimized operational signals for booking conflicts, stale changes, and fail-closed identity failures in `src/test/java/com/hospital/smartqueue/appointment/AppointmentPerformanceAcceptanceTest.java` and `src/main/java/com/hospital/smartqueue/appointment/application/AppointmentOperationalSignals.java`.

---

## Dependencies and Execution Order

- Phase 1 establishes the prerequisite contract and package/test structure.
- Phase 2 blocks all stories: identity, migration, persistence invariant, audit/error behavior, and cross-feature ports must be complete first.
- US1 depends on Phase 2 and is the MVP.
- US2 depends on the appointment aggregate and booking foundation from US1.
- US3 depends on the appointment aggregate and security foundation; it may begin after Phase 2 if the aggregate/query persistence tasks are complete, but is sequenced after US2 for delivery clarity.
- Phase 6 follows all selected stories.

## Parallel Opportunities

- T002 and T003 can run in parallel after T001.
- T004, T005, T006, T007, and T009 can be worked in parallel where their target files do not overlap; T008 follows T007.
- Within each user story, tasks marked `[P]` target separate files and may proceed in parallel after their dependencies.
- The three Phase 6 validation tasks T033–T035 can be prepared in parallel once their corresponding behavior exists.

## Implementation Strategy

### MVP First

1. Complete Phases 1 and 2 without exposing an unauthenticated appointment endpoint.
2. Complete US1 and prove the exact-slot, scope, and database-concurrency outcomes.
3. Stop for MVP validation before lifecycle extensions.

### Incremental Delivery

1. Add US2 after booking is stable; retain one aggregate/number through all lifecycle changes.
2. Add US3 scoped retrieval and non-disclosure checks.
3. Complete Phase 6 only after all story-specific tests pass.

## Notes

- All tasks follow the required checkbox, ID, optional parallel marker, story label, and exact-path format.
- The required trusted test context is not production authentication and must not be exposed through request data or runtime configuration.
- Select the next unused Flyway version only after prerequisite feature migrations are integrated; never reuse or reorder an existing version.

---

## Phase 7: Convergence

- [X] T038 CRITICAL Provide the patient ownership/lookup capability and migration prerequisite required before appointment booking can validate patient hospital membership per FR-002 and FR-016 (missing).
- [ ] T039 CRITICAL Provide the doctor scheduling, branch-timezone, and derived-available-slot capability required before appointment booking can validate slots per FR-001, FR-012, FR-016, and Constitution II–III (missing).

---

## Phase 8: Convergence

- [ ] T040 CRITICAL Implement a timezone-aware scheduling availability provider that derives ≤31-day doctor/branch slots from revisions, working periods, breaks, leaves, and exceptions in `src/main/java/com/hospital/smartqueue/doctor/scheduling/application/DoctorAvailabilityService.java` and expose its appointment-facing application port per FR-001, FR-012, FR-016, and Constitution II–III (partial).
