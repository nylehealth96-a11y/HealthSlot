# Research: Patient Registration and Profile Management

## Decisions

### Hospital-scoped ownership with global patient numbers

**Decision**: Persist an immutable `hospital_id` on every patient. Scope retrieval, search, update, and authorization to that hospital. Keep `patient_number` globally unique and immutable.

**Rationale**: The clarified requirement shares a patient across branches of one hospital organization, while existing hospital-scoped APIs use the hospital as the tenancy boundary. The specification requires the patient number to be unique among patient records.

**Alternatives considered**: Branch-owned records were rejected because patients must be shared across branches. Per-hospital patient numbers were rejected because they weaken the specified uniqueness rule.

### Persisted optimistic concurrency

**Decision**: Store a patient record version and require the current version in a profile-update request. Reject an update with HTTP 409 when its version is stale.

**Rationale**: This implements the clarified rule that later concurrent saves must not overwrite already saved changes.

**Alternatives considered**: Last-write-wins and automatic field merging were rejected because they can silently lose or obscure patient-data changes.

### Search and emergency-contact design

**Decision**: Search only within the path hospital by patient number, normalized first/last-name text, or normalized mobile number. Model the single optional emergency contact as name, relationship, and mobile-number columns; require all three when any is supplied.

**Rationale**: This meets staff search needs, preserves hospital isolation, handles case and surrounding whitespace, and maintains a simple fixed contact shape.

**Alternatives considered**: Cross-hospital search and exact-name-only search violate isolation or usability. A separate contact table and unstructured text are unnecessary or prevent validation.

### Authorized actor and audit integration

**Decision**: Resolve authenticated staff identity and hospital authorization through a trusted application/security context. Record successful registration and profile updates through the existing audit service, with patient ID and action but no patient PII in metadata.

**Rationale**: The constitution requires authorization and auditability, but the current project has no authentication implementation and its audit service uses `SYSTEM`. Request-provided actor data would be forgeable.

**Alternatives considered**: Actor headers/request fields and omitted actor auditing were rejected as insecure or non-compliant.
