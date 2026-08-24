# Tasks: Doctor Consultation Lifecycle

**Input**: Design documents from `specs/007-doctor-consultation/`

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/consultation-api.md`, `quickstart.md`

**Tests**: Required by the feature success criteria and Constitution III–V. Write focused tests before each lifecycle implementation task and keep production routes fail-closed.

## Phase 1: Setup

**Purpose**: Create the consultation module and focused test layout.

- [X] T001 Create consultation API, application, domain, and infrastructure package layout under `src/main/java/com/hospital/smartqueue/consultation/`.
- [X] T002 [P] Create consultation test fixtures and a clearly named test-only trusted staff context in `src/test/java/com/hospital/smartqueue/support/ConsultationFixtures.java` and `src/test/java/com/hospital/smartqueue/support/TestOnlyTrustedStaffContext.java`.
- [X] T003 [P] Create the consultation validation guide references in `specs/007-doctor-consultation/quickstart.md` for the focused lifecycle test classes.

---

## Phase 2: Foundational Prerequisites

**Purpose**: Establish the shared contracts and persistence required before any lifecycle action. Do not duplicate upstream patient, check-in, queue, scheduling, timezone, or identity persistence.

**⚠️ CRITICAL**: All user-story work is blocked until the authoritative upstream capabilities are integrated or contract adapters are available.

- [ ] T004 Integrate or explicitly adapt authoritative patient, check-in queue-entry, queue recalculation, branch-timezone, and trusted-identity capabilities through ports in `src/main/java/com/hospital/smartqueue/consultation/application/port/`.
- [ ] T005 Define trusted staff identity, role, hospital/branch scope, and assigned-doctor authorization interfaces with a production fail-closed adapter in `src/main/java/com/hospital/smartqueue/consultation/application/TrustedStaffContext.java` and `src/main/java/com/hospital/smartqueue/consultation/infrastructure/ProductionConsultationAccess.java`.
- [ ] T006 Define check-in-source validation and queue-recalculation request contracts, including idempotency and acknowledgement semantics, in `src/main/java/com/hospital/smartqueue/consultation/application/port/CheckInQueueEntryPort.java` and `src/main/java/com/hospital/smartqueue/consultation/application/port/QueueRecalculationPort.java`.
- [ ] T007 Inspect the integrated migration baseline and create the next contiguous Flyway migration for consultations and durable queue hand-off in `src/main/resources/db/migration/V<next-contiguous>__create_consultations.sql`.
- [ ] T008 Create `ConsultationState`, the versioned `Consultation` entity, and `QueueRecalculationOutbox` entity in `src/main/java/com/hospital/smartqueue/consultation/domain/`.
- [ ] T009 Create scoped consultation and outbox repositories in `src/main/java/com/hospital/smartqueue/consultation/infrastructure/ConsultationRepository.java` and `src/main/java/com/hospital/smartqueue/consultation/infrastructure/QueueRecalculationOutboxRepository.java`.
- [ ] T010 Add common lifecycle authorization, invalid-transition, non-disclosure, and PII-minimized audit helpers in `src/main/java/com/hospital/smartqueue/consultation/application/ConsultationAccessService.java` and `src/main/java/com/hospital/smartqueue/consultation/application/ConsultationAuditService.java`.
- [ ] T011 Add foundational persistence and trusted-context tests in `src/test/java/com/hospital/smartqueue/consultation/infrastructure/ConsultationPersistenceIntegrationTest.java` and `src/test/java/com/hospital/smartqueue/consultation/application/TrustedStaffContextTest.java`.

**Checkpoint**: Consultation persistence and intentional upstream contracts are available; production identity remains fail-closed.

---

## Phase 3: User Story 1 - Call the Next Patient (Priority: P1) 🎯 MVP

**Goal**: An in-scope receptionist calls a check-in-created waiting consultation exactly once without disclosing out-of-scope information.

**Independent Test**: A valid receptionist call records `CALLED` and `calledAt`; duplicate, invalid-state, absent, and out-of-scope calls do not mutate or disclose data.

### Tests

- [ ] T012 [P] [US1] Add call lifecycle and audit unit tests in `src/test/java/com/hospital/smartqueue/consultation/application/CallConsultationServiceTest.java`.
- [ ] T013 [P] [US1] Add call API validation, non-disclosure, reception-role, and production-fail-closed tests in `src/test/java/com/hospital/smartqueue/consultation/api/CallConsultationApiIntegrationTest.java`.
- [ ] T014 [P] [US1] Add concurrent duplicate-call persistence test in `src/test/java/com/hospital/smartqueue/consultation/infrastructure/CallConsultationConcurrencyIntegrationTest.java`.

### Implementation

- [ ] T015 [US1] Implement guarded `WAITING` to `CALLED` transition, check-in-source validation, scope authorization, `calledAt`, and atomic audit persistence in `src/main/java/com/hospital/smartqueue/consultation/application/CallConsultationService.java`.
- [ ] T016 [US1] Implement the versioned call endpoint and response mapping in `src/main/java/com/hospital/smartqueue/consultation/api/ConsultationLifecycleController.java` and `src/main/java/com/hospital/smartqueue/consultation/api/ConsultationResponse.java`.

**Checkpoint**: User Story 1 is independently testable and provides a safe receptionist call flow.

---

## Phase 4: User Story 2 - Start and Track a Consultation (Priority: P1)

**Goal**: Only the assigned doctor starts a called consultation once, persisting an authoritative actual-start instant.

**Independent Test**: An assigned in-scope doctor can start a called consultation; reception, another doctor, invalid states, and concurrent duplicates leave state/timing unchanged.

### Tests

- [ ] T017 [P] [US2] Add start-transition, assigned-doctor authorization, timestamp, and audit unit tests in `src/test/java/com/hospital/smartqueue/consultation/application/StartConsultationServiceTest.java`.
- [ ] T018 [P] [US2] Add start API invalid-state, non-disclosure, and fail-closed authorization tests in `src/test/java/com/hospital/smartqueue/consultation/api/StartConsultationApiIntegrationTest.java`.
- [ ] T019 [P] [US2] Add concurrent start persistence test in `src/test/java/com/hospital/smartqueue/consultation/infrastructure/StartConsultationConcurrencyIntegrationTest.java`.

### Implementation

- [ ] T020 [US2] Implement guarded `CALLED` to `IN_CONSULTATION` transition for the assigned doctor with atomic actual-start and audit persistence in `src/main/java/com/hospital/smartqueue/consultation/application/StartConsultationService.java`.
- [ ] T021 [US2] Extend the lifecycle controller and response mapping for the start route in `src/main/java/com/hospital/smartqueue/consultation/api/ConsultationLifecycleController.java` and `src/main/java/com/hospital/smartqueue/consultation/api/ConsultationResponse.java`.

**Checkpoint**: User Stories 1 and 2 retain distinct reception and assigned-doctor authority boundaries.

---

## Phase 5: User Story 3 - Complete a Consultation and Recalculate the Queue (Priority: P2)

**Goal**: The assigned doctor completes an in-consultation visit once, records valid actual timing, and reliably requests scoped queue recalculation.

**Independent Test**: Completion persists `COMPLETED` and valid end time with audit and durable queue hand-off; failure leaves no partial visible result or is recovered idempotently under the documented contract.

### Tests

- [ ] T022 [P] [US3] Add completion state, actual-end ordering, assigned-doctor authority, audit, and outbox unit tests in `src/test/java/com/hospital/smartqueue/consultation/application/CompleteConsultationServiceTest.java`.
- [ ] T023 [P] [US3] Add completion API invalid-state, non-disclosure, and lifecycle-response tests in `src/test/java/com/hospital/smartqueue/consultation/api/CompleteConsultationApiIntegrationTest.java`.
- [ ] T024 [P] [US3] Add completion concurrency and transaction-rollback tests for audit/outbox persistence in `src/test/java/com/hospital/smartqueue/consultation/infrastructure/CompleteConsultationPersistenceIntegrationTest.java`.
- [ ] T025 [P] [US3] Add queue-recalculation hand-off retry, idempotency, and terminal-failure contract tests in `src/test/java/com/hospital/smartqueue/consultation/application/QueueRecalculationOutboxServiceTest.java`.

### Implementation

- [ ] T026 [US3] Implement guarded `IN_CONSULTATION` to `COMPLETED` transition with actual-end validation, trusted assigned-doctor authorization, and atomic audit/outbox persistence in `src/main/java/com/hospital/smartqueue/consultation/application/CompleteConsultationService.java`.
- [ ] T027 [US3] Implement idempotent queue-recalculation hand-off delivery and recovery in `src/main/java/com/hospital/smartqueue/consultation/application/QueueRecalculationOutboxService.java`.
- [ ] T028 [US3] Extend the lifecycle controller and response mapping for completion and hand-off acknowledgement in `src/main/java/com/hospital/smartqueue/consultation/api/ConsultationLifecycleController.java` and `src/main/java/com/hospital/smartqueue/consultation/api/ConsultationResponse.java`.

**Checkpoint**: Completion is independently testable with no partial state/audit/queue result.

---

## Phase 6: Polish and Cross-Cutting Validation

**Purpose**: Complete quality requirements across all lifecycle actions.

- [ ] T029 Add cross-lifecycle transition-matrix, scope, PII-minimization, and unknown-versus-out-of-scope contract coverage in `src/test/java/com/hospital/smartqueue/consultation/api/ConsultationLifecycleContractIntegrationTest.java`.
- [ ] T030 Add the bounded lifecycle performance validation defined by the plan in `src/test/java/com/hospital/smartqueue/consultation/infrastructure/ConsultationLifecyclePerformanceIntegrationTest.java`.
- [ ] T031 Document the selected migration number, upstream adapter ownership, outbox recovery behavior, and any approved constitution exception in `specs/007-doctor-consultation/plan.md` and `specs/007-doctor-consultation/quickstart.md`.
- [ ] T032 Run the focused consultation tests, full Maven test suite, quickstart scenarios, and `git diff --check`; record results in `specs/007-doctor-consultation/quickstart.md`.

---

## Dependencies and Execution Order

- Phase 1 has no dependencies.
- Phase 2 depends on Phase 1 and blocks all lifecycle stories. T004–T006 require the authoritative upstream modules or their approved intentional contract adapters.
- US1 depends on Phase 2.
- US2 depends on US1 because only `CALLED` consultations may start.
- US3 depends on US2 because only `IN_CONSULTATION` consultations may complete.
- Phase 6 depends on all three stories.

## Parallel Opportunities

- T002 and T003 can proceed in parallel.
- Within Phase 2, T005 and T006 can proceed in parallel after the upstream integration decision; T008 and T009 follow the migration design.
- Test tasks T012–T014, T017–T019, and T022–T025 can each run in parallel within their story phase.
- The API tests and persistence tests use separate files and may be developed in parallel with their corresponding service test.

## Implementation Strategy

### MVP First

1. Complete setup and foundational integration contracts.
2. Complete US1 and its focused tests.
3. Validate that the receptionist call path is secure, non-disclosing, and concurrency-safe before starting doctor actions.

### Incremental Delivery

1. Add US1 (`WAITING → CALLED`).
2. Add US2 (`CALLED → IN_CONSULTATION`) with assigned-doctor enforcement.
3. Add US3 (`IN_CONSULTATION → COMPLETED`) with durable queue-recalculation hand-off.
4. Run cross-cutting concurrency, audit, recovery, and full-suite validation.

## Phase 7: Convergence

- [ ] T033 CRITICAL Integrate the authoritative patient, check-in queue-entry, queue recalculation, branch-timezone, and trusted-identity capabilities required by T004, or record an approved dependency blocker, before lifecycle code proceeds per US1–US3 and Constitution I–V (missing).
- [ ] T034 Reconcile and document completion acknowledgement, queue-handoff failure, durable recovery, and visible-state semantics across `specs/007-doctor-consultation/spec.md`, `plan.md`, `data-model.md`, and `contracts/consultation-api.md` per FR-006, FR-011, and SC-005 (partial).
- [ ] T035 Add successful completion-to-queue-result contract and integration coverage proving the completed consultation is removed from the active queue in `src/test/java/com/hospital/smartqueue/consultation/application/QueueRecalculationOutboxServiceTest.java` per SC-003 (missing).
- [ ] T036 Define bounded local lifecycle-performance fixture volume, warm-up, concurrency, measurement boundary, percentile rule, and threshold in `specs/007-doctor-consultation/plan.md` and align `src/test/java/com/hospital/smartqueue/consultation/infrastructure/ConsultationLifecyclePerformanceIntegrationTest.java` per plan performance goal (partial).
- [ ] T037 Reconcile the consultation source terminology and invariant so the sole check-in queue-entry origin is consistent in `specs/007-doctor-consultation/spec.md` and `data-model.md` per FR-010 and FR-013 (partial).
- [ ] T038 Define the user-facing branch-timezone representation and invalid/unavailable-timezone behavior in `specs/007-doctor-consultation/spec.md` and `contracts/consultation-api.md`, with matching tests under `src/test/java/com/hospital/smartqueue/consultation/`, per FR-005 (partial).
