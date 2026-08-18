# Architecture

## Layered backend (MVC + service/repository pattern)

```
Controller  -> validates request shape (DTO), delegates to Service, never touches entities directly
Service     -> business logic, ownership checks, transactions, audit logging
Repository  -> Spring Data JPA, no raw SQL
Entity      -> JPA-mapped tables
```

Every write path from Controller down to Repository takes the authenticated
user's ID from the JWT principal (`AuthenticatedUser`), never from the
request body — this is what stops one user from reading or editing another
user's data (see `*ServiceImpl.getOwnedOrThrow` in Income/Expense/Budget/SavingsGoal).

## Request flow

```
React (Axios) --Bearer JWT--> Spring Security filter chain
                                 |
                         JwtAuthenticationFilter (validates token, loads UserDetails)
                                 |
                         Controller (@AuthenticationPrincipal AuthenticatedUser)
                                 |
                         Service (business rules + ownership check)
                                 |
                         Repository (JPA, parameterized queries only)
                                 |
                             PostgreSQL
```

## Auth flow

1. `POST /api/auth/register` or `/login` returns a short-lived access token
   (15 min default) and a longer-lived refresh token (7 days default).
2. The frontend Axios instance (`services/api.ts`) attaches the access token
   to every request and transparently refreshes it on a 401 once, before
   forcing a re-login.
3. Passwords are hashed with BCrypt (strength 12). Five failed login
   attempts locks the account for 15 minutes (see `AuthServiceImpl`).

## Derived data, not stored totals

Budget spend (`spentAmount`, `percentUsed`, `exceeded`) and the dashboard
summary are computed on read by summing `Income`/`Expense` rows for the
relevant month, rather than maintaining a redundant running total. This
keeps the numbers always correct after edits/deletes, at the cost of doing
the aggregation in Java rather than SQL — acceptable at this data volume;
worth moving to a SQL `GROUP BY`/materialized view if a user's transaction
history grows very large.

## Module status

| Module         | Backend                                             | Frontend                              |
|----------------|------------------------------------------------------|-----------------------------------------|
| Auth           | ✅ implemented                                        | ✅ Login/Register wired                 |
| Income         | ✅ implemented                                        | ✅ full CRUD (list/add/edit/delete/search)|
| Expense        | ✅ implemented                                        | ✅ full CRUD + category filter           |
| Budget         | ✅ implemented (spend computed from Expenses)         | ✅ create/delete, progress bars, exceeded warning |
| Savings Goals  | ✅ implemented                                        | ✅ create/delete, progress bars          |
| Dashboard      | ✅ implemented (income/expense/budget/savings + 6-mo trend) | ✅ charts (pie/bar/line) wired      |
| Reports        | ✅ implemented (CSV via OpenCSV, PDF via iText7)      | ✅ month/year picker + download buttons  |
| User Profile   | ✅ implemented (view/update name, password change)   | ✅ profile form + change-password form   |
| Categories     | ✅ read-only endpoint (seeded via Flyway)             | ✅ used in Expense/Budget dropdowns      |

## Known gaps / next steps

- **Edit for Budget & Savings Goal** on the frontend (create/delete are wired; add an edit modal following `pages/Income.tsx`'s pattern).
- **Rate limiting** buckets are defined (`RateLimitConfig`, Bucket4j) but not yet wired into a filter/interceptor on `/api/auth/login`.
- **Server-side logout/token revocation**: logout is currently client-side only (tokens discarded); add a Redis-backed blocklist keyed by JTI for true server-side revocation.
- **Unit test coverage**: `AuthServiceImplTest` is the reference pattern; Income/Expense/Budget/SavingsGoal/Report service tests are still outstanding (see `docs/TEST_PLAN.md`).
- **Category management**: categories are currently seeded and read-only; add admin CRUD if users should be able to add custom categories.
- Class/use-case/sequence/high-level architecture diagrams are still to be drawn (ER diagram is done, see `docs/diagrams/`).
