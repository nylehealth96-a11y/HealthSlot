# Tasks: Doctor Scheduling

**Input**: Design documents from `/specs/005-doctor-scheduling/`

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/doctor-scheduling-api.md`, `quickstart.md`

**Tests**: Tests are included because the specification defines acceptance testing and the constitution requires automated coverage for important business rules.

**Organization**: Tasks are grouped by user story so each increment can be implemented and validated independently after the shared foundation is complete.

## Phase 1: Setup

**Purpose**: Establish the doctor-scheduling feature boundary and test support layout.

- [ ] T001 Create `api`, `application`, `domain`, and `infrastructure` packages under `src/main/java/com/hospital/smartqueue/doctor/scheduling/`
- [ ] T002 [P] Create scheduling test package layout under `src/test/java/com/hospital/smartqueue/doctor/scheduling/` and `src/test/java/com/hospital/smartqueue/support/`
- [ ] T003 [P] Add the doctor-scheduling feature design references to `specs/005-doctor-scheduling/quickstart.md`

---

## Phase 2: Foundational Prerequisites

**Purpose**: Complete the database, trusted-identity, authorization, auditing, and error foundations before implementing any user story.

**Critical**: No user-story work begins before this phase is complete.

- [ ] T004 Create `src/main/resources/db/migration/V2__create_doctor_scheduling.sql` with branch IANA timezone, UUID scheduling tables, foreign keys, range checks, uniqueness, indexes, and optimistic-version columns
- [ ] T005 Create `src/main/java/com/hospital/smartqueue/doctor/scheduling/application/SchedulingAccessContext.java` and `StaffSchedulingIdentity.java` so business logic receives trusted staff, role, hospital, branch, and doctor scope without reading request input
- [ ] T006 Create a production fail-closed `src/main/java/com/hospital/smartqueue/doctor/scheduling/infrastructure/UnauthenticatedSchedulingAccessContext.java` that rejects all production scheduling access until a real identity adapter is configured
- [ ] T007 Create test-only `src/test/java/com/hospital/smartqueue/support/TrustedTestSchedulingAccessContext.java` and test configuration that supplies explicit staff ID, role, hospital, branch, and doctor scope without being component-scanned in production
- [ ] T008 Update `src/main/java/com/hospital/smartqueue/common/api/GlobalExceptionHandler.java` and `src/main/java/com/hospital/smartqueue/common/domain/DomainException.java` to map unauthenticated access to 401, forbidden access to 403, validation to 400, and stale revisions to 409 in the standard error envelope
- [ ] T009 Update `src/main/java/com/hospital/smartqueue/common/infrastructure/AuditService.java` and `src/main/java/com/hospital/smartqueue/common/infrastructure/AuditEvent.java` so scheduling mutations retain the trusted actor reference and PII-minimal metadata
- [ ] T010 [P] Add branch timezone persistence/validation changes in `src/main/java/com/hospital/smartqueue/hospital/domain/Branch.java`, `src/main/java/com/hospital/smartqueue/hospital/api/CreateBranchRequest.java`, and `src/main/java/com/hospital/smartqueue/hospital/application/HospitalStructureService.java`
- [ ] T011 Add branch-scoped doctor membership lookup in `src/main/java/com/hospital/smartqueue/doctor/infrastructure/DoctorRepository.java` or an intentional doctor application contract used by scheduling
- [ ] T012 Add foundational migration, fail-closed access, trusted-test-context, timezone, error-envelope, and audit integration coverage in `src/test/java/com/hospital/smartqueue/doctor/scheduling/integration/SchedulingFoundationIntegrationTest.java`

**Checkpoint**: Database schema, test-only trusted identity, production fail-closed behavior, scoped ownership, error handling, and audit support are ready.

---

## Phase 3: User Story 1 - Configure Recurring Availability (Priority: P1) MVP

**Goal**: Authorized staff can configure a branch-scoped, effective-dated weekly schedule with working periods, breaks, slot duration, and stale-write protection.

**Independent Test**: Using trusted test staff authorized for a doctor and branch, save a valid revision and retrieve it; invalid periods/breaks/duration or stale changes return the documented error without changing the last valid revision.

### Tests for User Story 1

- [ ] T013 [P] [US1] Add recurring-period and break invariant unit tests in `src/test/java/com/hospital/smartqueue/doctor/scheduling/application/RecurringScheduleRulesTest.java`
- [ ] T014 [P] [US1] Add effective-date and stale-revision integration tests in `src/test/java/com/hospital/smartqueue/doctor/scheduling/integration/RecurringScheduleApiIntegrationTest.java`

### Implementation for User Story 1

- [ ] T015 [P] [US1] Create `DoctorScheduleRevision`, `WorkingPeriod`, and `ScheduleBreak` entities in `src/main/java/com/hospital/smartqueue/doctor/scheduling/domain/`
- [ ] T016 [P] [US1] Create JPA repositories for recurring schedule entities in `src/main/java/com/hospital/smartqueue/doctor/scheduling/infrastructure/`
- [ ] T017 [P] [US1] Create validated schedule request/response records in `src/main/java/com/hospital/smartqueue/doctor/scheduling/api/`
- [ ] T018 [US1] Implement branch ownership, authorized-staff access, effective-date selection, interval validation, optimistic conflict handling, and audit recording in `src/main/java/com/hospital/smartqueue/doctor/scheduling/application/DoctorScheduleService.java`
- [ ] T019 [US1] Implement `PUT /schedule` and `GET /schedule` contract endpoints in `src/main/java/com/hospital/smartqueue/doctor/scheduling/api/DoctorScheduleController.java`

**Checkpoint**: User Story 1 supports valid, effective-dated recurring schedules and rejects invalid/stale writes independently.

---

## Phase 4: User Story 2 - Manage Leave and Exceptions (Priority: P2)

**Goal**: Authorized staff can record inclusive leave and date-specific replacement schedules, including closed dates, without violating branch scope or conflict rules.

**Independent Test**: With a configured doctor schedule, create leave and exceptions and demonstrate that invalid date ranges, overlaps, stale writes, and unauthorized changes do not change existing rules.

### Tests for User Story 2

- [ ] T020 [P] [US2] Add leave-range and exception-replacement invariant unit tests in `src/test/java/com/hospital/smartqueue/doctor/scheduling/application/LeaveAndExceptionRulesTest.java`
- [ ] T021 [P] [US2] Add leave, special-hours exception, closed-day exception, overlap, and stale-write API tests in `src/test/java/com/hospital/smartqueue/doctor/scheduling/integration/LeaveAndExceptionApiIntegrationTest.java`

### Implementation for User Story 2

- [ ] T022 [P] [US2] Create `DoctorLeave`, `ScheduleException`, `ExceptionWorkingPeriod`, and `ExceptionBreak` entities in `src/main/java/com/hospital/smartqueue/doctor/scheduling/domain/`
- [ ] T023 [P] [US2] Create JPA repositories for leave and exception entities in `src/main/java/com/hospital/smartqueue/doctor/scheduling/infrastructure/`
- [ ] T024 [P] [US2] Create validated leave/exception request and response records in `src/main/java/com/hospital/smartqueue/doctor/scheduling/api/`
- [ ] T025 [US2] Implement inclusive leave validation, exception replacement/closed-date semantics, overlap rejection, scoped authorization, stale conflict handling, and audit recording in `src/main/java/com/hospital/smartqueue/doctor/scheduling/application/DoctorScheduleExceptionService.java`
- [ ] T026 [US2] Implement create, full-replacement update, and delete `/leave` and `/exceptions` contract endpoints with expected-version checks in `src/main/java/com/hospital/smartqueue/doctor/scheduling/api/DoctorScheduleController.java`

**Checkpoint**: User Stories 1 and 2 preserve valid schedule rules while supporting leave and date replacements.

---

## Phase 5: User Story 3 - View Available Slots (Priority: P3)

**Goal**: Authorized staff can retrieve ordered, deterministic, branch-timezone-aware available slots for a requested date range.

**Independent Test**: For normal, break, leave, special-hours, closed-exception, non-working, and revision-boundary dates, retrieve the exact derived slots and verify `leave > exception > recurring` precedence.

### Tests for User Story 3

- [ ] T027 [P] [US3] Add slot-generation unit tests for interval alignment, breaks, incomplete final slots, precedence, effective dates, and daylight-saving local-time behavior in `src/test/java/com/hospital/smartqueue/doctor/scheduling/application/AvailableSlotGeneratorTest.java`
- [ ] T028 [P] [US3] Add 31-day ordering, timezone, authorization, inactive-doctor, and response-contract integration tests in `src/test/java/com/hospital/smartqueue/doctor/scheduling/integration/AvailableSlotsApiIntegrationTest.java`

### Implementation for User Story 3

- [ ] T029 [P] [US3] Create derived slot result types and branch-timezone conversion support in `src/main/java/com/hospital/smartqueue/doctor/scheduling/domain/AvailableSlot.java` and `src/main/java/com/hospital/smartqueue/doctor/scheduling/application/BranchTimeResolver.java`
- [ ] T030 [US3] Implement deterministic effective-revision selection, leave/exception precedence, break subtraction, partial-slot omission, inactive-doctor handling, and request-range validation in `src/main/java/com/hospital/smartqueue/doctor/scheduling/application/AvailableSlotService.java`
- [ ] T031 [US3] Implement the `GET /available-slots` endpoint and ordered UTC/local response contract in `src/main/java/com/hospital/smartqueue/doctor/scheduling/api/DoctorScheduleController.java`

**Checkpoint**: All user stories are independently functional; slots are derived only and no booking/reservation state exists.

---

## Phase 6: Polish and Cross-Cutting Validation

**Purpose**: Confirm the feature meets the documented contract, constitution, and acceptance boundaries.

- [ ] T032 [P] Add scheduling audit metadata and forbidden/not-found disclosure regression coverage in `src/test/java/com/hospital/smartqueue/doctor/scheduling/integration/SchedulingSecurityAuditIntegrationTest.java`
- [ ] T033 [P] Add migration validation and 31-day p95 measurement coverage in `src/test/java/com/hospital/smartqueue/doctor/scheduling/integration/SchedulingPerformanceIntegrationTest.java`
- [ ] T034 Reconcile endpoint examples, error responses, and out-of-scope booking boundaries in `specs/005-doctor-scheduling/contracts/doctor-scheduling-api.md` and `specs/005-doctor-scheduling/quickstart.md`
- [ ] T035 Run the end-to-end validation scenarios from `specs/005-doctor-scheduling/quickstart.md` and record reviewer results in `specs/005-doctor-scheduling/checklists/scheduling-review.md`

---

## Dependencies and Execution Order

- Phase 1 has no dependencies.
- Phase 2 depends on Phase 1 and blocks every user story.
- US1 begins after Phase 2 and establishes recurring schedules.
- US2 depends on Phase 2 and uses the schedule foundation established by US1 for its independent test data.
- US3 depends on US1 and US2 because it projects their rules.
- Polish follows all desired stories.

```text
Setup → Foundation → US1 → US2 → US3 → Polish
```

## Parallel Opportunities

- T002 and T003 can run in parallel after T001.
- T005, T010, and their isolated tests can proceed in parallel once the migration shape is agreed.
- Within US1: T013/T014 and T015/T016/T017 can run in parallel; T018 then T019 follow.
- Within US2: T020/T021 and T022/T023/T024 can run in parallel; T025 then T026 follow.
- Within US3: T027/T028 and T029 can run in parallel; T030 then T031 follow.
- T032 and T033 can run in parallel during polish.

## Implementation Strategy

### MVP first

1. Complete Setup and Foundational prerequisites.
2. Complete US1 and its independent integration tests.
3. Review production fail-closed behavior before exposing any real endpoint access.

### Incremental delivery

1. Deliver recurring schedules (US1).
2. Add leave and date replacements (US2).
3. Add deterministic availability projection (US3).
4. Complete cross-cutting security, audit, migration, performance, and quickstart validation.

## Format Validation

Every task uses the required checkbox, sequential task ID, optional `[P]` marker, required user-story label within story phases, and an exact repository file path.

---

## Phase 7: Convergence

- [ ] T036 CRITICAL Implement the branch-timezone and doctor-scheduling Flyway schema per Constitution II and FR-010 in `src/main/resources/db/migration/V2__create_doctor_scheduling.sql` (missing)
- [ ] T037 CRITICAL Implement the fail-closed scheduling access context, trusted test context, scoped authorization, standard identity errors, and trusted-actor audit support per Constitution IV and FR-009–FR-011 in `src/main/java/com/hospital/smartqueue/doctor/scheduling/` and `src/main/java/com/hospital/smartqueue/common/` (missing)
- [ ] T038 Implement effective-dated recurring schedule, working-period, break, stale-version, and branch-membership behavior per US1 and FR-001–FR-003 in `src/main/java/com/hospital/smartqueue/doctor/scheduling/` (missing)
- [ ] T039 Implement versioned leave and exception create/update/delete behavior, replacement/closed-date semantics, and precedence validation per US2 and FR-004–FR-007, FR-013–FR-014 in `src/main/java/com/hospital/smartqueue/doctor/scheduling/` (missing)
- [ ] T040 Implement 31-day bounded, branch-timezone-aware deterministic available-slot projection and its API response per US3 and FR-008, FR-010, FR-015–FR-017 in `src/main/java/com/hospital/smartqueue/doctor/scheduling/` (missing)
- [ ] T041 Add automated scheduling rule, API, trusted-access, audit, migration, daylight-saving, stale-update, and 31-day performance validation per SC-002–SC-005 in `src/test/java/com/hospital/smartqueue/doctor/scheduling/` (missing)

---

## Phase 8: Convergence

- [ ] T042 Complete recurring schedule replacement/version conflicts, break-overlap validation, and revision tests per FR-001–FR-003 and FR-013 in `src/main/java/com/hospital/smartqueue/doctor/scheduling/` and `src/test/java/com/hospital/smartqueue/doctor/scheduling/` (partial)
- [ ] T043 Complete leave update/delete/version handling and implement date-specific exception persistence, CRUD, replacement, and precedence behavior per FR-004–FR-007 and FR-014 in `src/main/java/com/hospital/smartqueue/doctor/scheduling/` (partial)
- [ ] T044 Implement bounded deterministic available-slot projection and the `/available-slots` response contract per FR-008, FR-010, and FR-015–FR-017 in `src/main/java/com/hospital/smartqueue/doctor/scheduling/` (missing)
- [ ] T045 Wire `TrustedTestSchedulingAccessContext` through test-only Spring configuration and add scheduling API/integration coverage per FR-009–FR-011 and SC-003–SC-005 in `src/test/java/com/hospital/smartqueue/` (partial)
- [ ] T046 Complete trusted-actor auditing and branch timezone validation/response behavior per Constitution II, Constitution IV, and FR-010–FR-011 in `src/main/java/com/hospital/smartqueue/` (partial)
