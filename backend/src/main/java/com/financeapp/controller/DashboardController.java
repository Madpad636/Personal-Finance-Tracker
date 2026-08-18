package com.financeapp.controller;

import com.financeapp.dto.report.DashboardSummaryResponse;
import com.financeapp.security.AuthenticatedUser;
import com.financeapp.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: implement DashboardService to aggregate income/expenses/budget/savings for charts.
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Aggregated summary for the dashboard view")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(@AuthenticationPrincipal AuthenticatedUser user,
                                                                 @RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(dashboardService.getSummary(user.getId(), month, year));
    }
}
