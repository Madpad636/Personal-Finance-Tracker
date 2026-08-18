package com.financeapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "savings_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsGoal {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal targetAmount;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

    private LocalDate targetDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
