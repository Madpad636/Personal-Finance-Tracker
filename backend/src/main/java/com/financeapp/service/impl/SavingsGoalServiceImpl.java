package com.financeapp.service.impl;

import com.financeapp.dto.savings.SavingsGoalRequest;
import com.financeapp.dto.savings.SavingsGoalResponse;
import com.financeapp.entity.AuditLog;
import com.financeapp.entity.SavingsGoal;
import com.financeapp.entity.User;
import com.financeapp.exception.ResourceNotFoundException;
import com.financeapp.exception.UnauthorizedAccessException;
import com.financeapp.repository.AuditLogRepository;
import com.financeapp.repository.SavingsGoalRepository;
import com.financeapp.repository.UserRepository;
import com.financeapp.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public SavingsGoalResponse create(UUID userId, SavingsGoalRequest request) {
        User user = userRepository.getReferenceById(userId);
        SavingsGoal goal = SavingsGoal.builder()
                .user(user)
                .name(request.name())
                .targetAmount(request.targetAmount())
                .currentAmount(request.currentAmount() != null ? request.currentAmount() : BigDecimal.ZERO)
                .targetDate(request.targetDate())
                .build();
        goal = savingsGoalRepository.save(goal);
        audit(userId, "SAVINGS_GOAL_CREATED", goal.getId().toString());
        return toResponse(goal);
    }

    @Override
    @Transactional
    public SavingsGoalResponse update(UUID userId, UUID goalId, SavingsGoalRequest request) {
        SavingsGoal goal = getOwnedOrThrow(userId, goalId);
        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        if (request.currentAmount() != null) goal.setCurrentAmount(request.currentAmount());
        goal.setTargetDate(request.targetDate());
        audit(userId, "SAVINGS_GOAL_UPDATED", goalId.toString());
        return toResponse(goal);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID goalId) {
        SavingsGoal goal = getOwnedOrThrow(userId, goalId);
        savingsGoalRepository.delete(goal);
        audit(userId, "SAVINGS_GOAL_DELETED", goalId.toString());
    }

    @Override
    public List<SavingsGoalResponse> list(UUID userId) {
        return savingsGoalRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    private SavingsGoal getOwnedOrThrow(UUID userId, UUID goalId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found: " + goalId));
        if (!goal.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("This savings goal does not belong to the current user");
        }
        return goal;
    }

    private void audit(UUID userId, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
                .user(userRepository.getReferenceById(userId))
                .action(action)
                .details(details)
                .build());
    }

    private SavingsGoalResponse toResponse(SavingsGoal g) {
        BigDecimal remaining = g.getTargetAmount().subtract(g.getCurrentAmount()).max(BigDecimal.ZERO);
        double percent = g.getTargetAmount().signum() == 0 ? 0.0
                : g.getCurrentAmount().divide(g.getTargetAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100;
        return new SavingsGoalResponse(
                g.getId(), g.getName(), g.getTargetAmount(), g.getCurrentAmount(),
                remaining, Math.min(percent, 100.0), g.getTargetDate());
    }
}
