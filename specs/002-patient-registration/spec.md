# Feature Specification: Patient Registration

**Feature Branch**: `002-patient-registration`
**Created**: 2026-08-20
**Status**: Draft
**Input**: User description: "Implement patient registration and patient profile management."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Register a Patient (Priority: P1)

Reception staff register a patient and receive a unique patient profile with both an internal
identifier and a human-readable patient number.

**Why this priority**: Registration is the essential patient-record foundation.
**Independent Test**: Register a patient with required details and retrieve the returned profile.

**Acceptance Scenarios**:

1. **Given** required details are available, **When** staff register a patient, **Then** a profile
   is created with a unique internal identifier and patient number.
2. **Given** optional contact information is omitted, **When** staff register a patient, **Then**
   the profile is created without those optional details.

---

### User Story 2 - Find a Patient (Priority: P2)

Reception staff retrieve a known patient or search for patients to find the correct profile.

**Why this priority**: Staff must safely identify an existing profile before future workflows.
**Independent Test**: Search by patient number, name, and mobile number and retrieve a result.

**Acceptance Scenarios**:

1. **Given** a patient exists, **When** staff retrieve by internal identifier or patient number,
   **Then** the complete profile is returned.
2. **Given** several patients exist, **When** staff search by name or mobile number, **Then**
   matching profiles are returned without unrelated profiles.

---

### User Story 3 - Update Basic Details (Priority: P3)

Reception staff correct a patient's basic profile information without changing identity.

**Why this priority**: Accurate contact and demographic details support later care workflows.
**Independent Test**: Update a patient name or contact detail and retrieve the same identifiers.

**Acceptance Scenarios**:

1. **Given** a patient exists, **When** staff update permitted basic details, **Then** the profile
   reflects the new details and retains its internal identifier and patient number.

### Edge Cases

- Required first name, last name, date of birth, gender, and mobile number cannot be blank.
- Mobile number is searchable but is never the primary identifier and is not required to be unique.
- Invalid date of birth, gender, mobile number, or provided email is rejected without changes.
- A search with no matches returns an empty result.
- Appointment booking is not provided by this feature.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow reception staff to register a patient with a unique internal
  identifier and human-readable patient number.
- **FR-002**: Patient registration MUST require first name, last name, date of birth, gender, and
  mobile number; email, address, and emergency contact are optional.
- **FR-003**: The mobile number MUST NOT be used as the patient's database primary key.
- **FR-004**: The system MUST allow staff to retrieve a patient by internal identifier or patient number.
- **FR-005**: The system MUST allow staff to search patients by patient number, name, or mobile number.
- **FR-006**: The system MUST allow staff to update basic patient information while retaining both identifiers.
- **FR-007**: The system MUST validate input and reject invalid registrations or updates without persisting changes.
- **FR-008**: The system MUST audit patient registration and profile updates without unnecessary medical information in logs.
- **FR-009**: Appointment booking is outside this feature's scope.

### Key Entities *(include if feature involves data)*

- **Patient**: A person registered by reception staff, with internal identifier, patient number,
  demographic data, contact details, and audit timestamps.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Staff can register a patient in under 2 minutes when required details are available.
- **SC-002**: 100% of acceptance-test profiles retain their identifiers after updates.
- **SC-003**: 100% of invalid patient inputs are rejected without creating or changing a profile.
- **SC-004**: At least 90% of representative reception staff can find an existing profile on the first attempt.

## Assumptions

- Patient numbers are system-generated, human-readable, and globally unique.
- A mobile number may be shared by multiple patients, such as family members.
- Authentication and authorization are outside scope; "staff" identifies the intended actor only.
