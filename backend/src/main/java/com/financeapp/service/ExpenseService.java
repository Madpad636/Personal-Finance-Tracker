package com.financeapp.service;

import com.financeapp.dto.expense.ExpenseRequest;
import com.financeapp.dto.expense.ExpenseResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// TODO: implement following the pattern in IncomeServiceImpl (ownership checks, audit logging).
public interface ExpenseService {
    ExpenseResponse create(UUID userId, ExpenseRequest request);
    ExpenseResponse update(UUID userId, UUID expenseId, ExpenseRequest request);
    void delete(UUID userId, UUID expenseId);
    ExpenseResponse getById(UUID userId, UUID expenseId);
    List<ExpenseResponse> list(UUID userId, LocalDate from, LocalDate to, UUID categoryId, String sortBy, String direction);
}
