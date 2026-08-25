# Frontend Validation Guide

1. Install frontend dependencies only after the scaffold is created.
2. Run the development server and confirm patient, doctor, and reception/admin workspaces load with a visible test/local identity label.
3. Confirm every unavailable workflow is disabled and has no mock success path.
4. When an API capability is enabled, validate loading, empty, validation, conflict, unavailable, and denied states.
5. Repeat a pending mutation and verify the UI sends at most one request.
6. Confirm no patient contact, clinical content, or authorization scope is saved in browser storage or routine logs.

## Current validation

- `npm.cmd test`: PASS (6 files, 11 tests)
- `npm.cmd run build`: PASS
- `git diff --check`: PASS
- The SC-004 50-record warmed-browser p95 measurement remains blocked until at least one authoritative workflow API contract is enabled; no unavailable workflow is measured as a successful product flow.
