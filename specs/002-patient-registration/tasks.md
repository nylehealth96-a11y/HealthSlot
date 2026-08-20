# Tasks: Patient Registration and Profile Management

**Input**: Design documents from `specs/002-patient-registration/`  
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, and `contracts/patient-api.md`

**Tests**: Automated tests are required by the project constitution for all important business rules. Implement the test tasks below with their corresponding story.

## Phase 1: Setup

- [X] T001 Create the feature package skeleton at `src/main/java/com/hospital/smartqueue/patient/{api,application,domain,infrastructure}/` and `src/test/java/com/hospital/smartqueue/patient/{application,integration}/`.
- [X] T002 Verify `pom.xml` remains configured for Java 17, Spring Web, Spring Data JPA, Jakarta Validation, PostgreSQL, and Flyway; do not add infrastructure dependencies.

## Phase 2: Foundational Persistence

**Purpose**: Establish the patient schema and shared persistence rules needed by every patient story.

- [X] T003 Add `src/main/resources/db/migration/V2__create_patients.sql` to create the `patients` table with UUID primary key, globally unique `patient_number`, required demographic/contact columns, nullable optional profile columns, `created_at`/`updated_at` as `timestamptz`, and search indexes for normalized names and mobile number.
- [X] T004 [P] Add `src/test/java/com/hospital/smartqueue/patient/integration/PatientMigrationIntegrationTest.java` to verify the Flyway migration, UUID identity, patient-number uniqueness, nullable optional fields, and timestamp columns against PostgreSQL.
- [X] T005 Implement the patient persistence model in `src/main/java/com/hospital/smartqueue/patient/domain/Patient.java` and `src/main/java/com/hospital/smartqueue/patient/infrastructure/PatientRepository.java`, including audit timestamps and repository methods that support immutable-ID lookup and scoped searches.

**Checkpoint**: Patient storage is ready and migration-tested; no REST feature behavior is required yet.

## Phase 3: User Story 1 - Register a Patient (Priority: P1) 🎯 MVP

**Goal**: Reception staff can register a patient and receive its UUID and human-readable patient number.

**Independent Test**: Submit a valid registration request and verify the response and database record; submit missing/invalid required data and a duplicate patient number and verify consistent validation/conflict errors.

- [X] T006 [P] [US1] Add registration business-rule tests in `src/test/java/com/hospital/smartqueue/patient/application/PatientRegistrationServiceTest.java` for required values, date-of-birth validity, patient-number generation, and duplicate-number conflict handling.
- [X] T007 [US1] Implement patient-number generation and the transactional registration use case in `src/main/java/com/hospital/smartqueue/patient/application/PatientRegistrationService.java`; persist an operational audit event through `AuditService` without placing profile data in logs.
- [X] T008 [US1] Add registration DTOs with Jakarta Validation in `src/main/java/com/hospital/smartqueue/patient/api/CreatePatientRequest.java` and `PatientResponse.java`.
- [X] T009 [US1] Add `POST /api/v1/patients` in `src/main/java/com/hospital/smartqueue/patient/api/PatientController.java`, delegating only to the application service and using the existing global error contract.
- [X] T010 [US1] Add PostgreSQL-backed endpoint coverage in `src/test/java/com/hospital/smartqueue/patient/integration/PatientApiIntegrationTest.java` for successful registration, validation failures, timestamps, and audit creation.

**Checkpoint**: Patient registration is independently usable and testable.

## Phase 4: User Story 2 - Retrieve and Search Patients (Priority: P2)

**Goal**: Staff can retrieve a patient by UUID or patient number and find patients by number, name, or mobile number.

**Independent Test**: Register multiple patients, retrieve one by each identifier, then search by each supported criterion without receiving unrelated records.

- [X] T011 [P] [US2] Add retrieval/search tests in `src/test/java/com/hospital/smartqueue/patient/application/PatientQueryServiceTest.java` covering UUID and patient-number lookup, case-insensitive normalized name matching, mobile matching, empty-result behavior, and not-found handling.
- [X] T012 [US2] Implement read and search application services in `src/main/java/com/hospital/smartqueue/patient/application/PatientQueryService.java`, using only repository queries that support the documented search criteria and pagination.
- [X] T013 [US2] Add `GET /api/v1/patients/{patientId}`, `GET /api/v1/patients/patient-number/{patientNumber}`, and `GET /api/v1/patients` query handling in `src/main/java/com/hospital/smartqueue/patient/api/PatientController.java`, with response DTOs that never expose JPA entities.
- [X] T014 [US2] Add endpoint integration coverage in `src/test/java/com/hospital/smartqueue/patient/integration/PatientApiIntegrationTest.java` for both retrieval paths, each search criterion, pagination, and the consistent not-found response.

**Checkpoint**: Registration, retrieval, and search work independently through the REST API.

## Phase 5: User Story 3 - Update Basic Patient Information (Priority: P3)

**Goal**: Staff can correct basic profile information while the internal identifier and patient number remain immutable.

**Independent Test**: Update a registered patient's editable details, retrieve it to confirm persistence, and verify that attempts to change identity fields are rejected or impossible through the API contract.

- [X] T015 [P] [US3] Add update-rule tests in `src/test/java/com/hospital/smartqueue/patient/application/PatientProfileServiceTest.java` for editable fields, required-field validation, immutable UUID/patient number, audit creation, and updated timestamp behavior.
- [X] T016 [US3] Implement the transactional profile-update use case in `src/main/java/com/hospital/smartqueue/patient/application/PatientProfileService.java`, updating only basic profile fields and writing a minimal operational audit event.
- [X] T017 [US3] Add `UpdatePatientRequest.java` and `PUT /api/v1/patients/{patientId}` in `src/main/java/com/hospital/smartqueue/patient/api/`, with Jakarta Validation and the existing error response format.
- [X] T018 [US3] Add PostgreSQL-backed endpoint coverage in `src/test/java/com/hospital/smartqueue/patient/integration/PatientApiIntegrationTest.java` for successful updates, validation failures, immutable identifiers, timestamps, and audit events.

**Checkpoint**: All patient profile functions in the specification are available.

## Phase 6: Polish and Cross-Cutting Verification

- [X] T019 Review `src/main/java/com/hospital/smartqueue/patient/` for feature-oriented boundaries, thin controllers, no PHI in logs, no secrets, UUID-only internal identity, and external-input validation.
- [X] T020 Update `specs/002-patient-registration/quickstart.md` with runnable patient API examples and required PostgreSQL/Flyway environment configuration.
- [X] T021 Run `mvn test` and resolve any failures; verify the full suite includes patient unit and PostgreSQL integration tests.

## Dependencies and Execution Order

- Phase 1 → Phase 2 → US1 → US2 → US3 → Phase 6.
- T003 and T005 are required before every user story.
- US2 depends on US1 because it needs registered patient data; US3 depends on US1 because it updates registered patient data.
- Within each story, tests may be written first; API tasks depend on their application-service tasks; integration tasks depend on both.

## Parallel Opportunities

- T004 can proceed in parallel with T005 after T003.
- T006 and T008 can proceed in parallel after the foundational model is available.
- T011 can proceed in parallel with the query implementation design; T015 can begin once registration behavior is stable.

## Implementation Strategy

1. Complete setup and the PostgreSQL/Flyway foundation.
2. Deliver and verify patient registration as the MVP.
3. Add retrieval/search, then profile update.
4. Run the complete test suite and perform the security/maintainability review.

## Phase 7: Convergence

- [X] T022 Add a consistent `HttpMessageNotReadableException` mapping to `src/main/java/com/hospital/smartqueue/common/api/GlobalExceptionHandler.java` and endpoint tests for malformed date and unknown gender payloads per FR-007 and Constitution IV (partial).
- [X] T023 Extend `src/test/java/com/hospital/smartqueue/patient/integration/PatientMigrationIntegrationTest.java` to assert PostgreSQL rejects a duplicate `patient_number` per FR-001 (partial).
- [X] T024 Extend `src/test/java/com/hospital/smartqueue/patient/integration/PatientApiIntegrationTest.java` to cover patient-number and mobile search, no-match results, and pagination validation per US2/AC2 and Constitution V (partial).
- [X] T025 Extend `src/test/java/com/hospital/smartqueue/patient/integration/PatientApiIntegrationTest.java` to cover optional-field omission, invalid update rollback, persisted audit events, and updated timestamps per US1/AC2, US3/AC1, and Constitution V (partial).
