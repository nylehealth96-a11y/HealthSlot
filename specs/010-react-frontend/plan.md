# Implementation Plan: Initial Role-Based Frontend

**Branch**: `010-react-frontend` | **Date**: 2026-08-24 | **Spec**: [spec.md](./spec.md)

## Summary

Create `frontend/` as a React, TypeScript, and Vite application that provides patient, doctor, and reception/admin workspaces. It consumes validated backend APIs only; unavailable workflow APIs are rendered as disabled, non-sensitive states.

## Technical Context

**Language/Version**: TypeScript with React and Vite; **Testing**: component/unit and browser/API-contract tests; **Target**: modern desktop browser; **Project**: frontend within a Java modular-monolith repository.

**Constraints**: No browser authority for identity, role, scope, timing, availability, or state. Test/local trusted identity is visibly labelled, development-only, and replaceable. No sensitive browser storage/logging. API results control state changes and displayed timezone values. Prevent repeat submits while a request is pending. Support approved evergreen desktop browsers and keyboard/assistive-technology interaction. Read-only retries are bounded; mutations are never automatically retried.

**Available backend foundation**: current branch exposes hospital, department, and doctor APIs only. Patient, appointment, slot, ETA, queue/check-in, consultation, branch-timezone, and real authentication contracts are absent. These workflows must be disabled rather than mocked.

## Constitution Check

PASS. The client is a separate UI adapter and does not duplicate business rules. Backend remains the PostgreSQL, authorization, timezone, audit, and state-transition authority. No new infrastructure or secret is required.

## Project Structure

```text
frontend/
  src/{app,api,auth,features/{patient,doctor,reception},components}/
  src/test/
  package.json
  vite.config.ts
specs/010-react-frontend/{research.md,data-model.md,contracts/,quickstart.md}
```

## API Readiness Matrix

| Workspace action | Current state | UI behavior |
|---|---|---|
| Doctor search | Partial doctor API foundation | Wire only after scoped public/search contract is validated |
| Slots, booking, ETA | Absent | Disabled |
| Doctor queue/actions | Absent | Disabled |
| Patient registration/check-in/walk-in | Absent | Disabled |

## Complexity Tracking

No constitution violations.
