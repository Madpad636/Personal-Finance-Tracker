package com.financeapp.service.impl;

import com.financeapp.dto.budget.BudgetRequest;
import com.financeapp.dto.budget.BudgetResponse;
import com.financeapp.entity.AuditLog;
import com.financeapp.entity.Budget;
import com.financeapp.entity.Category;
import com.financeapp.entity.Expense;
import com.financeapp.entity.User;
import com.financeapp.exception.ResourceNotFoundException;
import com.financeapp.exception.UnauthorizedAccessException;
import com.financeapp.repository.AuditLogRepository;
import com.financeapp.repository.BudgetRepository;
import com.financeapp.repository.CategoryRepository;
import com.financeapp.repository.ExpenseRepository;
import com.financeapp.repository.UserRepository;
import com.financeapp.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public BudgetResponse create(UUID userId, BudgetRequest request) {
        User user = userRepository.getReferenceById(userId);
        Category category = resolveCategory(request.categoryId());

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .month(request.month())
                .year(request.year())
                .limitAmount(request.limitAmount())
                .build();
        budget = budgetRepository.save(budget);
        audit(userId, "BUDGET_CREATED", budget.getId().toString());
        return toResponse(userId, budget);
    }

    @Override
    @Transactional
    public BudgetResponse update(UUID userId, UUID budgetId, BudgetRequest request) {
        Budget budget = getOwnedOrThrow(userId, budgetId);
        budget.setCategory(resolveCategory(request.categoryId()));
        budget.setMonth(request.month());
        budget.setYear(request.year());
        budget.setLimitAmount(request.limitAmount());
        audit(userId, "BUDGET_UPDATED", budgetId.toString());
        return toResponse(userId, budget);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID budgetId) {
        Budget budget = getOwnedOrThrow(userId, budgetId);
        budgetRepository.delete(budget);
        audit(userId, "BUDGET_DELETED", budgetId.toString());
    }

    @Override
    public List<BudgetResponse> listForMonth(UUID userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(b -> toResponse(userId, b))
                .toList();
    }

    private Category resolveCategory(UUID categoryId) {
        if (categoryId == null) return null; // overall monthly budget
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private Budget getOwnedOrThrow(UUID userId, UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found: " + budgetId));
        if (!budget.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("This budget does not belong to the current user");
        }
        return budget;
    }

    /** Computes spend against the budget on the fly rather than storing a redundant running total. */
    private BudgetResponse toResponse(UUID userId, Budget budget) {
        YearMonth ym = YearMonth.of(budget.getYear(), budget.getMonth());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Expense> monthExpenses = expenseRepository.findByUserIdAndDateBetween(userId, start, end);
        BigDecimal spent = monthExpenses.stream()
                .filter(e -> budget.getCategory() == null || e.getCategory().getId().equals(budget.getCategory().getId()))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        double percentUsed = budget.getLimitAmount().signum() == 0 ? 0.0
                : spent.divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100;

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory() != null ? budget.getCategory().getId() : null,
                budget.getCategory() != null ? budget.getCategory().getName() : null,
                budget.getMonth(),
                budget.getYear(),
                budget.getLimitAmount(),
                spent,
                remaining,
                percentUsed,
                spent.compareTo(budget.getLimitAmount()) > 0
        );
    }

    private void audit(UUID userId, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
                .user(userRepository.getReferenceById(userId))
                .action(action)
                .details(details)
                .build());
    }
}
