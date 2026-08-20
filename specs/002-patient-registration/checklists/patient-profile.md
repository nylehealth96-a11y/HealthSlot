# Patient Profile Checklist: Patient Registration

**Purpose**: Review requirements quality for patient identity and profile management.
**Created**: 2026-08-20
**Feature**: [spec.md](../spec.md)

**Review Ownership**: This is a reviewer-owned requirements-quality artifact; `[x]` does not mean implementation is complete.

## Requirement Completeness

- [ ] CHK001 Are internal identifier and human-readable patient-number requirements both defined? [Completeness, Spec §FR-001]
- [ ] CHK002 Are every required and optional patient detail explicitly classified? [Completeness, Spec §FR-002]
- [ ] CHK003 Are retrieval and search criteria defined for each supported patient identifier? [Completeness, Spec §FR-004, FR-005]
- [ ] CHK004 Are editable and immutable patient fields explicitly distinguished? [Completeness, Spec §FR-006, Assumptions]

## Clarity and Consistency

- [ ] CHK005 Is mobile-number non-uniqueness consistent with its search behavior and identity restrictions? [Consistency, Spec §FR-003, Edge Cases]
- [ ] CHK006 Is patient-number generation and uniqueness sufficiently defined for acceptance review? [Clarity, Assumptions]
- [ ] CHK007 Are invalid demographic and contact input rules specific enough to review objectively? [Clarity, Spec §FR-007, Edge Cases]

## Scenario Coverage

- [ ] CHK008 Are no-match search results and optional-contact omission requirements documented? [Coverage, Edge Cases]
- [ ] CHK009 Are update requirements clear about preserving both identifiers? [Coverage, User Story 3, SC-002]
- [ ] CHK010 Are patient registration and update audit requirements defined without requiring medical information in logs? [Coverage, Spec §FR-008]

## Scope and Privacy

- [ ] CHK011 Is appointment booking clearly excluded from patient-profile scope? [Scope, Spec §FR-009]
- [ ] CHK012 Are assumptions about authentication being outside scope distinct from patient data protection requirements? [Clarity, Assumptions]

## Notes

- Mark items only after requirements-quality review.
