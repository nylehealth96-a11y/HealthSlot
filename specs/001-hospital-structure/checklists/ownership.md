# Ownership and Integrity Checklist: Hospital Structure Management

**Purpose**: Review the completeness, clarity, and consistency of requirements for ownership,
duplicates, doctor status, and cross-hospital isolation.
**Created**: 2026-08-20
**Feature**: [spec.md](../spec.md)

**Note**: This custom checklist is a requirements-quality review artifact.
**Review Ownership**: Mark an item `[x]` only when a reviewer determines that the written
requirements-quality criterion is satisfied.
**Marker Semantics**: `[x]` means requirements have been reviewed and are satisfactory; it does
not mean implementation work is complete.

## Requirement Completeness

- [x] CHK001 Are the ownership relationships for hospital, branch, department, doctor, and
  doctor-department membership completely defined? [Completeness, Spec §Key Entities]
- [x] CHK002 Are the required ownership-chain rules for all nested reads and writes documented?
  [Completeness, Spec §FR-002 to FR-005]
- [x] CHK003 Are all uniqueness scopes documented for hospital, branch, department, doctor code,
  and professional registration number? [Completeness, Spec §FR-007, Assumptions]
- [x] CHK004 Are the initial doctor status, allowed status changes, retained associations, and
  absence of deletion all specified? [Completeness, Spec §FR-006, FR-010, FR-013]

## Requirement Clarity

- [x] CHK005 Is the term "unrelated hospital or branch" defined by an explicit ownership chain
  rather than an implied identifier relationship? [Clarity, Spec §FR-003, FR-005, FR-011]
- [x] CHK006 Is duplicate comparison clarified for letter case and surrounding whitespace across
  every duplicate-sensitive field? [Gap, Spec §FR-007, Assumptions]
- [x] CHK007 Is the scope of professional registration-number uniqueness consistently specified
  between the feature requirements and the data-model design? [Consistency, Spec §FR-007,
  data-model.md]
- [x] CHK008 Is the response outcome for an unrelated identifier consistently defined as
  not-found, ownership-conflict, or another single safe outcome? [Ambiguity, Spec §FR-011,
  data-model.md]

## Scenario and Edge Case Coverage

- [x] CHK009 Are requirements defined for associating one doctor with departments at different
  branches of the same hospital? [Coverage, Spec §FR-008, Edge Cases]
- [x] CHK010 Are requirements defined for rejecting every cross-hospital association combination,
  including branch/department, doctor/department, and doctor/status context? [Coverage,
  Spec §FR-003, FR-005, FR-008, FR-011]
- [x] CHK011 Are duplicate outcomes defined for repeated submissions and concurrent attempts,
  including whether stored data changes? [Coverage, Spec §FR-011, Edge Cases]
- [x] CHK012 Are same-status doctor requests clearly defined as idempotent, including their
  returned state and audit expectations? [Clarity, Spec §FR-010, Edge Cases]

## Acceptance Criteria Quality

- [x] CHK013 Do the acceptance scenarios objectively demonstrate that hierarchy lists exclude
  unrelated organizational records? [Measurability, Spec §User Story 1]
- [x] CHK014 Do the acceptance scenarios objectively demonstrate that doctor lists and status
  changes remain constrained to the owning hospital? [Measurability, Spec §User Stories 2-3]
- [x] CHK015 Are the cross-hospital isolation success measures consistent with the functional
  rejection requirements? [Consistency, Spec §SC-003, SC-004, FR-011]

## Dependencies and Assumptions

- [x] CHK016 Is the lack of authentication clearly separated from the required data-level
  hospital isolation so it cannot be misread as permission enforcement? [Clarity, Spec §FR-014,
  Assumptions]
- [x] CHK017 Are the assumptions about branch-name and department-name uniqueness validated
  against the stated business intent? [Assumption, Spec §Assumptions]

## Clarification Follow-up Review

- [x] CHK018 Are the platform-wide hospital-name uniqueness rule and its case/whitespace
  normalization rule specified consistently? [Consistency, Spec §FR-001a, FR-015, Assumptions]
- [x] CHK019 Is global professional-registration-number uniqueness distinguished clearly from
  hospital-scoped doctor-code uniqueness? [Clarity, Spec §FR-007]
- [x] CHK020 Does the specification state whether inactive doctors remain included in ordinary
  hospital and department directory views, or explicitly defer that filter behavior? [Gap,
  Spec §FR-009, FR-010]
- [x] CHK021 Are the requirements for idempotent active/inactive requests specific about both
  retained associations and the required audit outcome? [Completeness, Spec §FR-010, FR-012,
  Edge Cases]
- [x] CHK022 Are cross-hospital isolation requirements consistently separated from future
  authentication and authorization requirements? [Consistency, Spec §FR-003, FR-005, FR-008,
  FR-011, FR-014]

## Notes

- Leave items unchecked when they need clarification, correction, or reviewer evaluation.
- `$speckit-implement` reads checklist state but does not modify reviewer markers.
