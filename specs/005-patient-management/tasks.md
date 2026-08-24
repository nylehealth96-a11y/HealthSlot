# Tasks: Patient Registration and Profile Management

**Input**: Design documents from `/specs/005-patient-management/`

**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [patient-api.md](contracts/patient-api.md), [quickstart.md](quickstart.md)

**Tests**: Required by the project constitution for important business rules and by the feature quickstart.

**Organization**: Tasks are grouped by user story so each increment can be implemented and validated independently after foundational work completes.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel with other tasks in its phase when their dependencies are satisfied.
- **[Story]**: Maps the task to a user story from the specification.

## Phase 1: Setup

**Purpose**: Establish the feature package and test layout.

- [ ] T001 Create patient feature package markers in `src/main/java/com/hospital/smartqueue/patient/{api,application,domain,infrastructure}/`
- [ ] T002 [P] Create patient unit-test package markers in `src/test/java/com/hospital/smartqueue/patient/{application,domain}/`
- [ ] T003 [P] Create patient integration-test fixture location in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java`

---

## Phase 2: Foundational Prerequisites

**Purpose**: Establish security, audit, persistence, and error-handling rules that block all patient stories.

**⚠️ CRITICAL**: Do not enable patient endpoints until trusted staff authentication and hospital-scoped authorization are available.

- [ ] T004 Define the trusted current-staff authorization abstraction, including role, branch affiliation, requested hospital, and patient ownership checks, in `src/main/java/com/hospital/smartqueue/common/security/PatientAccessContext.java`
- [ ] T005 Implement the production adapter that resolves authenticated staff identity and applies role, branch, hospital, and patient-ownership authorization in `src/main/java/com/hospital/smartqueue/common/security/AuthenticatedPatientAccessContext.java`
- [ ] T006 [P] Add authorization-context unit tests for missing identity, non-reception role, unauthorized branch, cross-hospital access, and patient-ownership mismatch in `src/test/java/com/hospital/smartqueue/common/security/PatientAccessContextTest.java`
- [ ] T007 Update audit recording to accept the trusted acting-staff reference without PII metadata in `src/main/java/com/hospital/smartqueue/common/infrastructure/AuditService.java`
- [ ] T008 [P] Add audit-service tests for trusted actor propagation and PII-free metadata in `src/test/java/com/hospital/smartqueue/common/infrastructure/AuditServiceTest.java`
- [ ] T009 Add stale-update conflict mapping to the global API-error contract in `src/main/java/com/hospital/smartqueue/common/api/GlobalExceptionHandler.java`
- [ ] T010 [P] Add API-error integration coverage for validation, unauthenticated 401, non-disclosing unauthorized 404, not-found, and stale-update outcomes in `src/test/java/com/hospital/smartqueue/integration/GlobalApiErrorIntegrationTest.java`
- [ ] T011 Create the forward-only patient schema migration, constraints, indexes, version column, and audit-compatible foreign keys in `src/main/resources/db/migration/V2__create_patients.sql`
- [ ] T012 [P] Add migration/schema assertions for patients, identifiers, and indexes in `src/test/java/com/hospital/smartqueue/integration/InfrastructureIntegrationTest.java`

**Checkpoint**: Trusted access, audit, errors, and storage are available; user-story work may begin.

---

## Phase 3: User Story 1 — Register a Patient (Priority: P1) 🎯 MVP

**Goal**: Authorized reception staff can register a hospital-scoped patient with immutable identifiers and validated required/optional details.

**Independent Test**: Register a valid patient for a hospital and receive a UUID, globally unique patient number, and initial version; invalid input changes no data and a duplicate mobile number remains valid.

### Tests for User Story 1

- [ ] T013 [P] [US1] Add patient-domain tests for identity immutability, optional emergency contact, and emergency-contact completeness in `src/test/java/com/hospital/smartqueue/patient/domain/PatientTest.java`
- [ ] T014 [P] [US1] Add registration-service tests for valid data, invalid data, duplicate mobile numbers, hospital scope, and audit action in `src/test/java/com/hospital/smartqueue/patient/application/PatientServiceTest.java`
- [ ] T015 [P] [US1] Add register-patient API integration coverage for `POST /api/v1/hospitals/{hospitalId}/patients` in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java`

### Implementation for User Story 1

- [ ] T016 [P] [US1] Implement gender and emergency-contact value types in `src/main/java/com/hospital/smartqueue/patient/domain/Gender.java` and `src/main/java/com/hospital/smartqueue/patient/domain/EmergencyContact.java`
- [ ] T017 [US1] Implement the Patient JPA aggregate with UUID, hospital scope, immutable patient number, normalized searchable fields, version, and timestamps in `src/main/java/com/hospital/smartqueue/patient/domain/Patient.java`
- [ ] T018 [US1] Implement hospital-scoped patient persistence and global patient-number lookup in `src/main/java/com/hospital/smartqueue/patient/infrastructure/PatientRepository.java`
- [ ] T019 [P] [US1] Define validated registration request and response DTOs in `src/main/java/com/hospital/smartqueue/patient/api/RegisterPatientRequest.java` and `src/main/java/com/hospital/smartqueue/patient/api/PatientResponse.java`
- [ ] T020 [US1] Implement transactional registration, patient-number generation, input normalization, trusted access enforcement, and PII-safe audit recording in `src/main/java/com/hospital/smartqueue/patient/application/PatientService.java`
- [ ] T021 [US1] Expose the authorized hospital-scoped registration endpoint in `src/main/java/com/hospital/smartqueue/patient/api/PatientController.java`

**Checkpoint**: User Story 1 is complete and independently demonstrable.

---

## Phase 4: User Story 2 — Retrieve and Search for a Patient (Priority: P2)

**Goal**: Authorized reception staff can retrieve a hospital patient by either identifier and search patients without exposing another hospital’s records.

**Independent Test**: Register patients for two hospitals, retrieve the first by UUID and patient number, search by partial name and mobile number, and confirm cross-hospital and no-match behavior.

### Tests for User Story 2

- [ ] T022 [P] [US2] Add retrieval and search service tests for hospital scope, identifier lookup, canonical matching, duplicate mobile matches, empty results, sorting, and pagination in `src/test/java/com/hospital/smartqueue/patient/application/PatientSearchServiceTest.java`
- [ ] T023 [P] [US2] Add retrieval/search API integration coverage for the GET patient and patient-number routes in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java`
- [ ] T024 [P] [US2] Add search API integration coverage for query normalization, empty pages, bounded size, and cross-hospital isolation in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java`

### Implementation for User Story 2

- [ ] T025 [US2] Add hospital-scoped UUID, patient-number, and paginated search queries in `src/main/java/com/hospital/smartqueue/patient/infrastructure/PatientRepository.java`
- [ ] T026 [P] [US2] Define patient-search summary and page response DTOs in `src/main/java/com/hospital/smartqueue/patient/api/PatientSummaryResponse.java` and `src/main/java/com/hospital/smartqueue/patient/api/PatientPageResponse.java`
- [ ] T027 [US2] Implement trusted access checks, retrieval by both identifiers, normalized search, deterministic ordering, and bounded pagination in `src/main/java/com/hospital/smartqueue/patient/application/PatientService.java`
- [ ] T028 [US2] Expose hospital-scoped retrieval and search endpoints in `src/main/java/com/hospital/smartqueue/patient/api/PatientController.java`

**Checkpoint**: User Stories 1 and 2 work independently; retrieval and search honor hospital boundaries.

---

## Phase 5: User Story 3 — Update Basic Patient Information (Priority: P3)

**Goal**: Authorized reception staff can update basic profile data while patient identifiers remain immutable and stale concurrent updates are rejected.

**Independent Test**: Update a registered patient with its current version, then repeat with the prior version and confirm a conflict with no lost data.

### Tests for User Story 3

- [ ] T029 [P] [US3] Add profile-update service tests for validation, immutable identifiers, emergency-contact rules, audit action, and version conflicts in `src/test/java/com/hospital/smartqueue/patient/application/PatientUpdateServiceTest.java`
- [ ] T030 [P] [US3] Add update API integration coverage for valid changes, invalid fields, cross-hospital access, and identifier preservation in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java`
- [ ] T031 [P] [US3] Add concurrent stale-version integration coverage proving the later save returns conflict without overwriting data in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java`

### Implementation for User Story 3

- [ ] T032 [P] [US3] Define a validated versioned profile-update request DTO in `src/main/java/com/hospital/smartqueue/patient/api/UpdatePatientRequest.java`
- [ ] T033 [US3] Add patient aggregate operations that change only permitted profile fields and enforce emergency-contact invariants in `src/main/java/com/hospital/smartqueue/patient/domain/Patient.java`
- [ ] T034 [US3] Implement transactional authorized profile update, stale-version rejection, and PII-safe audit recording in `src/main/java/com/hospital/smartqueue/patient/application/PatientService.java`
- [ ] T035 [US3] Expose the versioned hospital-scoped profile-update endpoint in `src/main/java/com/hospital/smartqueue/patient/api/PatientController.java`

**Checkpoint**: All three user stories are independently functional and patient identity is preserved through updates.

---

## Phase 6: Polish and Cross-Cutting Validation

**Purpose**: Complete requirements traceability, privacy review, performance checks, and end-to-end validation.

- [ ] T036 [P] Review and resolve relevant unchecked PR-review requirements items in `specs/005-patient-management/checklists/api-security.md`
- [ ] T037 [P] Update patient API examples and validation notes in `specs/005-patient-management/contracts/patient-api.md`
- [ ] T038 Add performance-oriented search integration coverage using 1,000 representative patients in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java`
- [ ] T039 Conduct and document the 20-attempt, 5-staff usability evaluation for registration and retrieval criteria in `specs/005-patient-management/quickstart.md`
- [ ] T040 Run `mvn test` and resolve patient-feature regressions in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java`

---

## Dependencies and Execution Order

### Phase Dependencies

- Phase 1 has no dependencies.
- Phase 2 depends on Phase 1 and blocks all user stories.
- US1 depends on Phase 2.
- US2 depends on Phase 2 and on US1’s patient aggregate/repository/service foundations (T017–T020).
- US3 depends on Phase 2 and on US1’s patient aggregate/repository/service foundations (T017–T020).
- Phase 6 depends on the desired user stories being complete.

### User Story Completion Order

`Setup → Foundational → US1 (MVP) → US2 and US3 → Polish`

US2 and US3 may proceed in parallel after US1’s shared patient foundation completes, provided changes to `PatientService`, `PatientController`, and `PatientApiIntegrationTest` are coordinated.

### Parallel Opportunities

- T002–T003 can run in parallel after T001.
- T006, T008, T010, and T012 can run in parallel with their corresponding foundational implementation once the target contract is stable.
- T013–T015 and T016/T019 are parallel work in US1.
- T022–T024 and T026 are parallel work in US2.
- T029–T032 are parallel work in US3.
- T036–T037 can run in parallel in Phase 6.

## Implementation Strategy

### MVP First

1. Complete setup and all foundational trusted-access, audit, migration, and error work.
2. Complete US1, including its tests, and validate registration independently.
3. Demo registration only after authorization, audit, and duplicate-mobile behavior are proven.

### Incremental Delivery

1. Add US2 retrieval and search after the registration foundation is stable.
2. Add US3 versioned profile updates after retrieval supports staff refresh/review flows.
3. Finish privacy, requirement-review, performance, quickstart, and full-suite validation.

## Phase 7: Convergence

- [ ] T041 CRITICAL Complete trusted test-only staff context and fail-closed production authorization per FR-001 and Constitution IV in `src/test/java/com/hospital/smartqueue/common/security/TestPatientAccessContext.java` (missing)
- [ ] T042 Implement patient registration, validation, patient-number generation, and audit recording per US1/FR-002–FR-007 in `src/main/java/com/hospital/smartqueue/patient/application/PatientService.java` (missing)
- [ ] T043 Implement registration request/response DTOs and hospital-scoped controller endpoint per US1 in `src/main/java/com/hospital/smartqueue/patient/api/PatientController.java` (missing)
- [ ] T044 Implement hospital-scoped retrieval and paginated normalized search per US2/FR-008–FR-010 in `src/main/java/com/hospital/smartqueue/patient/infrastructure/PatientRepository.java` (missing)
- [ ] T045 Implement retrieval/search DTOs and endpoints per US2 in `src/main/java/com/hospital/smartqueue/patient/api/PatientController.java` (missing)
- [ ] T046 Implement versioned profile updates, immutable identifiers, and stale-write rejection per US3/FR-011–FR-014 in `src/main/java/com/hospital/smartqueue/patient/application/PatientService.java` (missing)
- [ ] T047 Implement update DTO and endpoint per US3 in `src/main/java/com/hospital/smartqueue/patient/api/UpdatePatientRequest.java` (missing)
- [ ] T048 Update trusted actor auditing and global 401/404/409 error mapping per FR-015, FR-019, and Constitution IV in `src/main/java/com/hospital/smartqueue/common/api/GlobalExceptionHandler.java` (partial)
- [ ] T049 Add patient domain, service, API, migration, isolation, and stale-update automated tests per Constitution V in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java` (missing)

## Phase 8: Convergence

- [ ] T050 CRITICAL Wire a test-only trusted staff context and retain fail-closed production access per FR-001/FR-017 in `src/test/java/com/hospital/smartqueue/common/security/TestPatientAccessContext.java` (missing)
- [ ] T051 Complete hospital-scoped patient retrieval and paginated search per FR-008–FR-010 in `src/main/java/com/hospital/smartqueue/patient/application/PatientService.java` (missing)
- [ ] T052 Complete patient retrieval/search endpoints and DTOs per US2 in `src/main/java/com/hospital/smartqueue/patient/api/PatientController.java` (missing)
- [ ] T053 Complete versioned profile update behavior and stale-write conflict handling per FR-011–FR-014 in `src/main/java/com/hospital/smartqueue/patient/application/PatientService.java` (missing)
- [ ] T054 Complete audit actor propagation and standard 401/404/409 responses per FR-015/FR-019 in `src/main/java/com/hospital/smartqueue/common/api/GlobalExceptionHandler.java` (missing)
- [ ] T055 Add automated patient registration, search, update, scope, and concurrency coverage per Constitution V in `src/test/java/com/hospital/smartqueue/integration/PatientApiIntegrationTest.java` (missing)
