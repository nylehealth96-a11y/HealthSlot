# Implementation Plan: Dynamic Doctor ETA

**Branch**: `008-dynamic-ETA` | **Date**: 2026-08-24 | **Spec**: [spec.md](./spec.md)

## Summary

Create deterministic, versioned doctor ETA predictions from authoritative appointment schedules and consultation actual timing. Recalculate after timing changes and each one-minute active overrun; expose an in-scope operational view without patient PII.

## Technical Context

**Language/Version**: Java 17; **Dependencies**: Spring Boot, JPA, Flyway, PostgreSQL, JUnit/Testcontainers; **Storage**: UUIDs, instants, versioned ETA publications; **Testing**: unit, API, persistence, concurrency, failure injection; **Project**: modular-monolith REST service.

**Constraints**: `/api/v1`; trusted server-side staff scope; production fail-closed; no caller timing/override input; branch timezone; PII-minimized audit; no cross-module persistence access. One-minute refresh is invoked by a server-side scheduler/trigger only after active overrun; its idempotency key is doctor, branch, local minute, and timing revision. Missed ticks use latest inputs, stale revisions are discarded, and failed unpublished prediction/audit transactions are retried idempotently by the ETA service before terminal operational failure. Local performance validation uses 50 appointments, 20 warmed-up recalculations, p95 under 2 seconds, and excludes startup.

## Constitution Check

PASS subject to upstream prerequisites: appointment schedule/slot duration, consultation actual timing, branch timezone, trusted identity, and smart-queue consumer contracts are absent from this branch and must be integrated through ports, never duplicated. PostgreSQL is authoritative; concurrent publications use transactional/versioned state; audit and prediction persistence share a consistency boundary.

## Project Structure

```text
src/main/java/com/hospital/smartqueue/eta/{api,application,domain,infrastructure}/
src/test/java/com/hospital/smartqueue/eta/{api,application,infrastructure}/
src/main/resources/db/migration/V<next-contiguous>__create_eta_predictions.sql
specs/008-dynamic-eta/{research.md,data-model.md,quickstart.md,contracts/}
```

**Structure Decision**: The `eta` module owns prediction versions and calculation. It consumes intentional appointment, consultation, timezone, identity, audit, and queue ports. Select the Flyway version only after the integrated migration baseline is known.
