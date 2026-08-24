# Tasks: Appointment Notifications

**Input**: Design documents from `/specs/009-notifications/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/notification-api.md

## Phase 1: Setup

- [X] T001 Create notification module package layout in `src/main/java/com/hospital/smartqueue/notification/{api,application,domain,infrastructure}/`.
- [X] T002 [P] Create test-only notification fixtures and trusted context in `src/test/java/com/hospital/smartqueue/support/NotificationFixtures.java`.

## Phase 2: Foundational Prerequisites

- [X] T003 Define intentional inbound workflow, patient-contact, branch-timezone, trusted-identity, audit, and provider ports in `src/main/java/com/hospital/smartqueue/notification/application/port/`.
- [X] T004 Define production fail-closed notification access and test-only adapter in `src/main/java/com/hospital/smartqueue/notification/application/NotificationAccess.java` and `src/main/java/com/hospital/smartqueue/notification/infrastructure/ProductionNotificationAccess.java`.
- [X] T005 Select the integrated migration baseline and create `src/main/resources/db/migration/V2__create_notifications.sql`.
- [ ] T006 Create notification intent, delivery-attempt entities, state-transition rules, repositories, and unique source-event/type constraint in `src/main/java/com/hospital/smartqueue/notification/domain/` and `src/main/java/com/hospital/smartqueue/notification/infrastructure/`.
- [ ] T007 Document and integrate required upstream adapters only after appointment lifecycle, patient-contact, queue/check-in, ETA/consultation, branch-timezone, and authentication contracts exist, in `src/main/java/com/hospital/smartqueue/notification/infrastructure/`.

## Phase 3: User Story 1 - Receive Appointment Updates (Priority: P1) 🎯 MVP

**Goal**: Create exactly one patient notification intent for authorized booking, cancellation, and reschedule workflow events.

**Independent Test**: One eligible lifecycle trigger creates one intent; repeated/concurrent triggers create no duplicate intent or successful delivery.

- [ ] T008 [P] [US1] Add booking, cancellation, reschedule, superseded-reminder, and duplicate-trigger tests in `src/test/java/com/hospital/smartqueue/notification/application/NotificationIntentServiceTest.java`.
- [ ] T009 [P] [US1] Add idempotency and unique-constraint persistence tests in `src/test/java/com/hospital/smartqueue/notification/infrastructure/NotificationIntentConcurrencyIntegrationTest.java`.
- [ ] T010 [US1] Implement transactional lifecycle-trigger intent creation and obsolete-intent suppression in `src/main/java/com/hospital/smartqueue/notification/application/NotificationIntentService.java`.
- [ ] T011 [US1] Implement mock-only asynchronous delivery claim and provider-neutral request/result mapping in `src/main/java/com/hospital/smartqueue/notification/application/NotificationDeliveryService.java` and `src/main/java/com/hospital/smartqueue/notification/infrastructure/MockNotificationDeliveryProvider.java`.

## Phase 4: User Story 2 - Receive Timely Operational Updates (Priority: P1)

**Goal**: Prepare a patient 24-hour reminder, doctor-delay, and nearly-due notifications, and a reception patient-called notification, from authoritative workflow triggers.

**Independent Test**: Each eligible event routes once to its mandated recipient class using branch-local timing, without caller-supplied recipient or contact data.

- [ ] T012 [P] [US2] Add 24-hour branch-timezone reminder, delay, nearly-due, patient-called recipient, and ineligible-source tests in `src/test/java/com/hospital/smartqueue/notification/application/OperationalNotificationServiceTest.java`.
- [ ] T013 [US2] Implement authoritative reminder scheduling and operational trigger handling in `src/main/java/com/hospital/smartqueue/notification/application/OperationalNotificationService.java`.
- [ ] T014 [US2] Implement mock-provider failure retry scheduling with one initial attempt plus at most three retries in `src/main/java/com/hospital/smartqueue/notification/application/NotificationRetryService.java`.

## Phase 5: User Story 3 - Safely Manage Notification Delivery (Priority: P2)

**Goal**: Allow in-scope operational status and eligible manual retry without disclosing contacts, content, or out-of-scope resource existence.

**Independent Test**: In-scope staff view minimal status and retry an eligible failed intent; unknown/out-of-scope access has identical non-disclosing behavior.

- [ ] T015 [P] [US3] Add status, manual-retry, fail-closed authorization, non-disclosure, and response-field tests in `src/test/java/com/hospital/smartqueue/notification/api/NotificationApiIntegrationTest.java`.
- [ ] T016 [P] [US3] Add audit, retry-limit, terminal-failure, suppression, and transaction-rollback tests in `src/test/java/com/hospital/smartqueue/notification/application/NotificationAuditServiceTest.java`.
- [ ] T017 [US3] Implement PII-minimized notification audit lifecycle and atomic state/audit consistency in `src/main/java/com/hospital/smartqueue/notification/application/NotificationAuditService.java`.
- [ ] T018 [US3] Implement versioned status and manual-retry endpoints plus PII-free response mapping in `src/main/java/com/hospital/smartqueue/notification/api/NotificationController.java` and `src/main/java/com/hospital/smartqueue/notification/api/NotificationStatusResponse.java`.

## Phase 6: Validation

- [ ] T019 Add unavailable-upstream, concurrent-delivery claim, and 50-record status performance tests in `src/test/java/com/hospital/smartqueue/notification/infrastructure/NotificationFailureIntegrationTest.java`.
- [ ] T020 Run focused notification tests, `mvn test`, quickstart validation, and `git diff --check`; record results in `specs/009-notifications/quickstart.md`.

## Dependencies & Execution Order

- Phase 2 blocks all user stories; T007 is a prerequisite for real workflow adapters but does not authorize duplicating upstream modules.
- US1 establishes idempotent patient lifecycle intents and mock delivery.
- US2 depends on the same intent/delivery foundations and adds operational event routing.
- US3 depends on foundational access, persistence, and delivery state.
- Validation follows the implemented stories.

## Parallel Opportunities

- T002, T008/T009, T012, T015/T016 can run in parallel after their prerequisites.
- US1 and US2 tests may be prepared in parallel once Phase 2 contracts exist, but their implementations share intent/delivery services and should be integrated sequentially.

## Implementation Strategy

1. Complete setup and intentional boundaries, then validate production fails closed.
2. Deliver US1 as the MVP with idempotent lifecycle intents and mock delivery.
3. Add operational notification timing/routing in US2.
4. Add the protected status/retry and audit surface in US3.
5. Finish concurrency, failure, performance, and full-suite validation.

## Phase 7: Convergence

- [ ] T021 CRITICAL Implement and test idempotent notification intent persistence, state transitions, mock-only delivery claims, and obsolete-intent suppression per FR-001 through FR-006 and US1 (missing).
- [ ] T022 CRITICAL Implement and test server-side 24-hour reminder, doctor-delay, nearly-due, and reception patient-called trigger routing with branch-local time per FR-002, FR-013, FR-015, and US2 (missing).
- [ ] T023 Implement and test production fail-closed notification status/manual retry endpoints, non-disclosure, and PII-free logging per FR-007 through FR-009 and FR-014 (missing).
- [ ] T024 Implement and test PII-minimized audit records, four-total-attempt retry limits, terminal failure, suppression, and atomic notification-state/audit rollback per FR-010 through FR-012 and US3 (missing).
- [ ] T025 Integrate and contract-test atomic source-event notification intent recording or durable recovery separately for appointment lifecycle, queue/check-in, and ETA/consultation owners once their upstream contracts exist, per FR-011 and plan: Upstream Integration Prerequisites (partial).
- [ ] T026 Add and run notification concurrency, failure-injection, privacy/logging, branch-timezone, and 50-record status performance validation per SC-001 through SC-005 (missing).
