package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Wallet performance entity.
 */

@Builder
@Data
@NoArgsConstructor(force = true)
@Entity
@Table(name = "performance")
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Performance {

    @EmbeddedId
    private PerformanceId id;

    // 9999999999.00 total financial value in USD
    @Column(name = "total_usd", nullable = false)
    @NotNull
    private BigDecimal totalUsd;

    @Column(name = "best_asset", length = 5)
    private String bestAsset;

    @Column(name = "best_performance")
    private Float bestPerformance;

    @Column(name = "worst_asset", length = 5)
    private String worstAsset;

    @Column(name = "worst_performance")
    private Float worstPerformance;

    @Data
    @Builder
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @EqualsAndHashCode(of = {"walletId", "updatedAt"})
    @Embeddable
    public static class PerformanceId implements Serializable {

        // identifier for wallet (CFJK32DUI4)
        @NotNull(message = "Wallet id is required")
        @Column(name = "id", updatable = false, nullable = false, length = 10)
        private String walletId;

        // updated timestamp of asset price
        @Column(name = "updated_at", nullable = false)
        @NotNull
        private Instant updatedAt;
    }
}