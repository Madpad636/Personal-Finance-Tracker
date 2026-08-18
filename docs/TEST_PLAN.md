# Test Plan

## Unit tests (JUnit 5 + Mockito) — target 80% line coverage
- ✅ `AuthServiceImplTest` — register, login (success/failure/lockout), implemented as the reference pattern
- ⬜ IncomeServiceImplTest, ExpenseServiceImplTest, BudgetServiceImplTest, SavingsGoalServiceImplTest, ReportServiceImplTest
- ⬜ JwtUtilTest — token generation/parsing/expiry (stub in `security/JwtUtilTest.java`)
- ⬜ GlobalExceptionHandlerTest — verify each exception maps to the right HTTP status

## Integration tests (Spring Boot Test + Testcontainers)
- ✅ `AuthControllerIntegrationTest` — register→login flow, bad password, weak password validation
- ⬜ IncomeControllerIntegrationTest, ExpenseControllerIntegrationTest, BudgetControllerIntegrationTest
- ⬜ Repository tests for each JpaRepository (stub in `repository/IncomeRepositoryTest.java`)
- ⬜ Cross-user access denial test (user A cannot GET/PUT/DELETE user B's income/expense/budget/goal)

## End-to-end tests (Playwright)
- ✅ `tests/e2e/auth.spec.ts` — register → dashboard, unauthenticated redirect
- ⬜ Login, add income, add expense, create budget, add savings goal, generate report, update profile, logout

## Security tests
- ⬜ SQL injection attempts against filter/search params (expect: no effect, since JPA/Hibernate parameterizes all queries)
- ⬜ XSS payloads in free-text fields (description, source, name) — verify safe rendering on the frontend
- ⬜ Access another user's resource by ID — expect 403 (`UnauthorizedAccessException`)
- ⬜ Expired/tampered JWT — expect 401
- ⬜ Brute-force login — expect account lock after 5 failed attempts (already covered by `AuthServiceImplTest`)
- ⬜ Rate limit exceeded on `/api/auth/login` — expect 429

## Running

```bash
# Backend unit + integration tests (spins up Testcontainers Postgres automatically)
cd backend && mvn test

# Coverage report (after mvn test)
open backend/target/site/jacoco/index.html

# Frontend E2E
cd frontend && npm run test:e2e
```
