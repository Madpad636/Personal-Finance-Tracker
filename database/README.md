# Database

PostgreSQL 16 (14+ supported). Schema is owned by **Flyway** and lives at
`backend/src/main/resources/db/migration/`. The files in this folder are
copies for reference / manual psql use — do not edit these independently of
the Flyway migrations, or they will drift out of sync.

- `schema.sql` — baseline schema (mirrors V1__init_schema.sql)
- `seed/seed_categories.sql` — default expense categories (mirrors V2__seed_categories.sql)

## Local setup
```bash
createdb finance_db
psql finance_db -f schema.sql
psql finance_db -f seed/seed_categories.sql
```
Or just start the backend — Flyway will apply both automatically on boot.
