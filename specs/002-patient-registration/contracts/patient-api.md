# Patient API Contract

- `POST /api/v1/patients`: register a patient.
- `GET /api/v1/patients/{id}`: retrieve by internal identifier.
- `GET /api/v1/patients/patient-number/{patientNumber}`: retrieve by patient number.
- `GET /api/v1/patients?query=`: search by patient number, name, or mobile number.
- `PUT /api/v1/patients/{id}`: update basic details while retaining identifiers.

All invalid input uses the global API error response.
