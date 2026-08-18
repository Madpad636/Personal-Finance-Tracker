package com.financeapp.service.impl;

import com.financeapp.dto.expense.ExpenseRequest;
import com.financeapp.dto.expense.ExpenseResponse;
import com.financeapp.entity.AuditLog;
import com.financeapp.entity.Category;
import com.financeapp.entity.Expense;
import com.financeapp.entity.User;
import com.financeapp.exception.ResourceNotFoundException;
import com.financeapp.exception.UnauthorizedAccessException;
import com.financeapp.repository.AuditLogRepository;
import com.financeapp.repository.CategoryRepository;
import com.financeapp.repository.ExpenseRepository;
import com.financeapp.repository.UserRepository;
import com.financeapp.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public ExpenseResponse create(UUID userId, ExpenseRequest request) {
        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categoryId()));

        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .amount(request.amount())
                .date(request.date())
                .description(request.description())
                .build();
        expense = expenseRepository.save(expense);
        audit(userId, "EXPENSE_CREATED", expense.getId().toString());
        return toResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse update(UUID userId, UUID expenseId, ExpenseRequest request) {
        Expense expense = getOwnedOrThrow(userId, expenseId);
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categoryId()));

        expense.setAmount(request.amount());
        expense.setCategory(category);
        expense.setDate(request.date());
        expense.setDescription(request.description());
        audit(userId, "EXPENSE_UPDATED", expenseId.toString());
        return toResponse(expense);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID expenseId) {
        Expense expense = getOwnedOrThrow(userId, expenseId);
        expenseRepository.delete(expense);
        audit(userId, "EXPENSE_DELETED", expenseId.toString());
    }

    @Override
    public ExpenseResponse getById(UUID userId, UUID expenseId) {
        return toResponse(getOwnedOrThrow(userId, expenseId));
    }

    @Override
    public List<ExpenseResponse> list(UUID userId, LocalDate from, LocalDate to, UUID categoryId, String sortBy, String direction) {
        List<Expense> expenses;
        if (categoryId != null) {
            expenses = expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
        } else if (from != null && to != null) {
            expenses = expenseRepository.findByUserIdAndDateBetween(userId, from, to);
        } else {
            expenses = expenseRepository.findByUserId(userId);
        }

        Comparator<Expense> comparator = switch (sortBy == null ? "date" : sortBy) {
            case "amount" -> Comparator.comparing(Expense::getAmount);
            default -> Comparator.comparing(Expense::getDate);
        };
        if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();

        return expenses.stream().sorted(comparator).map(this::toResponse).toList();
    }

    private Expense getOwnedOrThrow(UUID userId, UUID expenseId) {
        Expense expense = expenseRepository.findWithCategoryById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + expenseId));
        if (!expense.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("This expense record does not belong to the current user");
        }
        return expense;
    }

    private void audit(UUID userId, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
                .user(userRepository.getReferenceById(userId))
                .action(action)
                .details(details)
                .build());
    }

    private ExpenseResponse toResponse(Expense e) {
        return new ExpenseResponse(e.getId(), e.getAmount(), e.getCategory().getId(),
                e.getCategory().getName(), e.getDate(), e.getDescription());
    }
}
