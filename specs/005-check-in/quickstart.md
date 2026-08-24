# Check-In Validation Quickstart

## Prerequisites

1. Java 17, Maven, Docker/Testcontainers-capable PostgreSQL integration testing.
2. Integrated patient, doctor-scheduling, appointment-booking, branch-timezone, and audit capabilities, supplying the ports in [plan.md](./plan.md).
3. Test data for a timezoned branch, scoped patient/doctor, and eligible `BOOKED` appointment.
4. Tests/local verification alone may install `TestOnlyTrustedStaffContext`; production configuration keeps the deny-by-default adapter.

## Focused validation

Run `mvn "-Dtest=CheckInServiceTest,CheckInApiIntegrationTest,QueueEntryRepositoryIntegrationTest" test`.

Expected: first eligible appointment check-in is `201`; repeat is `200` with identical ID/reference; valid walk-in is `201` with `WALK_IN` and no appointment ID; invalid window/state/slot/duplicate requests add no row; scope and production fail-closed behaviour are non-disclosing.

## Persistence and concurrency

Run `mvn "-Dtest=QueueEntryRepositoryIntegrationTest,CheckInConcurrencyIntegrationTest" test`.

Expected: exactly one active entry survives concurrent duplicates; PostgreSQL enforces appointment-link, reference, and active-visit constraints; duplicate appointment check-in reloads its existing entry.

## Lifecycle, rollback, and performance validation

Run focused lifecycle/rollback validation using `mvn "-Dtest=AppointmentCheckInServiceTest,WalkInRegistrationServiceTest,CheckInConcurrencyIntegrationTest,WalkInConcurrencyIntegrationTest" test`. Expected: a `CANCELLED` or other non-`BOOKED` appointment conflicts; appointment queue-link mutation, queue row, and mandatory audit row roll back together on any injected failure; a changed slot at commit creates no walk-in; and simultaneous check-in/walk-in requests leave one scoped active entry.

Run `mvn "-Dtest=CheckInPerformanceIntegrationTest" test` in a local Docker-backed PostgreSQL Testcontainers environment. After fixture setup and JVM warm-up, run 100 valid sequential appointment check-ins and 100 valid sequential walk-ins against 100 patients, 10 doctors, and 200 eligible appointments; require p95 below 2 seconds and every operation below 5 seconds for each type. Then run 50 scoped UUID and 50 scoped reference reads at 10 concurrent callers against at least 200 entries with the same thresholds. This is a local regression budget, not a production-scale guarantee.

## Broader validation

Run `mvn test`, followed by `git diff --check`.

Expected: all existing module tests remain green and no whitespace errors are reported. See [check-in-api.md](./contracts/check-in-api.md) for requests/statuses and [data-model.md](./data-model.md) for invariants.
