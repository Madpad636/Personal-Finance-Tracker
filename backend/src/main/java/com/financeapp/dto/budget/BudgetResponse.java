package com.financeapp.dto.budget;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        Integer month,
        Integer year,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        double percentUsed,
        boolean exceeded
) {}
