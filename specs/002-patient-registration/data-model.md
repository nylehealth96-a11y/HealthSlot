# Data Model: Patient Registration

## Patient

| Field | Rule |
|---|---|
| id | UUID primary key |
| patient_number | Required globally unique human-readable identifier; immutable |
| first_name / last_name | Required |
| date_of_birth / gender / mobile_number | Required; mobile is non-unique and not a primary key |
| email / address / emergency_contact | Optional |
| created_at / updated_at | Required `timestamptz` audit timestamps |

Registration and profile-update audit events identify the action and patient without storing
unnecessary medical information. All basic details may be updated; `id` and `patient_number` may not.
