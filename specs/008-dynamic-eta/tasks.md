# Tasks: Dynamic Doctor ETA

## Phase 1: Setup

- [X] T001 Create ETA module package layout under `src/main/java/com/hospital/smartqueue/eta/{api,application,domain,infrastructure}/`.
- [X] T002 [P] Create ETA fixtures and test-only trusted contexts in `src/test/java/com/hospital/smartqueue/support/EtaFixtures.java`.

## Phase 2: Foundational Prerequisites

- [X] T003 Integrate intentional appointment-schedule, consultation-timing, branch-timezone, trusted-identity, audit, and queue-consumer ports in `src/main/java/com/hospital/smartqueue/eta/application/port/`.
- [X] T004 Define production fail-closed ETA access and test-only context adapter in `src/main/java/com/hospital/smartqueue/eta/application/EtaAccess.java` and `src/main/java/com/hospital/smartqueue/eta/infrastructure/ProductionEtaAccess.java`.
- [X] T005 Select the integrated migration baseline and create `src/main/resources/db/migration/V2__create_eta_predictions.sql`.
- [X] T006 Create versioned ETA entities and repositories in `src/main/java/com/hospital/smartqueue/eta/domain/` and `src/main/java/com/hospital/smartqueue/eta/infrastructure/`.

## Phase 3: User Story 1 - View Updated Doctor ETAs (P1)

**Independent Test**: The Rahul/Amit/Priya example returns 10:25 and 10:40 without any prediction earlier than scheduled.

- [X] T007 [P] [US1] Add ETA-calculation and idle-gap tests in `src/test/java/com/hospital/smartqueue/eta/application/EtaPredictionServiceTest.java`.
- [ ] T008 [P] [US1] Add scoped ETA API and non-disclosure tests in `src/test/java/com/hospital/smartqueue/eta/api/EtaApiIntegrationTest.java`.
- [ ] T009 [US1] Implement deterministic ETA calculation and immutable version publication in `src/main/java/com/hospital/smartqueue/eta/application/EtaPredictionService.java`.
- [ ] T010 [US1] Implement the versioned ETA retrieval route and response mapping in `src/main/java/com/hospital/smartqueue/eta/api/EtaController.java` and `src/main/java/com/hospital/smartqueue/eta/api/EtaResponse.java`.

## Phase 4: User Story 2 - Recalculate After Timing Changes (P1)

**Independent Test**: Actual start/end and one-minute active-overrun ticks publish one deterministic recalculation version.

- [ ] T011 [P] [US2] Add timing-trigger, one-minute refresh, and concurrency tests in `src/test/java/com/hospital/smartqueue/eta/application/EtaRecalculationServiceTest.java`.
- [ ] T012 [P] [US2] Add version-concurrency persistence tests in `src/test/java/com/hospital/smartqueue/eta/infrastructure/EtaPredictionConcurrencyIntegrationTest.java`.
- [ ] T013 [US2] Implement authoritative timing trigger handling and active-overrun scheduler in `src/main/java/com/hospital/smartqueue/eta/application/EtaRecalculationService.java` and `src/main/java/com/hospital/smartqueue/eta/application/ActiveOverrunRefreshService.java`.

## Phase 5: User Story 3 - Protect ETA Data and Audit Changes (P2)

**Independent Test**: Out-of-scope requests disclose nothing; first delay and 5+ minute shifts audit atomically.

- [ ] T014 [P] [US3] Add authorization, five-minute audit-threshold, and rollback tests in `src/test/java/com/hospital/smartqueue/eta/application/EtaAuditServiceTest.java`.
- [ ] T015 [US3] Implement PII-minimized audit threshold and atomic prediction/audit transaction in `src/main/java/com/hospital/smartqueue/eta/application/EtaAuditService.java`.
- [ ] T016 [US3] Add cross-scope contract coverage in `src/test/java/com/hospital/smartqueue/eta/api/EtaContractIntegrationTest.java`.

## Phase 6: Validation

- [ ] T017 Add unavailable-upstream, timezone, and failure-injection tests in `src/test/java/com/hospital/smartqueue/eta/infrastructure/EtaFailureIntegrationTest.java`.
- [ ] T018 Run focused ETA tests, `mvn test`, quickstart validation, and `git diff --check`; record results in `specs/008-dynamic-eta/quickstart.md`.

## Dependencies

Phase 2 blocks all stories. US1 establishes the calculation/view MVP; US2 adds recalculation triggers; US3 adds audit/security completion.

## Parallel Opportunities

T002, T007/T008, T011/T012, and T014 can run in parallel once their prerequisites are ready.

## Phase 7: Convergence

- [ ] T019 CRITICAL Integrate intentional appointment-schedule, consultation-timing, branch-timezone, trusted-identity, audit, and queue-consumer ports only after their upstream contracts are available, per plan: Constitution Check (missing).
- [ ] T020 CRITICAL Implement production fail-closed ETA authorization and a test-only context adapter per FR-008 and FR-009 (missing).
- [ ] T021 Create the next contiguous Flyway migration plus immutable ETA version entities, repositories, uniqueness constraints, and a prediction/audit publication transaction per FR-006 and FR-011 (missing).
- [ ] T022 Implement and test deterministic same-day ETA calculation, idle-gap handling, excluded appointment handling, and PII-minimized scoped retrieval per FR-001 through FR-007 and US1 (missing).
- [ ] T023 Implement and test authoritative timing triggers, one-minute overrun refresh, duplicate-trigger idempotency, stale-revision rejection, and unpublished-transaction recovery per FR-002, FR-004, and FR-013 through FR-015 (missing).
- [ ] T024 Implement and test first-delay and five-minute-shift ETA audit records with trusted staff/system attribution and atomic rollback semantics per FR-010 and FR-011 (missing).
- [ ] T025 Add and run ETA unit, API, persistence, concurrency, authorization/non-disclosure, unavailable-upstream, timezone, failure-injection, and 50-appointment p95 performance validation per SC-001 through SC-006 (missing).
