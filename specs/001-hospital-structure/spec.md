# Feature Specification: Hospital Structure Management

**Feature Branch**: `001-hospital-structure`

**Created**: 2026-08-20

**Status**: Draft

**Input**: User description: "Build the hospital structure management foundation."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Establish Hospital Structure (Priority: P1)

An administrator creates a hospital, adds its branches, and creates the departments that operate
within each branch so the organization has an accurate service structure.

**Why this priority**: Hospitals, branches, and departments are the foundation required before
staff or later operational workflows can be managed.

**Independent Test**: Create one hospital with two branches and departments, then view the
hospital's branches and each branch's departments to confirm the complete hierarchy.

**Acceptance Scenarios**:

1. **Given** no matching hospital exists, **When** an administrator creates a hospital with its
   required details, **Then** the hospital is recorded and appears when hospitals are viewed.
2. **Given** a hospital exists, **When** an administrator creates a branch for that hospital,
   **Then** the branch appears only in that hospital's branch list.
3. **Given** a branch exists, **When** an administrator creates a department for that branch,
   **Then** the department appears in that branch's department list.
4. **Given** a branch belongs to one hospital, **When** an administrator attempts to create a
   department by pairing it with a different hospital, **Then** the request is rejected and no
   department is created.

---

### User Story 2 - Register and Find Doctors (Priority: P2)

An administrator registers a doctor within a hospital and links the doctor to one or more of
that hospital's departments, then finds doctors in the hospital or by department.

**Why this priority**: A reliable doctor directory is the next essential building block after
the hospital structure is established.

**Independent Test**: Register a doctor with two valid departments in one hospital, view the
hospital's doctors, and view the doctor through each associated department.

**Acceptance Scenarios**:

1. **Given** a hospital and its departments exist, **When** an administrator registers a doctor
   with a doctor code, name, specialization, professional registration number, and one or more
   of the hospital's departments, **Then** the doctor is recorded as active and is visible in the
   hospital's doctor list and each selected department's doctor list.
2. **Given** a department belongs to a different hospital, **When** an administrator attempts to
   associate it with a doctor, **Then** the request is rejected and the association is not made.
3. **Given** doctors exist in different departments, **When** an administrator views doctors for
   one department, **Then** only doctors associated with that department are shown.

---

### User Story 3 - Maintain Doctor Availability Status (Priority: P3)

An administrator activates or deactivates a registered doctor so the directory accurately shows
whether the doctor is currently active.

**Why this priority**: Status maintenance is necessary to keep the foundational directory
accurate, but depends on a registered doctor existing.

**Independent Test**: Register a doctor, deactivate the doctor, verify the inactive status in a
doctor view, then reactivate the doctor and verify the active status.

**Acceptance Scenarios**:

1. **Given** an active doctor exists, **When** an administrator deactivates the doctor, **Then**
   the doctor remains in the directory with inactive status.
2. **Given** an inactive doctor exists, **When** an administrator activates the doctor, **Then**
   the doctor remains in the directory with active status.
3. **Given** a doctor belongs to one hospital, **When** an administrator attempts to change that
   doctor's status through another hospital, **Then** the request is rejected and the status is
   unchanged.

---

### Edge Cases

- A hospital, branch, department, or doctor cannot be created when any required identifying or
  descriptive detail is missing or blank.
- A branch cannot be viewed as belonging to a hospital other than its recorded hospital.
- A department cannot be viewed as belonging to a branch other than its recorded branch.
- A doctor registration with no department, a duplicate doctor code in the same hospital, or a
  duplicate professional registration number in the same hospital is rejected.
- A doctor may be linked only to departments belonging to the doctor's hospital, including
  departments at different branches of that hospital.
- Repeating a request to set a doctor to its current active or inactive status leaves the status
  unchanged and returns the current doctor details.
- This feature does not delete hospitals, branches, departments, doctors, or doctor-department
  associations.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow administrators to create and view hospitals.
- **FR-002**: The system MUST allow administrators to create branches for a hospital and view
  only the branches belonging to a selected hospital.
- **FR-003**: Each branch MUST belong to exactly one hospital, and the system MUST reject any
  request that represents a branch as belonging to an unrelated hospital.
- **FR-004**: The system MUST allow administrators to create departments for a branch and view
  only the departments belonging to a selected branch.
- **FR-005**: Each department MUST belong to exactly one branch. A department's hospital context
  MUST be the hospital of its branch, and the system MUST reject mismatched hospital/branch
  associations.
- **FR-006**: The system MUST allow administrators to register a doctor within a hospital with a
  human-readable doctor code, name, specialization, professional registration number, active or
  inactive status, and at least one department association.
- **FR-007**: A doctor code and professional registration number MUST each be unique within the
  doctor's hospital.
- **FR-008**: The system MUST allow a doctor to be associated with one or more departments only
  when every selected department belongs to that doctor's hospital.
- **FR-009**: The system MUST allow administrators to view all doctors for a hospital and view
  only doctors associated with a selected department.
- **FR-010**: The system MUST allow administrators to activate and deactivate a registered doctor
  without removing the doctor's details or department associations.
- **FR-011**: The system MUST reject invalid, incomplete, duplicate, or cross-hospital and
  cross-branch foundational-data requests without creating or changing records.
- **FR-012**: The system MUST retain creation and status-change history for hospitals, branches,
  departments, and doctors sufficient to identify what changed and when; the history MUST not
  include unnecessary medical or patient information.
- **FR-013**: The system MUST NOT provide deletion of foundational data, doctor schedules,
  patient registration, appointment booking, or queue management in this feature.
- **FR-014**: Authentication and authorization mechanisms are outside this feature's scope;
  interactions in this specification refer to administrators as the intended actor.

### Key Entities *(include if feature involves data)*

- **Hospital**: A healthcare organization that owns one or more branches and its doctors.
- **Branch**: A physical or operational location belonging to exactly one hospital and containing
  multiple departments.
- **Department**: A clinical or service unit, such as Cardiology, Orthopaedics, or General
  Medicine, belonging to exactly one branch.
- **Doctor**: A professional registered within one hospital, identified by a human-readable doctor
  code and professional registration number, with name, specialization, and active/inactive
  status.
- **Doctor-Department Association**: The assignment connecting one doctor to one department;
  every associated department belongs to the doctor's hospital.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: An administrator can create a hospital, branch, and department hierarchy in under
  3 minutes when the required details are available.
- **SC-002**: An administrator can register a doctor with one or more departments in under
  2 minutes when the required details are available.
- **SC-003**: In acceptance testing with 100 hospitals, 10 branches per hospital, 10 departments
  per branch, and 20 doctors per hospital, 100% of displayed branch, department, and doctor lists
  contain only records in the selected organizational context.
- **SC-004**: In acceptance testing, 100% of attempted invalid cross-hospital or cross-branch
  associations are rejected without changing stored data.
- **SC-005**: At least 90% of representative administrators can complete hospital setup and doctor
  registration on their first attempt without assistance.

## Assumptions

- Hospital, branch, and department names are required; branch names are unique within a hospital,
  and department names are unique within a branch.
- A doctor is registered to one hospital but may serve departments across multiple branches of
  that same hospital.
- A newly registered doctor is active unless an administrator explicitly records the doctor as
  inactive during registration.
- Authentication and authorization will be introduced by a later feature; this feature defines
  the intended administrator actions but does not enforce identity or permissions.
- No migration, import, merge, deletion, or archival workflow is required for this initial
  feature.
