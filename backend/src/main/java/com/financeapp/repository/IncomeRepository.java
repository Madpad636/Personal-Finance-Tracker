package com.financeapp.repository;

import com.financeapp.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IncomeRepository extends JpaRepository<Income, UUID>, JpaSpecificationExecutor<Income> {
    List<Income> findByUserIdAndDateBetween(UUID userId, LocalDate start, LocalDate end);
    List<Income> findByUserId(UUID userId);
}
