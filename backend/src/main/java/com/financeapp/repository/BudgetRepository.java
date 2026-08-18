package com.financeapp.repository;

import com.financeapp.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    List<Budget> findByUserIdAndMonthAndYear(UUID userId, Integer month, Integer year);
    Optional<Budget> findByUserIdAndMonthAndYearAndCategoryIsNull(UUID userId, Integer month, Integer year);
}
