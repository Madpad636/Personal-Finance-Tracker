package com.financeapp.dto.savings;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingsGoalRequest(
        @NotBlank String name,
        @NotNull @DecimalMin(value = "0.01") BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate targetDate
) {}
