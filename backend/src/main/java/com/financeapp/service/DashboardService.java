package com.financeapp.service;

import com.financeapp.dto.report.DashboardSummaryResponse;

import java.util.UUID;

// TODO: aggregate Income/Expense/Budget/SavingsGoal for the given user + month/year
// into a single DashboardSummaryResponse for the frontend charts.
public interface DashboardService {
    DashboardSummaryResponse getSummary(UUID userId, int month, int year);
}
