package com.example.demo.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Response for wallet performance
 * total: total financial value in USD of the entire wallet
 * best_asset: which asset had the best performance (value increase) the wallet compared to the latest price retrieved from the API
 * best_performance: percentage of the performance of the best_asset
 * worst_asset: which asset had the worst performance (value decrease) from the wallet compared to the latest price retrieved from the API
 * worst_performance: percentage of the performance of the worst_asset
 * Values rounded to 2 decimal places, HALF_UP
 */

@Data
@JsonPropertyOrder({"total", "best_asset", "best_performance", "worst_asset", "worst_performance"})
public final class WalletPerformanceResponse {

    @JsonIgnore
    private final BigDecimal total;
    @JsonProperty("best_asset")
    private final String bestAsset;
    @JsonIgnore
    private final BigDecimal bestPerformance;
    @JsonProperty("worst_asset")
    private final String worstAsset;
    @JsonIgnore
    private final BigDecimal worstPerformance;

    @JsonProperty("total")
    public double getTotalScaled() {
        return Optional.ofNullable(total)
                .map(b -> b.setScale(2, RoundingMode.HALF_UP))
                .map(BigDecimal::doubleValue)
                .orElse(0.0);
    }

    @JsonProperty("best_performance")
    public float getBestPerformancePercentage() {
        return roundPercentage(bestPerformance);
    }

    @JsonProperty("worst_performance")
    public float getWorstPerformancePercentage() {
        return roundPercentage(worstPerformance);
    }

    private float roundPercentage(final BigDecimal value) {
        return Optional.ofNullable(value)
                .map(b -> b.setScale(2, RoundingMode.HALF_UP))
                .map(BigDecimal::floatValue)
                .orElse(0.0f) * 100;
    }
}
