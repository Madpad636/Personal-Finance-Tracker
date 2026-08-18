package com.financeapp.dto.savings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SavingsGoalResponse(
        UUID id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal remainingAmount,
        double percentComplete,
        LocalDate targetDate
) {}
