# Appointment Booking Validation Guide

## Prerequisites

- Java 17 and Maven
- Docker available for PostgreSQL Testcontainers
- A database configuration for local application runs: `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`
- Migrations/features that provide hospital, branch timezone, patient, doctor, and doctor schedule data applied before appointment tests or manual verification

## Automated validation

Run the focused module tests first:

```powershell
mvn "-Dtest=AppointmentServiceTest,AppointmentApiIntegrationTest" test
```

Then run the broader suite:

```powershell
mvn test
```

Expected results include:

1. A trusted test staff fixture can view branch-scoped slots and create a `BOOKED` appointment.
2. Two simultaneous same-slot booking requests produce exactly one `201` and one `409`; the database contains one `BOOKED` appointment.
3. Cancelling a current-version booked appointment changes it to `CANCELLED` and makes its slot available.
4. A successful reschedule retains the appointment number; a conflicting replacement leaves the original interval booked.
5. Invalid requests, past slots, stale versions, invalid transitions, and out-of-scope reads have the contract-defined error outcome.
6. Production configuration without an authentication integration rejects appointment endpoints; request headers/body/query data cannot impersonate staff.

## Manual API smoke test

Start the service after loading prerequisite data:

```powershell
mvn spring-boot:run
```

Use the availability endpoint from [appointment-api.md](contracts/appointment-api.md) to select an exact returned interval, then create an appointment using that interval. Verify the response number and status. Repeat the request concurrently to verify one conflict response. Manual production calls are expected to fail closed until real authentication is integrated; the trusted context is test-only and must not be exposed as a runtime setup path.
