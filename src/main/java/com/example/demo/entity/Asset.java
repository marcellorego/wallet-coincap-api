package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Asset entity.
 */

@Data
@NoArgsConstructor(force = true)
@Entity
@Table(name = "asset")
@EqualsAndHashCode(of = {"id"})
public class Asset {

    // unique identifier for asset
    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 40)
    @NotBlank
    @Size(min = 1, max = 40)
    private String id;

    // most common symbol used to identify this asset on an exchange
    @Column(name = "symbol", nullable = false, length = 10)
    @NotBlank
    @Size(min = 1, max = 10)
    private String symbol;

    // proper name for asset
    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;

    // latest price of asset in USD
    @Column(name = "price_usd", nullable = false)
    @NotNull
    private BigDecimal priceUsd;

    // updated timestamp of asset price
    @Column(name = "updated_at", nullable = false)
    @NotNull
    private Instant updatedAt;
}