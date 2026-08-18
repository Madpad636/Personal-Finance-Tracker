package com.financeapp.dto.expense;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequest(
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotNull UUID categoryId,
        @NotNull LocalDate date,
        String description
) {}
