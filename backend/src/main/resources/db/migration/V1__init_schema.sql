-- Personal Finance Management System - baseline schema (3NF)
-- Requires PostgreSQL 14+ (uses gen_random_uuid() from pgcrypto)

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name              VARCHAR(100) NOT NULL,
    email                  VARCHAR(150) NOT NULL,
    password_hash          TEXT NOT NULL,
    enabled                BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts  INTEGER NOT NULL DEFAULT 0,
    account_locked_until   TIMESTAMP,
    created_at             TIMESTAMP NOT NULL DEFAULT now(),
    updated_at             TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(50) NOT NULL,
    icon        VARCHAR(30),
    is_default  BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE TABLE incomes (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount       NUMERIC(14,2) NOT NULL CHECK (amount > 0),
    source       VARCHAR(100) NOT NULL,
    date         DATE NOT NULL,
    description  VARCHAR(500),
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_incomes_user_date ON incomes (user_id, date);

CREATE TABLE expenses (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id  UUID NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    amount       NUMERIC(14,2) NOT NULL CHECK (amount > 0),
    date         DATE NOT NULL,
    description  VARCHAR(500),
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_expenses_user_date ON expenses (user_id, date);
CREATE INDEX idx_expenses_category ON expenses (category_id);

CREATE TABLE budgets (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id  UUID REFERENCES categories(id) ON DELETE CASCADE, -- NULL = overall monthly budget
    month        SMALLINT NOT NULL CHECK (month BETWEEN 1 AND 12),
    year         INTEGER NOT NULL,
    limit_amount NUMERIC(14,2) NOT NULL CHECK (limit_amount > 0),
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_budget_user_month_category UNIQUE (user_id, month, year, category_id)
);

CREATE TABLE savings_goals (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name           VARCHAR(150) NOT NULL,
    target_amount  NUMERIC(14,2) NOT NULL CHECK (target_amount > 0),
    current_amount NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (current_amount >= 0),
    target_date    DATE,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE audit_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID REFERENCES users(id) ON DELETE SET NULL,
    action      VARCHAR(80) NOT NULL,
    details     VARCHAR(1000),
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_audit_logs_user ON audit_logs (user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs (created_at);
