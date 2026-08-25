# Tasks: Initial Role-Based Frontend

**Input**: Design documents from `/specs/010-react-frontend/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

## Phase 1: Setup

- [x] T001 Initialize the `frontend/` React/TypeScript/Vite project with scripts and dependency metadata in `frontend/package.json` and `frontend/vite.config.ts`.
- [x] T002 [P] Create frontend source layout under `frontend/src/{app,api,auth,features/{patient,doctor,reception},components}/`.
- [x] T003 [P] Configure frontend unit/component test tooling and browser test entry points in `frontend/src/test/` and `frontend/package.json`.

## Phase 2: Foundational

- [x] T004 Create the typed API client, versioned endpoint configuration, and standard `400/404/409/unexpected` error model in `frontend/src/api/ApiClient.ts` and `frontend/src/api/apiError.ts`.
- [x] T005 Create the capability map and disabled-workflow explanation model in `frontend/src/api/capabilities.ts` based on the plan API readiness matrix.
- [x] T006 Create the clearly labelled development-only trusted identity adapter and production-build guard in `frontend/src/auth/TestOnlyIdentity.ts` and `frontend/src/auth/identity.ts`.
- [x] T007 Create shared workflow state, pending-action guard, role workspace shell, and non-sensitive error/status components in `frontend/src/app/WorkflowState.ts`, `frontend/src/components/RoleWorkspace.tsx`, and `frontend/src/components/AsyncState.tsx`.
- [x] T008 [P] Add foundational API/error, capability-gating, identity-label, pending-submit, and sensitive-storage requirements tests in `frontend/src/test/foundation.test.tsx`.

## Phase 3: User Story 1 - Patient Appointment Journey (Priority: P1) 🎯 MVP

**Goal**: Provide the patient workspace and wire only validated patient APIs; unavailable actions stay disabled.

**Independent Test**: Patient workspace clearly labels local identity, supports any available doctor capability, and safely disables absent slots/booking/ETA workflows.

- [x] T009 [P] [US1] Define patient API request/response types and capability wiring in `frontend/src/features/patient/patientApi.ts`.
- [x] T010 [P] [US1] Add requirements-focused patient workspace tests for search, empty slots, unavailable API, conflict, and ETA privacy states in `frontend/src/test/patientWorkspace.test.tsx`.
- [x] T011 [US1] Implement patient workspace navigation, doctor search, slot view, booking, and ETA screens with capability-gated disabled states in `frontend/src/features/patient/PatientWorkspace.tsx`.
- [x] T012 [US1] Implement pending-submit prevention and authoritative-result refresh for patient mutations in `frontend/src/features/patient/patientActions.ts`.

## Phase 4: User Story 2 - Doctor Consultation Workspace (Priority: P1)

**Goal**: Provide the doctor workspace while queue and consultation APIs remain disabled until validated.

**Independent Test**: Doctor sees only permitted operational data and disabled queue/call/start/complete actions when contracts are absent.

- [x] T013 [P] [US2] Define doctor queue and consultation API types/capabilities in `frontend/src/features/doctor/doctorApi.ts`.
- [x] T014 [P] [US2] Add doctor workspace tests for disabled capabilities, scope-denied responses, state conflicts, and minimum operational fields in `frontend/src/test/doctorWorkspace.test.tsx`.
- [x] T015 [US2] Implement doctor workspace queue, call, start-consultation, and complete-consultation screens with capability gating in `frontend/src/features/doctor/DoctorWorkspace.tsx`.
- [x] T016 [US2] Implement authoritative state refresh and transition-action guards in `frontend/src/features/doctor/doctorActions.ts`.

## Phase 5: User Story 3 - Reception Operations Workspace (Priority: P1)

**Goal**: Provide reception/admin operations with unavailable registration, booking, check-in, and walk-in actions visibly disabled.

**Independent Test**: Reception workspace exposes only validated capabilities and shows safe validation/conflict/denied states.

- [x] T017 [P] [US3] Define reception API types and capability wiring in `frontend/src/features/reception/receptionApi.ts`.
- [x] T018 [P] [US3] Add reception workspace tests for disabled workflows, validation/conflict states, duplicate-submit prevention, and role-scoped data in `frontend/src/test/receptionWorkspace.test.tsx`.
- [x] T019 [US3] Implement reception/admin workspace for registration, booking, check-in, and walk-in flows with disabled-state gating in `frontend/src/features/reception/ReceptionWorkspace.tsx`.
- [x] T020 [US3] Implement authoritative-result refresh and safe form/action state handling in `frontend/src/features/reception/receptionActions.ts`.

## Phase 6: Polish and Cross-Cutting Validation

- [ ] T021 [P] Add keyboard navigation, focus, labels, semantic status announcements, responsive layout, and accessible disabled-state requirements coverage in `frontend/src/test/accessibility.test.tsx` and `frontend/src/components/`.
- [ ] T022 [P] Add browser storage/logging privacy checks and test/local identity production-exclusion checks in `frontend/src/test/security.test.tsx`.
- [x] T023 Add API contract fixtures for each newly validated backend capability without inventing unavailable endpoints in `frontend/src/test/contracts/`.
- [ ] T024 Run frontend tests, accessibility/security checks, build, and 50-record warmed-session p95 performance validation (2-second threshold), then run quickstart validation and `git diff --check`; record results in `specs/010-react-frontend/quickstart.md`.

## Dependencies and Execution Order

- Phase 2 blocks workspace implementation.
- US1, US2, and US3 can proceed in parallel after the foundation, but each capability remains disabled until its backend contract is validated.
- Tests precede their corresponding workspace implementation.
- Phase 6 follows the desired workspace increments.

## MVP Strategy

Complete setup/foundation and the patient workspace first. Enable only APIs actually available, validate disabled states for all other workflows, then incrementally wire doctor and reception capabilities as backend contracts arrive.

## Phase 7: Convergence

- [x] T025 Implement bounded timeout and at most two transient retries for read-only `ApiClient` requests while ensuring state-changing requests are never automatically retried, with focused tests per FR-017 (partial).
- [ ] T026 Add shared role-specific field allowlists, authoritative response types, and one error/status mapping used by all workspaces, with privacy and omitted-field tests per FR-018, FR-019, and FR-022 (missing).
- [x] T027 Replace the state-only `isPending` helper with a reusable pending-submit guard that prevents duplicate mutation dispatches and add repeated-submit tests per FR-006 (partial).
- [ ] T028 Add a browser-capable accessibility/support validation entry point covering keyboard focus, labels, status announcements, and approved evergreen browser assumptions per FR-014 and FR-015 (missing).
