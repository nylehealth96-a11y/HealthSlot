# Tasks: Hospital Structure Management

**Input**: Design documents from `specs/001-hospital-structure/`

**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), and [hospital-structure-api.yaml](contracts/hospital-structure-api.yaml)

**Tests**: Unit and PostgreSQL Testcontainers integration tests are required for important business rules, database integrity, API errors, and ownership isolation.

**Organization**: Tasks are grouped by user story after shared setup and foundational work.

## Phase 1: Setup

**Purpose**: Recreate the Java 17 Maven/Spring Boot project structure without out-of-scope infrastructure.

- [X] T001 Create the Maven Java 17 Spring Boot project and dependencies in `pom.xml`
- [X] T002 [P] Create the Spring Boot entry point in `src/main/java/com/hospital/smartqueue/SmartQueueApplication.java`
- [X] T003 [P] Create feature-first package directories under `src/main/java/com/hospital/smartqueue/{common,hospital,department,doctor}/`
- [X] T004 [P] Create externalized PostgreSQL/Flyway configuration in `src/main/resources/application.yml`
- [X] T005 [P] Create Testcontainers configuration in `src/test/resources/docker-java.properties`

---

## Phase 2: Foundational

**Purpose**: Establish shared persistence, validation, errors, audit behavior, and the initial schema that block all user stories.

**Critical**: Complete this phase before implementing any user story.

- [ ] T006 Create UUID tables, ownership foreign keys, canonical uniqueness constraints (including normalized professional registration numbers), indexes, timestamps, doctor status checks, and the append-only audit-event table in `src/main/resources/db/migration/V1__create_hospital_structure.sql`
- [X] T007 [P] Create shared domain exceptions in `src/main/java/com/hospital/smartqueue/common/domain/DomainException.java`, `NotFoundException.java`, and `ConflictException.java`
- [X] T008 [P] Create the standard API error DTO and global exception advice, including the safe not-found outcome for unrelated nested identifiers, in `src/main/java/com/hospital/smartqueue/common/api/ApiError.java` and `GlobalExceptionHandler.java`
- [X] T009 [P] Create append-only audit persistence and service in `src/main/java/com/hospital/smartqueue/common/infrastructure/AuditEvent.java`, `AuditEventRepository.java`, and `AuditService.java`
- [X] T010 [P] Create canonical text normalization and time utilities in `src/main/java/com/hospital/smartqueue/common/domain/CanonicalText.java` and `src/main/java/com/hospital/smartqueue/common/infrastructure/TimeConfiguration.java`
- [ ] T011 Create a PostgreSQL-backed migration and application-context test in `src/test/java/com/hospital/smartqueue/integration/InfrastructureIntegrationTest.java`
- [X] T012 Create the global API validation/error contract probe in `src/test/java/com/hospital/smartqueue/integration/GlobalApiErrorIntegrationTest.java`

**Checkpoint**: The build, Flyway baseline, global errors, audit infrastructure, and PostgreSQL test environment are ready.

---

## Phase 3: User Story 1 - Establish Hospital Structure (Priority: P1) MVP

**Goal**: Administrators can create and view hospitals, their branches, and each branch's departments without crossing ownership boundaries.

**Independent Test**: Create one hospital, two branches, and departments; retrieve scoped lists; then use unrelated IDs and confirm no records are exposed or created.

- [ ] T013 [P] [US1] Create Hospital and Branch JPA entities in `src/main/java/com/hospital/smartqueue/hospital/domain/Hospital.java` and `Branch.java`
- [ ] T014 [P] [US1] Create Hospital and Branch scoped repositories in `src/main/java/com/hospital/smartqueue/hospital/infrastructure/HospitalRepository.java` and `BranchRepository.java`
- [ ] T015 [P] [US1] Create Department JPA entity and scoped repository in `src/main/java/com/hospital/smartqueue/department/domain/Department.java` and `infrastructure/DepartmentRepository.java`
- [ ] T016 [US1] Implement hospital and branch creation/listing ownership rules and creation audit events in `src/main/java/com/hospital/smartqueue/hospital/application/HospitalStructureService.java`
- [ ] T017 [US1] Implement department creation/listing, hospital-branch chain validation, and creation audit events in `src/main/java/com/hospital/smartqueue/department/application/DepartmentService.java`
- [ ] T018 [US1] Create Jakarta-validated hospital and branch request/response DTOs and REST controller in `src/main/java/com/hospital/smartqueue/hospital/api/`
- [ ] T019 [US1] Create Jakarta-validated department request/response DTOs and REST controller in `src/main/java/com/hospital/smartqueue/department/api/`
- [ ] T020 [P] [US1] Add hospital/branch/department application-service tests in `src/test/java/com/hospital/smartqueue/hospital/application/HospitalStructureServiceTest.java` and `src/test/java/com/hospital/smartqueue/department/application/DepartmentServiceTest.java`
- [ ] T021 [US1] Add PostgreSQL API integration coverage for canonical and concurrent duplicates, invalid input, safe not-found cross-hospital/cross-branch isolation, and foundational creation audit events in `src/test/java/com/hospital/smartqueue/integration/HospitalStructureApiIntegrationTest.java`

**Checkpoint**: User Story 1 supports a complete, scoped hospital hierarchy and is independently testable.

---

## Phase 4: User Story 2 - Register and Find Doctors (Priority: P2)

**Goal**: Administrators can register doctors to a hospital, associate them with one or more same-hospital departments, and view them by hospital or department.

**Independent Test**: Register a doctor with departments at two branches of one hospital; list the doctor by hospital and each department; reject a different hospital department and a global duplicate registration number.

- [ ] T022 [P] [US2] Create Doctor, DoctorStatus, and membership persistence models in `src/main/java/com/hospital/smartqueue/doctor/domain/Doctor.java`, `DoctorStatus.java`, and `DoctorDepartment.java`
- [ ] T023 [P] [US2] Create doctor and membership repositories with hospital-scoped queries in `src/main/java/com/hospital/smartqueue/doctor/infrastructure/DoctorRepository.java` and `DoctorDepartmentRepository.java`
- [ ] T024 [US2] Implement doctor registration, canonical duplicate handling, department ownership validation, and audit events in `src/main/java/com/hospital/smartqueue/doctor/application/DoctorService.java`
- [ ] T025 [US2] Create Jakarta-validated doctor registration/listing DTOs and `/api/v1` controller endpoints in `src/main/java/com/hospital/smartqueue/doctor/api/DoctorController.java`
- [ ] T026 [P] [US2] Add doctor-service tests for same-hospital membership, global registration uniqueness, and scoped lists in `src/test/java/com/hospital/smartqueue/doctor/application/DoctorServiceTest.java`
- [ ] T027 [US2] Add PostgreSQL API integration tests for doctor registration, duplicate conflicts, and department-scoped isolation in `src/test/java/com/hospital/smartqueue/integration/DoctorDirectoryApiIntegrationTest.java`

**Checkpoint**: User Story 2 supports an isolated doctor directory and is independently testable.

---

## Phase 5: User Story 3 - Maintain Doctor Availability Status (Priority: P3)

**Goal**: Administrators can set a doctor active or inactive without deleting the doctor or department associations.

**Independent Test**: Set an active doctor inactive, repeat the request, reactivate the doctor, and confirm memberships remain intact; reject an unrelated hospital context.

- [ ] T028 [US3] Implement validated idempotent doctor-status transitions and audit events, with no status-change audit event for an unchanged status, in `src/main/java/com/hospital/smartqueue/doctor/application/DoctorService.java`
- [ ] T029 [US3] Add a Jakarta-validated status request/response DTO and the status endpoint in `src/main/java/com/hospital/smartqueue/doctor/api/DoctorController.java`
- [ ] T030 [P] [US3] Add tests for active/inactive transitions, same-state requests with no status-change audit event, retained memberships, visible inactive doctors, and hospital scope in `src/test/java/com/hospital/smartqueue/doctor/application/DoctorStatusServiceTest.java`
- [ ] T031 [US3] Add PostgreSQL API integration tests for status isolation and audit history in `src/test/java/com/hospital/smartqueue/integration/DoctorStatusApiIntegrationTest.java`

**Checkpoint**: All Hospital Structure user stories are independently functional and testable.

---

## Phase 6: Polish and Cross-Cutting Validation

**Purpose**: Confirm the complete feature meets the specification, contract, and constitution.

- [ ] T032 [P] Align API request/response and error examples in `specs/001-hospital-structure/contracts/hospital-structure-api.yaml`
- [ ] T033 [P] Update runnable validation instructions in `specs/001-hospital-structure/quickstart.md`
- [ ] T034 Run `mvn clean test` and resolve all failures across `pom.xml` and `src/test/`
- [ ] T035 Review `specs/001-hospital-structure/tasks.md` and mark only implemented and validated tasks complete

---

## Dependencies and Execution Order

1. Complete Setup (T001-T005).
2. Complete Foundational work (T006-T012); it blocks all user stories.
3. Deliver User Story 1 (T013-T021) as the MVP.
4. Deliver User Story 2 (T022-T027), which depends on the hierarchy from User Story 1.
5. Deliver User Story 3 (T028-T031), which depends on doctor registration from User Story 2.
6. Complete Polish and full validation (T032-T035).

### Parallel Opportunities

- T002-T005 may proceed in parallel after T001 starts.
- T007-T010 may proceed in parallel after T006 schema design is agreed.
- T013-T015 may proceed in parallel after the foundational schema is complete.
- T020 can proceed while T016-T019 are implemented against agreed contracts.
- T022-T023 and T026 can proceed in parallel once the doctor model contract is agreed.
- T030 can proceed in parallel with T029 after the status rules are defined.
- T032-T033 can proceed in parallel after all user stories are complete.

## Implementation Strategy

### MVP First

Complete Phases 1 and 2, then deliver and validate User Story 1. This provides a usable, ownership-safe hospital hierarchy before doctor-directory work begins.

### Incremental Delivery

Add the doctor directory after the hierarchy, then add status management without changing doctor ownership or membership behavior. Run the full Maven test suite after each completed story and at the final checkpoint.
