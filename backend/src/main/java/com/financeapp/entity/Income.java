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
@Table(name = "incomes", indexes = {
        @Index(name = "idx_incomes_user_date", columnList = "user_id, date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Income {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String source;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
