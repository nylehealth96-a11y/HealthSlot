# Feature Specification: Patient Registration and Profile Management

**Feature Branch**: `002-patient-management`

**Created**: 2026-08-24

**Status**: Draft

**Input**: User description: "Implement patient registration and patient profile management."

## Clarifications

### Session 2026-08-24

- Q: Should a patient record be shared across all branches of the same hospital organization, or kept separate per branch? → A: One patient record shared across all branches of the same hospital organization.
- Q: What should happen if two staff members try to save changes to the same patient record at the same time? → A: Reject the later save and ask staff to refresh and review changes.
- Q: Which details should be required when staff provide an emergency contact? → A: Name, relationship, and mobile number.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Register a Patient (Priority: P1)

Reception staff register a new patient so the patient can be reliably identified for future care and administrative activity.

**Why this priority**: Registration is the foundation for every subsequent patient-management activity.

**Independent Test**: A reception staff member enters valid required details and receives a completed patient record with both identifiers.

**Acceptance Scenarios**:

1. **Given** reception staff are registering a patient, **When** they provide the required first name, last name, date of birth, gender, and mobile number, **Then** the system creates a patient record with a unique internal identifier and a human-readable patient number.
2. **Given** reception staff are registering a patient, **When** they provide email, address, and an emergency contact with name, relationship, and mobile number, **Then** those details are stored with the patient record.
3. **Given** required information is absent or invalid, **When** staff attempt to submit the registration, **Then** the system identifies the fields that require correction and does not create a patient record.
4. **Given** a mobile number is already associated with a patient, **When** staff register another patient with that number, **Then** the system does not use the mobile number to identify, overwrite, or prevent creation of the new patient record solely because it matches.

---

### User Story 2 - Retrieve and Search for a Patient (Priority: P2)

Reception staff find an existing patient by their identifiers or demographic details so they can work with the correct record.

**Why this priority**: Fast, accurate retrieval reduces duplicate records and supports safe patient administration.

**Independent Test**: After registration, staff can retrieve the patient directly and locate the patient through a search using supported identifying details.

**Acceptance Scenarios**:

1. **Given** a patient exists, **When** staff retrieve the patient using the internal identifier or patient number, **Then** the system displays that patient's stored profile information.
2. **Given** multiple patient records exist, **When** staff search using a patient number, name, or mobile number, **Then** the system returns the matching patient records and enough information to distinguish them.
3. **Given** no patient matches the search, **When** staff perform the search, **Then** the system clearly reports that no matching patient was found.

---

### User Story 3 - Update Basic Patient Information (Priority: P3)

Reception staff correct or refresh a patient's basic demographic and contact information while preserving the patient’s identity.

**Why this priority**: Contact and demographic details change and must remain accurate for safe communication and administration.

**Independent Test**: Staff retrieve a registered patient, change permitted basic information, and confirm the revised values appear on later retrieval.

**Acceptance Scenarios**:

1. **Given** a patient exists, **When** staff submit valid changes to the patient's basic information, **Then** the system saves and displays the updated information.
2. **Given** a patient exists, **When** staff update basic information, **Then** the internal identifier and patient number remain unchanged.
3. **Given** staff submit invalid required information during an update, **When** they attempt to save, **Then** the system identifies the fields that require correction and retains the previously stored valid information.
4. **Given** another staff member has saved changes after the current staff member opened the patient profile, **When** the current staff member attempts to save, **Then** the system rejects the save and asks them to refresh and review the current record.

### Edge Cases

- A patient has no email, address, or emergency contact: registration and retrieval still succeed, and those fields are shown as not provided.
- More than one patient shares the same name, date of birth, or mobile number: search returns all applicable records rather than treating any of those values as a unique identity.
- A patient number or internal identifier does not match a record: retrieval reports that no patient was found without exposing other records.
- Search text includes leading/trailing spaces or letter-case variations: matching is not unintentionally prevented by those variations.
- An update attempts to blank a required field or change a value to an invalid format: the update is rejected without partially replacing the existing record.
- Two staff members update the same patient at once: the later save is rejected and does not overwrite the already saved changes.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow authorized reception staff to register a patient for their hospital organization.
- **FR-002**: The system MUST create one unique internal identifier for each patient record and use it to distinguish that record from all other patient records.
- **FR-003**: The system MUST assign each patient a human-readable patient number that is unique among patient records.
- **FR-004**: The system MUST store a patient's first name, last name, date of birth, gender, and mobile number at registration.
- **FR-005**: The system MUST allow staff to store email and address when provided and an emergency contact when its name, relationship, and mobile number are provided.
- **FR-006**: The system MUST not use a mobile number as the patient record's primary identifier or require it to be unique.
- **FR-007**: The system MUST validate that required patient information is present and that date, email, and mobile-number values have valid formats before creating or updating a record.
- **FR-008**: The system MUST allow authorized staff to retrieve a patient in their hospital organization by the internal identifier and by the human-readable patient number.
- **FR-009**: The system MUST allow authorized staff to search patients in their hospital organization by patient number, name, and mobile number, returning all matching records.
- **FR-010**: The system MUST display sufficient patient information in search results for staff to distinguish records with similar details, including patient number, name, date of birth, and mobile number.
- **FR-011**: The system MUST allow authorized staff to update a patient's stored first name, last name, date of birth, gender, mobile number, email, address, and emergency-contact details.
- **FR-012**: The system MUST preserve a patient's internal identifier and patient number when their basic information is updated.
- **FR-013**: The system MUST prevent patient creation or update when supplied information fails validation and clearly identify the information that needs correction.
- **FR-014**: The system MUST reject a patient-profile update when the record has changed since staff opened it, retain the already saved changes, and ask staff to refresh and review the current record before retrying.
- **FR-015**: The system MUST record an audit trail for patient registration and basic-information updates, including the acting staff member and time of the action, without recording unnecessary sensitive details.
- **FR-016**: The feature MUST exclude appointment booking, appointment availability, and scheduling workflows.
- **FR-017**: The system MUST authorize every patient operation using trusted staff identity, reception-staff role, staff branch affiliation, the requested hospital, and the patient record's hospital ownership. Branch affiliation MAY grant access to patients only when that branch belongs to the requested hospital.
- **FR-018**: The system MUST use patient numbers in the `PAT-<globally-unique-uppercase-alphanumeric>` format; patient numbers are assigned at registration and never changed.
- **FR-019**: The system MUST return an authentication-required response to unauthenticated callers and a non-disclosing not-found response when an authenticated caller is not authorized for the requested hospital or patient.

### Key Entities *(include if feature involves data)*

- **Patient**: An individual registered by reception staff, identified by an internal unique identifier and a human-readable patient number; includes required demographic and contact details plus optional email, address, and emergency-contact details.
- **Emergency Contact**: Optional information for a person to reach in an emergency, consisting of their name, relationship to the patient, and mobile number.
- **Patient Profile Update**: A change to permitted demographic or contact details for an existing patient while retaining the patient's two identifiers.
- **Patient Audit Record**: An accountability record for registration or profile changes, identifying the action, acting staff member, and time.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In a documented usability evaluation of at least 20 registration attempts by at least 5 reception staff, at least 95% of valid patient registrations, including obtaining both identifiers, complete in under 3 minutes.
- **SC-002**: In the same documented usability evaluation, at least 95% of reception staff retrieve a known patient by either identifier on their first attempt.
- **SC-003**: In a representative test set of at least 1,000 patient records, at least 95% of searches by patient number, full or partial name, or mobile number return results within 2 seconds.
- **SC-004**: In acceptance testing, 100% of invalid registrations and profile updates are prevented from changing patient data and clearly identify the invalid or missing fields.
- **SC-005**: In acceptance testing, 100% of basic-information updates retain the patient's internal identifier and human-readable patient number.

## Assumptions

- Existing access controls provide trusted staff identity, reception-staff role, and affiliated branch information. This feature authorizes patient-management actions only when the staff member's role and branch affiliation allow access within the requested hospital organization.
- A patient has one shared record across all branches of the same hospital organization; staff with authorization at any of those branches can retrieve and manage that record.
- First name, last name, date of birth, gender, and mobile number are mandatory; email, address, and emergency-contact details are optional. When an emergency contact is provided, its name, relationship, and mobile number are all required.
- Gender is captured using the organization’s existing approved set of values; if none exists, staff can select female, male, non-binary, or prefer not to say.
- A patient number is staff-readable and assigned automatically; its displayed format follows existing organization conventions where available.
- Patient data retention follows existing organization policy; retention changes and patient deactivation are intentionally out of scope for this feature.
- Search supports partial, case-insensitive matching for names and ignores surrounding whitespace.
- This feature manages only patient registration and basic profile information; it does not add clinical records, consent management, patient merging, deletion, appointment booking, or scheduling.
- The feature follows the project’s established requirements for authorized access, input validation, auditing, and protection of personally identifiable information.
