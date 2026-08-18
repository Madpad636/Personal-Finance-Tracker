package com.financeapp.repository;

import com.financeapp.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, UUID> {
    List<SavingsGoal> findByUserId(UUID userId);
}
