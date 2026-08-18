# Personal Finance Management System

A secure, full-stack app for tracking income, expenses, budgets, and savings
goals. React + TypeScript on the frontend, Spring Boot 3 on the backend,
PostgreSQL for storage.

```
finance-management-system/
├── backend/          Spring Boot 3 API (Java 21, Maven)
├── frontend/         React + Vite + TypeScript + Tailwind
├── database/         Reference copies of the schema (source of truth is
│                     backend/src/main/resources/db/migration/, via Flyway)
├── docs/             Architecture notes, test plan, ER diagram
├── docker-compose.yml   Local PostgreSQL
└── README.md         You are here
```

## Status

All eight modules (Auth, Income, Expense, Budget, Savings Goals, Dashboard,
Reports, Profile) are implemented end to end — backend service + controller,
and a wired frontend page. See `docs/ARCHITECTURE.md` for the full module
table and the short list of known gaps (mainly: edit UI for budgets/savings
goals, rate-limit wiring, and unit test coverage beyond Auth).

## Quick start

### 1. Database
```bash
docker compose up -d postgres
```

### 2. Backend
```bash
cd backend
cp .env.example .env    # then fill in JWT_SECRET (openssl rand -base64 64) etc.
export $(cat .env | xargs)   # or use your IDE's env-file support
mvn spring-boot:run
```
API comes up on `http://localhost:8080`. Swagger UI: `http://localhost:8080/swagger-ui.html`.

Flyway applies the schema + seed categories automatically on boot.

### 3. Frontend
```bash
cd frontend
npm install
cp .env.example .env
npm run dev
```
App comes up on `http://localhost:5173`.

### 4. Tests
```bash
# Backend (spins up Testcontainers Postgres automatically)
cd backend && mvn test

# Frontend E2E
cd frontend && npm run test:e2e
```

## Security features
- JWT auth (short-lived access token + refresh token), BCrypt password hashing (strength 12)
- Account lockout after 5 failed logins (15 min), full audit log of login/lockout/CRUD events
- Row-level ownership checks on every read/write across Income, Expense, Budget, Savings Goals
- Global exception handler with consistent error payloads, no stack traces leaked
- CORS locked to the configured frontend origin; parameterized queries only (no raw SQL)
- Rate-limit buckets defined (`RateLimitConfig`, Bucket4j) — not yet wired into a filter/interceptor (see `docs/ARCHITECTURE.md`)

## Documentation
- `docs/ARCHITECTURE.md` — layered architecture, request flow, auth flow, module status, known gaps
- `docs/TEST_PLAN.md` — what's tested, what's outstanding, how to run everything
- `docs/diagrams/` — ER diagram (Mermaid); class/use-case/sequence diagrams still to produce
