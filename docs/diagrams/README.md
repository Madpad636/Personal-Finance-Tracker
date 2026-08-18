# Diagrams

Mermaid source for the ER diagram — render at https://mermaid.live or with
a Mermaid-enabled Markdown viewer.

```mermaid
erDiagram
    USERS ||--o{ INCOMES : has
    USERS ||--o{ EXPENSES : has
    USERS ||--o{ BUDGETS : has
    USERS ||--o{ SAVINGS_GOALS : has
    USERS ||--o{ AUDIT_LOGS : generates
    CATEGORIES ||--o{ EXPENSES : categorizes
    CATEGORIES ||--o{ BUDGETS : "scopes (optional)"

    USERS {
        uuid id PK
        varchar full_name
        varchar email UK
        text password_hash
        boolean enabled
        int failed_login_attempts
        timestamp account_locked_until
    }
    CATEGORIES {
        uuid id PK
        varchar name UK
        varchar icon
        boolean is_default
    }
    INCOMES {
        uuid id PK
        uuid user_id FK
        numeric amount
        varchar source
        date date
        varchar description
    }
    EXPENSES {
        uuid id PK
        uuid user_id FK
        uuid category_id FK
        numeric amount
        date date
        varchar description
    }
    BUDGETS {
        uuid id PK
        uuid user_id FK
        uuid category_id FK "nullable"
        smallint month
        int year
        numeric limit_amount
    }
    SAVINGS_GOALS {
        uuid id PK
        uuid user_id FK
        varchar name
        numeric target_amount
        numeric current_amount
        date target_date
    }
    AUDIT_LOGS {
        uuid id PK
        uuid user_id FK "nullable"
        varchar action
        varchar details
        varchar ip_address
    }
```

## Still to produce
- Class diagram (Controller/Service/Repository/Entity relationships)
- Use case diagram (register, login, manage income/expense/budget/savings, generate report)
- Sequence diagram (login flow with JWT issuance + refresh)
- High-level architecture diagram (React ⟷ Spring Boot ⟷ PostgreSQL, with the security filter chain)

Ask for any of these and I'll generate them as Mermaid/SVG next.
