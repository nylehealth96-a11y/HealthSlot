# Tasks: Hospital Structure Management

**Input**: Design documents from `/specs/001-hospital-structure/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Unit and PostgreSQL-backed integration tests are required for important business rules.

## Phase 1: Setup

- [X] T001 Create the Maven Java 17 Spring Boot project in `pom.xml`
- [X] T002 [P] Create the application entry point in `src/main/java/com/hospital/smartqueue/SmartQueueApplication.java`
- [X] T003 [P] Configure externalized PostgreSQL and Flyway settings in `src/main/resources/application.yml`
- [X] T004 [P] Create feature package directories under `src/main/java/com/hospital/smartqueue/{common,hospital,department,doctor}/`

## Phase 2: Foundational

- [X] T005 Create the initial PostgreSQL schema, UUIDs, timestamps, canonical uniqueness indexes, foreign keys, doctor membership, and audit event table in `src/main/resources/db/migration/V1__create_hospital_structure.sql`
- [X] T006 Implement shared API error DTOs and error codes in `src/main/java/com/hospital/smartqueue/common/api/`
- [X] T007 Implement domain exceptions and `@RestControllerAdvice` in `src/main/java/com/hospital/smartqueue/common/api/GlobalExceptionHandler.java`
- [X] T008 Implement shared audit event persistence and service in `src/main/java/com/hospital/smartqueue/common/infrastructure/`
- [X] T009 Configure PostgreSQL Testcontainers integration-test support in `pom.xml`
- [X] T010 Add migration, global-error, and audit integration tests in `src/test/java/com/hospital/smartqueue/integration/InfrastructureIntegrationTest.java`

## Phase 3: User Story 1 - Establish Hospital Structure (Priority: P1) 🎯 MVP

**Goal**: Create and view hospitals, their branches, and each branch's departments with strict hierarchy isolation.

**Independent Test**: Create one hospital, two branches, and departments; only the selected hierarchy is returned and unrelated IDs are rejected.

- [X] T011 [P] [US1] Create hospital domain entity, repository, and canonical-name rule in `src/main/java/com/hospital/smartqueue/hospital/{domain,infrastructure}/`
- [X] T012 [P] [US1] Create branch domain entity and repository in `src/main/java/com/hospital/smartqueue/hospital/infrastructure/`
- [X] T013 [P] [US1] Create department domain entity and repository in `src/main/java/com/hospital/smartqueue/department/{domain,infrastructure}/`
- [X] T014 [P] [US1] Add unit tests for hierarchy ownership and canonical duplicate rules in `src/test/java/com/hospital/smartqueue/hospital/application/HospitalStructureServiceTest.java`
- [X] T015 [US1] Implement transactional hospital and branch create/list use cases in `src/main/java/com/hospital/smartqueue/hospital/application/`
- [X] T016 [US1] Implement transactional department create/list use cases with hospital-branch chain validation in `src/main/java/com/hospital/smartqueue/department/application/`
- [X] T017 [US1] Create validated hospital and branch DTOs/controllers for `/api/v1/hospitals` and nested branches in `src/main/java/com/hospital/smartqueue/hospital/api/`
- [X] T018 [US1] Create validated department DTOs/controller for nested departments in `src/main/java/com/hospital/smartqueue/department/api/`
- [ ] T019 [US1] Add PostgreSQL-backed API integration tests for hierarchy creation, scoped lists, duplicates, and unrelated IDs in `src/test/java/com/hospital/smartqueue/integration/HospitalStructureApiIntegrationTest.java`

## Phase 4: User Story 2 - Register and Find Doctors (Priority: P2)

**Goal**: Register doctors in one hospital with one or more same-hospital departments and view them by hospital or department.

**Independent Test**: Register a doctor linked to two same-hospital departments and retrieve it from both lists; reject a foreign department.

- [ ] T020 [P] [US2] Create doctor entity, status enum, membership entity, and scoped repositories in `src/main/java/com/hospital/smartqueue/doctor/{domain,infrastructure}/`
- [ ] T021 [P] [US2] Add unit tests for doctor canonical duplicates and department ownership validation in `src/test/java/com/hospital/smartqueue/doctor/application/DoctorRegistrationServiceTest.java`
- [ ] T022 [US2] Implement transactional doctor registration and scoped list use cases in `src/main/java/com/hospital/smartqueue/doctor/application/`
- [ ] T023 [US2] Create validated doctor request/response DTOs and controller endpoints in `src/main/java/com/hospital/smartqueue/doctor/api/`
- [ ] T024 [US2] Add PostgreSQL-backed API integration tests for registration, membership, scoped doctor lists, duplicates, and cross-hospital rejection in `src/test/java/com/hospital/smartqueue/integration/DoctorRegistrationApiIntegrationTest.java`

## Phase 5: User Story 3 - Maintain Doctor Availability Status (Priority: P3)

**Goal**: Activate or deactivate a doctor without removing the doctor or department memberships.

**Independent Test**: Deactivate then reactivate a doctor, including a repeated same-status request, and confirm status, memberships, and audit history.

- [ ] T025 [P] [US3] Add unit tests for allowed and idempotent doctor status transitions in `src/test/java/com/hospital/smartqueue/doctor/application/DoctorStatusServiceTest.java`
- [ ] T026 [US3] Implement transactional activation/deactivation use case and audit creation in `src/main/java/com/hospital/smartqueue/doctor/application/`
- [ ] T027 [US3] Add validated doctor-status DTO and `PATCH /api/v1/hospitals/{hospitalId}/doctors/{doctorId}/status` controller in `src/main/java/com/hospital/smartqueue/doctor/api/`
- [ ] T028 [US3] Add PostgreSQL-backed API integration tests for status changes, idempotence, retained memberships, audit events, and unrelated-hospital rejection in `src/test/java/com/hospital/smartqueue/integration/DoctorStatusApiIntegrationTest.java`

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T029 Review all controllers for DTO-only transport and Jakarta Validation in `src/main/java/com/hospital/smartqueue/*/api/`
- [ ] T030 Review all repository methods for hospital-scoped reads/writes and all logs for unnecessary medical/PHI data in `src/main/java/com/hospital/smartqueue/`
- [ ] T031 Update and validate the API contract in `specs/001-hospital-structure/contracts/hospital-structure-api.yaml`
- [ ] T032 Run the end-to-end validation guide in `specs/001-hospital-structure/quickstart.md`
- [ ] T033 Run the full Maven test suite and resolve failures in `pom.xml`

## Dependencies & Execution Order

- Setup (T001-T004) precedes Foundational (T005-T010).
- Foundational work blocks all user stories.
- US1 (T011-T019) is the MVP and provides the hierarchy required by US2.
- US2 (T020-T024) depends on US1. US3 (T025-T028) depends on US2.
- Polish starts after all selected stories complete.

## Parallel Opportunities

- T002-T004 can run after T001; T011-T014 can proceed in parallel where files do not overlap.
- T020 and T021 can run in parallel after US1. T025 can begin after doctor status domain work is present.
- Integration tests remain sequential with their relevant endpoint and use-case tasks.

## Implementation Strategy

1. Complete setup and foundation, then deliver US1 as the independently demonstrable MVP.
2. Add doctor registration and discovery (US2), then status maintenance (US3).
3. Finish with scoped-access, validation, contract, logging, and quickstart review.

## Phase 7: Convergence

- [ ] T034 Add PostgreSQL-backed hospital/branch/department API integration tests per US1/AC1-AC4 (partial)
- [ ] T035 Implement doctor-department membership persistence, same-hospital validation, and doctor-by-department listing per FR-008 and FR-009 (missing)
- [ ] T036 Add canonical duplicate conflict handling and tests for doctor code and registration number per FR-007 and FR-011 (partial)
- [ ] T037 Complete validated idempotent doctor status request handling and unit/API tests per FR-010 and US3/AC1-AC3 (partial)
- [ ] T038 Add end-to-end audit-event assertions for all creation and status actions per FR-012 (partial)
