# Quickstart: Hospital Structure Management

## Prerequisites

- Java 17 and Maven.
- PostgreSQL credentials from environment variables or external configuration.

## Run and validate

1. Start the service with `mvn spring-boot:run`; Flyway migrations must succeed.
2. Run `mvn clean test` for unit and PostgreSQL-backed integration tests.
3. Use [contracts/hospital-structure-api.yaml](contracts/hospital-structure-api.yaml) to create a
   hospital, two branches, departments, and a doctor assigned across those branches.
4. Verify hospital- and department-scoped doctor lists; attempt unrelated hospital IDs and verify
   no data is shown or changed.
5. Attempt case/whitespace duplicate values and verify a common conflict response with no record.
6. Deactivate then reactivate the doctor; verify retained memberships and audit history.

See [data-model.md](data-model.md) for ownership and lifecycle rules.
