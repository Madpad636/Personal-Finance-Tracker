package com.financeapp.dto.budget;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetRequest(
        UUID categoryId, // null = overall monthly budget
        @NotNull @Min(1) @Max(12) Integer month,
        @NotNull Integer year,
        @NotNull @DecimalMin(value = "0.01") BigDecimal limitAmount
) {}
