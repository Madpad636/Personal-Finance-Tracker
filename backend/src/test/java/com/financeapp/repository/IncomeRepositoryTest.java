package com.financeapp.repository;

// TODO: @DataJpaTest + Testcontainers Postgres. Verify:
//  - findByUserIdAndDateBetween returns only the calling user's records
//  - findByUserId respects insertion/date ordering expectations
//  - amount CHECK constraint (amount > 0) is enforced at the DB level
class IncomeRepositoryTest {
}
