package com.financeapp.service;

import com.financeapp.dto.budget.BudgetRequest;
import com.financeapp.dto.budget.BudgetResponse;

import java.util.List;
import java.util.UUID;

// TODO: implement. spentAmount/percentUsed/exceeded should be computed by summing
// Expense rows for the same user/month/(category) rather than stored redundantly.
public interface BudgetService {
    BudgetResponse create(UUID userId, BudgetRequest request);
    BudgetResponse update(UUID userId, UUID budgetId, BudgetRequest request);
    void delete(UUID userId, UUID budgetId);
    List<BudgetResponse> listForMonth(UUID userId, int month, int year);
}
