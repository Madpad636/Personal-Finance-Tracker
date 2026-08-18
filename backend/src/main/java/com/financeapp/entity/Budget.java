package com.financeapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A monthly budget, optionally scoped to a single category (null = overall budget).
 */
@Entity
@Table(name = "budgets", uniqueConstraints = {
        @UniqueConstraint(name = "uk_budget_user_month_category",
                columnNames = {"user_id", "month", "year", "category_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // nullable: null = overall monthly budget

    @NotNull
    @Min(1) @Max(12)
    @Column(nullable = false)
    @JdbcTypeCode(Types.SMALLINT)
    private Integer month;

    @NotNull
    @Column(nullable = false)
    private Integer year;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal limitAmount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
