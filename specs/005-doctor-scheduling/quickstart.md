# Quickstart: Validate Doctor Scheduling

## Prerequisites

- Java 17 and Maven.
- Docker available for Testcontainers.
- A PostgreSQL database when running the application outside tests; set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.

## Automated validation

Run:

```powershell
mvn test
```

The scheduling tests must use `TrustedTestSchedulingAccessContext` through test configuration only. They must prove that the production access context rejects requests until a real staff-identity adapter is supplied.

## Acceptance scenarios

1. Create hospital, timezone-enabled branch, department, and active doctor assigned to that branch through its department.
2. With trusted test staff authorized for that hospital/branch, save a Monday–Friday 09:00–17:00 schedule, 30-minute duration, and 12:30–13:00 break. Verify Monday slots omit the break and omit an incomplete final interval.
3. Save a future effective revision and verify dates before it use the earlier revision while dates on/after it use the new revision.
4. Add leave spanning two dates and verify no slots are returned for either date.
5. Add an exception with special periods and verify it replaces the recurring schedule. Add an empty exception and verify that day is closed.
6. Attempt overlapping periods, breaks, leave, and exceptions; verify a validation/conflict error and that prior rules persist unchanged.
7. Submit a stale version after another update; verify `409 STALE_REVISION` and the latest revision is unchanged.
8. Query up to 31 days and verify ordered deterministic slots, branch-local timezone conversion, precedence `leave > exception > recurring`, and p95 under two seconds.

See [data-model.md](./data-model.md) for rule derivation and [doctor-scheduling-api.md](./contracts/doctor-scheduling-api.md) for endpoint payloads and errors.
