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
@Table(name = "wallet")
@AllArgsConstructor
@EqualsAndHashCode(of = {"walletId"})
public class Wallet {

    @EmbeddedId
    private WalletId walletId;

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
    @EqualsAndHashCode(of = {"id", "createdAt"})
    @Embeddable
    public static class WalletId implements Serializable {

        // identifier for account (CFJK32DUI4)
        @NotNull(message = "Wallet id is required")
        @Column(name = "id", updatable = false, nullable = false, length = 10)
        private String id;

        // updated timestamp of asset price
        @Column(name = "created_at", nullable = false)
        @NotNull
        private Instant createdAt;
    }
}