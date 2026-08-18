package com.financeapp.repository;

import com.financeapp.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("""
        SELECT e
        FROM Expense e
        JOIN FETCH e.category
        WHERE e.user.id = :userId
    """)
    List<Expense> findByUserId(@Param("userId") UUID userId);

    @Query("""
        SELECT e
        FROM Expense e
        JOIN FETCH e.category
        WHERE e.user.id = :userId
          AND e.category.id = :categoryId
    """)
    List<Expense> findByUserIdAndCategoryId(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId);

    @Query("""
        SELECT e
        FROM Expense e
        JOIN FETCH e.category
        WHERE e.user.id = :userId
          AND e.date BETWEEN :start AND :end
    """)
    List<Expense> findByUserIdAndDateBetween(
            @Param("userId") UUID userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("""
        SELECT e
        FROM Expense e
        JOIN FETCH e.category
        WHERE e.id = :expenseId
    """)
    Optional<Expense> findWithCategoryById(@Param("expenseId") UUID expenseId);
}