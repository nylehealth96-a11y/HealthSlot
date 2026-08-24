---
description: "Task list for appointment check-in implementation"
---

# Tasks: Appointment Check-In

**Input**: Design documents from `/specs/005-check-in/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/check-in-api.md, quickstart.md

**Tests**: Required by the feature specification, constitution, and quickstart. Write focused tests before their matching implementation task.

**Organization**: Tasks are grouped by user story so each workflow is independently testable.

## Phase 1: Setup

**Purpose**: Establish the check-in module and test fixtures.

- [ ] T001 Create `api`, `application`, `domain`, and `infrastructure` packages under `src/main/java/com/hospital/smartqueue/checkin/`.
- [ ] T002 [P] Create reusable timezoned hospital, branch, patient, doctor, appointment, and queue test fixtures in `src/test/java/com/hospital/smartqueue/support/CheckInFixtures.java`.
- [ ] T003 [P] Create clearly test-only trusted staff context support in `src/test/java/com/hospital/smartqueue/support/TestOnlyTrustedStaffContext.java`.

---

## Phase 2: Foundational Boundaries and Persistence

**Purpose**: Complete shared queue persistence, authorization, upstream ports, validation, and audit foundations before story work.

**CRITICAL**: No user story may expose a production endpoint until this phase is complete.

- [ ] T004 Define `QueueSource` and `QueueStatus` in `src/main/java/com/hospital/smartqueue/checkin/domain/QueueSource.java` and `src/main/java/com/hospital/smartqueue/checkin/domain/QueueStatus.java`.
- [ ] T005 Define the queue-entry aggregate and its immutable invariant methods in `src/main/java/com/hospital/smartqueue/checkin/domain/QueueEntry.java`.
- [ ] T006 After T015 reconciles upstream migrations, add the forward-compatible queue-entry table, UUIDs, timestamps, source/link check, scoped indexes, globally unique reference, unique appointment link, and partial active-visit unique index in `src/main/resources/db/migration/V2__create_queue_entries.sql`.
- [ ] T007 Create scoped queue-entry persistence operations in `src/main/java/com/hospital/smartqueue/checkin/infrastructure/QueueEntryRepository.java`.
- [ ] T008 [P] Define server-side trusted staff access/permission/scope abstractions and deny-by-default production adapter in `src/main/java/com/hospital/smartqueue/checkin/application/TrustedStaffAccess.java` and `src/main/java/com/hospital/smartqueue/checkin/infrastructure/DenyByDefaultTrustedStaffAccess.java`.
- [ ] T009 [P] Define patient, appointment, branch-timezone, and doctor-slot application ports in `src/main/java/com/hospital/smartqueue/checkin/application/PatientLookup.java`, `AppointmentLookup.java`, `BranchTimezoneLookup.java`, and `DoctorSlotAvailability.java`.
- [ ] T010 Implement PII-free queue-reference generation in `src/main/java/com/hospital/smartqueue/checkin/application/QueueReferenceGenerator.java`.
- [ ] T011 Add queue-entry response, mapper, walk-in request validation, and common scoped-not-found/error mapping in `src/main/java/com/hospital/smartqueue/checkin/api/QueueEntryResponse.java`, `QueueEntryMapper.java`, and `RegisterWalkInRequest.java`.
- [ ] T012 Extend `src/main/java/com/hospital/smartqueue/common/infrastructure/AuditService.java` and `src/main/java/com/hospital/smartqueue/common/infrastructure/AuditEvent.java` with PII-minimized check-in audit event support.
- [ ] T013 Add PostgreSQL persistence tests for reference, appointment-link, source/link check, and active-visit constraints in `src/test/java/com/hospital/smartqueue/checkin/infrastructure/QueueEntryRepositoryIntegrationTest.java`.
- [ ] T014 Add access-boundary tests proving production denies and test-only support cannot be supplied by normal request input in `src/test/java/com/hospital/smartqueue/checkin/application/TrustedStaffAccessTest.java`.
- [ ] T015 Establish and document required upstream application contracts before dependent check-in integration: patient scoped lookup from `002-patient-management`, scheduling-owned authoritative revalidation based on `doctor.scheduling.application.DoctorAvailabilityService` from `003-doctor-scheduling`, and the missing appointment module/transactional queue-link contract from `specs/005-appointment-booking`; record the agreed contracts in `specs/005-check-in/plan.md`.

**Checkpoint**: Queue infrastructure, fail-closed identity boundary, and PostgreSQL invariants are available.

---

## Phase 3: User Story 1 - Check In an Appointment (Priority: P1) MVP

**Goal**: Reception can check in an eligible in-scope booked appointment and receive exactly one linked queue entry.

**Independent Test**: An eligible request creates one `WAITING` appointment-source entry; repeat returns the same entry; wrong state/window/scope creates none and reveals no protected record.

- [ ] T016 [P] [US1] Add application tests for eligible `BOOKED`-without-link to `BOOKED`-with-link transition, invalid non-`BOOKED` transitions, idempotency, timezone boundaries, audit metadata, rollback, and non-disclosure in `src/test/java/com/hospital/smartqueue/checkin/application/AppointmentCheckInServiceTest.java`.
- [ ] T017 [P] [US1] Add API integration tests for the check-in contract, validation, fail-closed production access, and scoped failures in `src/test/java/com/hospital/smartqueue/checkin/api/AppointmentCheckInApiIntegrationTest.java`.
- [ ] T018 [P] [US1] Add concurrent duplicate appointment-check-in and cross-source check-in-versus-walk-in integration coverage in `src/test/java/com/hospital/smartqueue/checkin/infrastructure/CheckInConcurrencyIntegrationTest.java`.
- [ ] T019 [US1] Implement transactional appointment eligibility, appointment-module-owned `BOOKED` queue-link transition, scoped lookup, branch-local window calculation, queue creation, duplicate-key reload, and PII-free audit in `src/main/java/com/hospital/smartqueue/checkin/application/AppointmentCheckInService.java`.
- [ ] T020 [US1] Implement the thin versioned appointment check-in controller in `src/main/java/com/hospital/smartqueue/checkin/api/AppointmentCheckInController.java`.
- [ ] T021 [US1] After T015's appointment contract is available, add upstream appointment adapter integration without direct persistence access in `src/main/java/com/hospital/smartqueue/checkin/infrastructure/AppointmentCheckInDependencies.java`.

**Checkpoint**: User Story 1 is independently complete and passes its focused tests.

---

## Phase 4: User Story 2 - Register a Walk-In Queue Entry (Priority: P2)

**Goal**: Reception can register one scoped walk-in only when the doctor has an available scheduled branch slot.

**Independent Test**: A valid scoped request creates a `WALK_IN` entry without appointment ID; unavailable, invalid, out-of-scope, and duplicate-active requests persist nothing additional.

- [ ] T022 [P] [US2] Add application tests for patient/doctor/branch scope, commit-boundary slot revalidation, duplicate active visits, audit rollback, and timezone visit date in `src/test/java/com/hospital/smartqueue/checkin/application/WalkInRegistrationServiceTest.java`.
- [ ] T023 [P] [US2] Add API integration tests for the walk-in contract, request validation, trusted access, non-disclosure, and conflict mapping in `src/test/java/com/hospital/smartqueue/checkin/api/WalkInRegistrationApiIntegrationTest.java`.
- [ ] T024 [P] [US2] Add concurrent duplicate walk-in, changed-availability-at-commit, and cross-source check-in-versus-walk-in integration coverage in `src/test/java/com/hospital/smartqueue/checkin/infrastructure/WalkInConcurrencyIntegrationTest.java`.
- [ ] T025 [US2] Implement transactional scoped walk-in registration, scheduling-owner commit-boundary availability revalidation, active-visit conflict mapping, queue creation, and audit in `src/main/java/com/hospital/smartqueue/checkin/application/WalkInRegistrationService.java`.
- [ ] T026 [US2] Implement the validated versioned walk-in controller in `src/main/java/com/hospital/smartqueue/checkin/api/WalkInRegistrationController.java`.
- [ ] T027 [US2] After T015's patient and scheduling contracts are available, add patient, branch-timezone, and scheduling availability adapter integration without direct repository access in `src/main/java/com/hospital/smartqueue/checkin/infrastructure/WalkInRegistrationDependencies.java`.

**Checkpoint**: User Stories 1 and 2 are independently complete and preserve shared queue invariants.

---

## Phase 5: User Story 3 - View Queue Entry Status (Priority: P3)

**Goal**: Reception retrieves a permitted queue entry by internal UUID or queue reference.

**Independent Test**: Both identifiers return only the in-scope entry and never disclose an out-of-scope or absent entry.

- [ ] T028 [P] [US3] Add application tests for scoped UUID/reference lookup, response contents, and non-disclosure in `src/test/java/com/hospital/smartqueue/checkin/application/QueueEntryQueryServiceTest.java`.
- [ ] T029 [P] [US3] Add API integration tests for both retrieval routes, trusted-access outcomes, and global error envelope in `src/test/java/com/hospital/smartqueue/checkin/api/QueueEntryQueryApiIntegrationTest.java`.
- [ ] T030 [US3] Implement scoped queue-entry retrieval and response mapping in `src/main/java/com/hospital/smartqueue/checkin/application/QueueEntryQueryService.java`.
- [ ] T031 [US3] Implement thin versioned UUID/reference retrieval routes in `src/main/java/com/hospital/smartqueue/checkin/api/QueueEntryQueryController.java`.

**Checkpoint**: All check-in stories are independently functional and scoped.

---

## Phase 6: Polish and Cross-Cutting Validation

**Purpose**: Close review gaps and validate the integrated feature.

- [X] T032 Resolve requirements gaps identified by `specs/005-check-in/checklists/checkin-security.md` in the appropriate files under `specs/005-check-in/` before treating that review gate as satisfied.
- [ ] T033 [P] Add queue ordering and response-privacy integration coverage in `src/test/java/com/hospital/smartqueue/checkin/api/QueueEntryQueryApiIntegrationTest.java`.
- [ ] T034 [P] Add migration, mandatory-audit rollback, and transaction-boundary persistence regression coverage in `src/test/java/com/hospital/smartqueue/checkin/infrastructure/QueueEntryRepositoryIntegrationTest.java`.
- [ ] T035 Add local PostgreSQL Testcontainers performance validation for 100 warmed sequential appointment check-ins and 100 warmed sequential walk-ins against 100 patients, 10 doctors, and 200 eligible appointments; measure service invocation through commit and assert p95 <2 seconds and max <5 seconds for each operation type in `src/test/java/com/hospital/smartqueue/checkin/infrastructure/CheckInPerformanceIntegrationTest.java`.
- [ ] T036 Add local PostgreSQL Testcontainers performance validation for 50 warmed scoped UUID reads and 50 warmed scoped queue-reference reads at 10 concurrent callers against at least 200 queue entries; measure service invocation through response mapping and assert p95 <2 seconds and max <5 seconds in `src/test/java/com/hospital/smartqueue/checkin/infrastructure/CheckInPerformanceIntegrationTest.java`.
- [ ] T037 Run focused check-in validation from `specs/005-check-in/quickstart.md` and fix resulting check-in defects.
- [ ] T038 Run `mvn test` and `git diff --check`, then record validation results in `specs/005-check-in/quickstart.md` if commands or prerequisites changed.

---

## Dependencies and Execution Order

- Phase 1 has no dependencies.
- Phase 2 depends on Phase 1 and blocks all user stories.
- US1, US2, and US3 depend on Phase 2. Implement in P1 to P3 order; US2 and US3 can proceed in parallel after their shared persistence boundary is stable.
- Phase 6 depends on the completed stories selected for delivery.

## Parallel Opportunities

- T002 and T003 can run in parallel.
- T008 and T009 can run in parallel after the module exists.
- T015–T017, T021–T023, and T027–T028 are parallel test work within their stories.
- US2 and US3 can be assigned to separate developers after Phase 2.

## Implementation Strategy

### MVP first

1. Complete Phases 1 and 2.
2. Complete and validate Phase 3 (appointment check-in).
3. Stop for an independent MVP review before adding walk-ins and retrieval.

### Incremental delivery

1. Add US1 and validate idempotent appointment check-in.
2. Add US2 and validate safe scheduling-gated walk-ins.
3. Add US3 and validate non-disclosing retrieval.
4. Complete the Phase 6 cross-cutting gate.

## Notes

- Every task follows the required checkbox, ID, optional parallel marker, story label, and path format.
- The checklist is reviewer-owned: implementation must not mark its custom items complete.
- Do not commit request-supplied or production-capable fake authentication.

---

## Phase 7: Convergence

- [X] T039 Align the performance goals in `specs/005-check-in/plan.md` with SC-001 and SC-003's local Testcontainers p95/max thresholds and workload definitions per SC-001/SC-003 (partial).
- [X] T040 Reconcile the Flyway migration selection/dependency order in `specs/005-check-in/plan.md` and `specs/005-check-in/tasks.md` so upstream migration integration completes before `V2__create_queue_entries.sql` is created per plan storage decision and T006/T015 (partial).
- [ ] T041 Replace the ambiguous “checked-in status” wording in `specs/005-check-in/spec.md` with the defined queue operational state while preserving appointment status `BOOKED` per US1 and FR-012 (partial).
- [ ] T042 Correct stale parallel task ranges in `specs/005-check-in/tasks.md` per the current task IDs (partial).
