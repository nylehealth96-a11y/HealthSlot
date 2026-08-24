# Doctor Consultation Lifecycle Validation Guide

## Prerequisites

- Integrate the authoritative patient, check-in queue-entry, queue recalculation, branch-timezone, and trusted-identity contracts.
- Configure the PostgreSQL test environment used by the project.
- Use the clearly named test-only trusted staff context; do not use it as production authentication.

## Focused Validation

1. Seed a check-in-created `WAITING` consultation for a doctor and branch.
2. As in-scope reception staff, call it and verify `CALLED`, one audit record, and no direct creation path.
3. As the assigned doctor, start it and verify `IN_CONSULTATION` plus one persisted `actualStartAt`.
4. Complete it as the assigned doctor and verify `COMPLETED`, a valid `actualEndAt`, audit persistence, and the queue-recalculation hand-off.
5. Repeat each action concurrently and verify exactly one transition succeeds.
6. Attempt the actions with an out-of-scope identity, wrong role, or unassigned doctor and verify non-disclosing failure with no mutation.
7. Inject queue hand-off/audit persistence failure and verify no partial completion is visible, or that the durable recovery contract is recorded and exercised.

## Commands

Run the focused consultation tests first, then the complete test suite:

```powershell
mvn test -Dtest=ConsultationLifecycleServiceTest,ConsultationLifecycleApiIntegrationTest,ConsultationLifecyclePersistenceIntegrationTest
mvn test
git diff --check
```

Expected result: valid lifecycle calls pass, invalid or out-of-scope transitions fail safely, exactly one concurrent transition persists, actual timing is consistent, and completed consultations hand off queue recalculation reliably.
