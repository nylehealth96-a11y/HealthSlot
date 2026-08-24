# Data Model: Patient Registration and Profile Management

## Patient

| Field | Type / rule |
|---|---|
| `id` | UUID primary key; immutable internal identifier |
| `hospital_id` | Required immutable UUID reference to `hospitals`; tenancy and authorization boundary |
| `patient_number` | Required immutable human-readable identifier; globally unique |
| `first_name`, `last_name` | Required trimmed display text; canonical normalized copies support name search |
| `date_of_birth` | Required calendar date; must not be in the future |
| `gender` | Required approved value: `FEMALE`, `MALE`, `NON_BINARY`, or `PREFER_NOT_TO_SAY` |
| `mobile_number` | Required normalized contact number; non-unique and not an identifier |
| `email`, `address` | Optional; email must be valid when supplied |
| `emergency_contact_name`, `emergency_contact_relationship`, `emergency_contact_mobile_number` | Optional together; all three required when any is supplied |
| `version` | Required optimistic-concurrency version; changes on every successful update |
| `created_at`, `updated_at` | Required UTC instants |

### Invariants

- A patient belongs to exactly one hospital and is accessible to its authorized branches through that shared hospital record.
- `id`, `hospital_id`, and `patient_number` cannot be changed after registration.
- A mobile number may occur on any number of patient records.
- Emergency-contact fields are either all present or all absent.
- A profile update includes the current persisted `version`; a stale version is rejected without changing data.

### Search representation and indexes

- Store canonical first and last names using the project’s trim-and-lowercase convention and a normalized mobile number for search.
- Index `(hospital_id, patient_number)`, `(hospital_id, canonical_last_name, canonical_first_name)`, and `(hospital_id, normalized_mobile_number)`.
- Apply a global unique constraint to `patient_number` and a foreign key from `hospital_id` to `hospitals`.

## Audit Event

Use existing `audit_events` for successful registration and updates: `PATIENT_REGISTERED` or `PATIENT_PROFILE_UPDATED`, target type `PATIENT`, patient ID, hospital ID, trusted actor reference, and minimal PII-free metadata.

## State Transitions

`New` → `Registered` on valid registration. `Registered(version n)` → `Registered(version n+1)` after a valid current-version update. Deletion, merging, clinical records, and appointment states are out of scope.
