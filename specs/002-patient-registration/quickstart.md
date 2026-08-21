# Quickstart: Patient Registration

1. Supply database credentials through environment variables; for example, set `DB_URL`,
   `DB_USERNAME`, and `DB_PASSWORD`. Do not place credentials in source-controlled files.
2. Start PostgreSQL and run `mvn spring-boot:run`. Flyway applies the patient migration automatically.
3. Register a patient:

   ```bash
   curl -X POST http://localhost:8080/api/v1/patients \
     -H "Content-Type: application/json" \
     -d '{"firstName":"Ada","lastName":"Lovelace","dateOfBirth":"1990-01-01","gender":"FEMALE","mobileNumber":"+919876543210"}'
   ```

4. Retrieve a returned patient by UUID at `GET /api/v1/patients/{id}`, or by number at
   `GET /api/v1/patients/patient-number/{patientNumber}`. Search with
   `GET /api/v1/patients?query=Ada&page=0&size=20`.
5. Replace editable basic details with `PUT /api/v1/patients/{id}`. The UUID and patient number are response-only
   and remain unchanged.
6. Run `mvn test` to validate patient business rules and PostgreSQL-backed migration/API behavior.
