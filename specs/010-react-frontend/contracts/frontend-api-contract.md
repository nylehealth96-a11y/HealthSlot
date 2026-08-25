# Frontend API Consumption Contract

The client consumes versioned `/api/v1` endpoints only after their backend contracts are validated. It sends user-entered workflow data only; it never sends authority-bearing identity, role, hospital/branch scope, timing, availability, or state values.

Every client operation must handle `400` validation, `404` non-disclosing resource/scope denial, `409` conflict/unavailable, and unexpected failures without exposing data. Server-returned presentation/timezone values are displayed as-is.

Current valid action: none of the patient, appointment, slot, ETA, queue/check-in, or consultation contracts are implemented on this branch. The doctor API is administrative and is not yet a validated patient-search contract. All such actions remain disabled.
