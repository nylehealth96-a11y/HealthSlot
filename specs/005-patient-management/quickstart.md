# Quickstart: Patient Registration and Profile Management

## Prerequisites

- Java 17, Maven, PostgreSQL, and `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
- Docker running for Testcontainers integration tests.
- A trusted authenticated reception-staff context authorized for the target hospital.

## Validate the feature

1. Start the application and confirm Flyway applies the patient migration.
2. Use a hospital UUID as `{hospitalId}` and register a patient through `POST /api/v1/hospitals/{hospitalId}/patients`. Confirm `id`, `patientNumber`, and initial `version`.
3. Register another patient with the same mobile number. Confirm success with different identifiers.
4. Retrieve the first patient by UUID and patient number, then search by partial name and mobile number. Confirm hospital scoping and an empty result page for no match.
5. Update the patient with the returned version. Confirm contact changes persist and identifiers remain unchanged.
6. Repeat the update using the old version. Confirm `409 Conflict` and no overwrite.
7. Submit missing/invalid fields, a future birth date, malformed email/mobile number, and partial emergency contact. Confirm `400 Bad Request`.
8. Attempt cross-hospital retrieval/update. Confirm the patient is not exposed.
9. Inspect audit events for registration/update actions and confirm they contain no patient PII in metadata.

## Automated verification

```powershell
mvn test
```

The suite must cover migration validation, registration, duplicate-mobile handling, immutable identifiers, scoped search/retrieval, invalid input, audit creation, and stale-update rejection. See [data-model.md](data-model.md) and [contracts/patient-api.md](contracts/patient-api.md).
