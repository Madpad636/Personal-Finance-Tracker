package com.financeapp.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        BigDecimal amount,
        UUID categoryId,
        String categoryName,
        LocalDate date,
        String description
) {}
