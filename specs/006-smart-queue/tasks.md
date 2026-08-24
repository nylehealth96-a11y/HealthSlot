# Tasks: Smart Waiting Queue

## Phase 1: Setup

- [ ] T001 Create `src/main/java/com/hospital/smartqueue/queue/{api,application,domain,infrastructure}/`.
- [ ] T002 [P] Create queue fixtures in `src/test/java/com/hospital/smartqueue/support/SmartQueueFixtures.java`.

## Phase 2: Foundational

- [ ] T003 Define priority and doctor-status enums in `src/main/java/com/hospital/smartqueue/queue/domain/QueuePriority.java` and `DoctorOperationalStatus.java`.
- [ ] T004 Create queue projection entities and PostgreSQL migration in `src/main/java/com/hospital/smartqueue/queue/domain/QueueProjection.java` and `src/main/resources/db/migration/V{next}__create_smart_queue.sql`.
- [ ] T005 Create scoped repositories and trusted access port in `src/main/java/com/hospital/smartqueue/queue/infrastructure/` and `src/main/java/com/hospital/smartqueue/queue/application/QueueAccess.java`.
- [ ] T006 Define queue-entry, scheduling-duration, branch-timezone, and audit upstream ports in `src/main/java/com/hospital/smartqueue/queue/application/`.

## Phase 3: User Story 1 - View Live Queue (P1)

**Independent Test**: Scoped queue view has deterministic order, positions, counts, and estimates.

- [ ] T007 [P] [US1] Add projection/service tests in `src/test/java/com/hospital/smartqueue/queue/application/QueueProjectionServiceTest.java`.
- [ ] T008 [P] [US1] Add queue API integration tests in `src/test/java/com/hospital/smartqueue/queue/api/QueueProjectionApiIntegrationTest.java`.
- [ ] T009 [US1] Implement deterministic projection and estimate calculation in `src/main/java/com/hospital/smartqueue/queue/application/QueueProjectionService.java`.
- [ ] T010 [US1] Implement scoped queue retrieval route in `src/main/java/com/hospital/smartqueue/queue/api/QueueProjectionController.java`.

## Phase 4: User Story 2 - Prioritize Patient (P2)

**Independent Test**: Priority update recalculates queue without changing queue number.

- [ ] T011 [P] [US2] Add priority and concurrency tests in `src/test/java/com/hospital/smartqueue/queue/application/QueuePriorityServiceTest.java`.
- [ ] T012 [US2] Implement priority update, audit, and recalculation in `src/main/java/com/hospital/smartqueue/queue/application/QueuePriorityService.java`.
- [ ] T013 [US2] Implement priority update route in `src/main/java/com/hospital/smartqueue/queue/api/QueuePriorityController.java`.

## Phase 5: User Story 3 - Doctor Status (P3)

**Independent Test**: Unavailable status preserves order and makes estimates unavailable.

- [ ] T014 [P] [US3] Add status/recalculation tests in `src/test/java/com/hospital/smartqueue/queue/application/DoctorOperationalStatusServiceTest.java`.
- [ ] T015 [US3] Implement doctor status update and audit in `src/main/java/com/hospital/smartqueue/queue/application/DoctorOperationalStatusService.java`.
- [ ] T016 [US3] Implement doctor status route in `src/main/java/com/hospital/smartqueue/queue/api/DoctorOperationalStatusController.java`.

## Phase 6: Validation

- [ ] T017 Add persistence/concurrent-version tests in `src/test/java/com/hospital/smartqueue/queue/infrastructure/QueueConcurrencyIntegrationTest.java`.
- [ ] T018 Run focused smart-queue tests and `mvn test`; validate `git diff --check`.

## Dependencies

Setup and foundational work block all stories. US1 is the MVP; US2 and US3 follow and both recalculate the US1 projection.

## Phase 7: Convergence

- [ ] T019 CRITICAL Establish intentional, tested upstream contracts for trusted staff scope, `WAITING` queue entries, doctor slot duration, and branch timezone before wiring the queue module; do not access another module's persistence directly per Constitution I–II and FR-006/FR-008 (missing).
- [ ] T020 CRITICAL Integrate the required upstream patient, appointment/check-in queue-entry, doctor-scheduling, and trusted-identity capabilities into the feature baseline, or record an approved dependency blocker, before executing queue implementation tasks per US1 and Constitution I–V (missing).
- [ ] T021 Select and use the next concrete Flyway migration version after inspecting the integrated migration baseline; replace the unresolved `V{next}` reference before creating queue persistence per FR-002 and Constitution II (partial).
- [ ] T022 Define, implement, and test the `BUSY` doctor-status estimate/progression semantics without contradicting unavailable behavior, then apply them consistently to queue projection and status updates per FR-005–006 (partial).
- [ ] T023 Define and add a bounded automated performance validation for SC-001, including fixture volume, warm-up, measurement boundary, percentile rule, and threshold (partial).
- [ ] T024 Add transactional persistence and concurrent integration coverage proving one deterministic queue version, no duplicate positions, scope fail-closure, and atomic audit writes for priority/status changes per FR-007–009, SC-002/SC-004, and Constitution III–V (missing).
