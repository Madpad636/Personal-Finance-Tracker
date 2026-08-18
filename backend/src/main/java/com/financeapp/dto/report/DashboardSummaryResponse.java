package com.financeapp.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal remainingBalance,
        BigDecimal monthlyBudget,
        BigDecimal totalSavings,
        List<CategorySpend> spendingByCategory,
        List<MonthlyTrendPoint> monthlyTrend
) {
    public record CategorySpend(String category, BigDecimal amount) {}
    public record MonthlyTrendPoint(String month, BigDecimal income, BigDecimal expenses) {}
}
