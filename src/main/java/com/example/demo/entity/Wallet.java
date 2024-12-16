package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Wallet performance entity.
 */

@Builder
@Data
@NoArgsConstructor(force = true)
@Entity
@Table(name = "wallet")
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Wallet {

    // identifier for account (CFJK32DUI4)
    @Id
    @NotNull(message = "Wallet id is required")
    @Column(name = "id", updatable = false, nullable = false, length = 10)
    private String id;

    // 9999999999.00 total financial value in USD
    @Column(name = "total_usd", nullable = false)
    @NotNull
    private BigDecimal totalUsd;

    // updated timestamp of asset price
    @Column(name = "updated_at", nullable = false)
    @NotNull
    private Instant updatedAt;

    @Column(name = "best_asset", length = 5)
    private String bestAsset;

    @Column(name = "best_performance")
    private Float bestPerformance;

    @Column(name = "worst_asset", length = 5)
    private String worstAsset;

    @Column(name = "worst_performance")
    private Float worstPerformance;
}