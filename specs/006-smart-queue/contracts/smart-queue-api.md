# Smart Queue API Contract

`GET /api/v1/hospitals/{hospitalId}/branches/{branchId}/doctors/{doctorId}/queue` returns scoped queue projection.

`PUT /api/v1/hospitals/{hospitalId}/branches/{branchId}/queue-entries/{queueEntryId}/priority` accepts `{ "priority": "NORMAL|PRIORITY|URGENT" }`.

`PUT /api/v1/hospitals/{hospitalId}/branches/{branchId}/doctors/{doctorId}/operational-status` accepts `{ "status": "AVAILABLE|BUSY|UNAVAILABLE" }`.

All operations require trusted staff scope; production fails closed. Out-of-scope and absent resources return non-disclosing `404`; unavailable/race conflicts return `409`.
