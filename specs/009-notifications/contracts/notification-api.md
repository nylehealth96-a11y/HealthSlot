# Notification API and Inbound Contracts

## Operational status

`GET /api/v1/hospitals/{hospitalId}/branches/{branchId}/notifications/{notificationId}` returns only ID, type, recipient class, status, attempt count, created/updated/delivered instants, and safe failure reason.

Paths locate resources only. Trusted server-side identity supplies authority/scope. Production fails closed until authentication exists. Unknown and out-of-scope values return the same non-disclosing `404`; malformed input returns `400`.

## Manual retry

`POST /api/v1/hospitals/{hospitalId}/branches/{branchId}/notifications/{notificationId}/retry` accepts no recipient, contact, provider, scope, or status fields. An eligible in-scope failed notification is re-queued; otherwise return non-disclosing `404` or conflict without patient data.

## Internal workflow trigger

Only an internal server-side `NotificationTrigger` port may create intent. It carries source-event ID/revision, scope, type, authoritative recipient selection, eligibility, and branch-local time context. Normal requests cannot invoke delivery with caller-supplied identity/contact/state values.
