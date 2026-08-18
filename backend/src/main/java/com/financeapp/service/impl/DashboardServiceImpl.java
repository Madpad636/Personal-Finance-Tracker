package com.financeapp.service.impl;

import com.financeapp.dto.report.DashboardSummaryResponse;
import com.financeapp.entity.Expense;
import com.financeapp.entity.Income;
import com.financeapp.entity.SavingsGoal;
import com.financeapp.repository.BudgetRepository;
import com.financeapp.repository.ExpenseRepository;
import com.financeapp.repository.IncomeRepository;
import com.financeapp.repository.SavingsGoalRepository;
import com.financeapp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int TREND_MONTHS = 6;

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final SavingsGoalRepository savingsGoalRepository;

    @Override
    public DashboardSummaryResponse getSummary(UUID userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Income> monthIncomes = incomeRepository.findByUserIdAndDateBetween(userId, start, end);
        List<Expense> monthExpenses = expenseRepository.findByUserIdAndDateBetween(userId, start, end);

        BigDecimal totalIncome = sum(monthIncomes.stream().map(Income::getAmount));
        BigDecimal totalExpenses = sum(monthExpenses.stream().map(Expense::getAmount));
        BigDecimal remainingBalance = totalIncome.subtract(totalExpenses);

        BigDecimal monthlyBudget = budgetRepository.findByUserIdAndMonthAndYearAndCategoryIsNull(userId, month, year)
                .map(b -> b.getLimitAmount())
                .orElse(BigDecimal.ZERO);

        BigDecimal totalSavings = sum(savingsGoalRepository.findByUserId(userId).stream()
                .map(SavingsGoal::getCurrentAmount));

        List<DashboardSummaryResponse.CategorySpend> spendingByCategory = monthExpenses.stream()
                .collect(Collectors.groupingBy(e -> e.getCategory().getName(),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)))
                .entrySet().stream()
                .map(e -> new DashboardSummaryResponse.CategorySpend(e.getKey(), e.getValue()))
                .toList();

        List<DashboardSummaryResponse.MonthlyTrendPoint> monthlyTrend = buildTrend(userId, ym);

        return new DashboardSummaryResponse(
                totalIncome, totalExpenses, remainingBalance, monthlyBudget, totalSavings,
                spendingByCategory, monthlyTrend);
    }

    private List<DashboardSummaryResponse.MonthlyTrendPoint> buildTrend(UUID userId, YearMonth latest) {
        List<DashboardSummaryResponse.MonthlyTrendPoint> points = new java.util.ArrayList<>();
        for (int i = TREND_MONTHS - 1; i >= 0; i--) {
            YearMonth ym = latest.minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            BigDecimal income = sum(incomeRepository.findByUserIdAndDateBetween(userId, start, end)
                    .stream().map(Income::getAmount));
            BigDecimal expenses = sum(expenseRepository.findByUserIdAndDateBetween(userId, start, end)
                    .stream().map(Expense::getAmount));

            String label = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + ym.getYear();
            points.add(new DashboardSummaryResponse.MonthlyTrendPoint(label, income, expenses));
        }
        return points;
    }

    private BigDecimal sum(java.util.stream.Stream<BigDecimal> amounts) {
        return amounts.reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
