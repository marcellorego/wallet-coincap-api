package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Wallet entity.
 */

@Builder
@Data
@NoArgsConstructor(force = true)
@Entity
@Table(name = "wallet")
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Wallet {

    // identifier for wallet (CFJK32DUI4)
    @Id
    @NotNull(message = "Wallet id is required")
    @Column(name = "id", updatable = false, nullable = false, length = 10)
    private String id;

    // updated timestamp of wallet
    @Column(name = "updated_at", nullable = false)
    @NotNull
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "wallet_assets",
            joinColumns = @JoinColumn(name = "wallet_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id", referencedColumnName = "id"))
    private Set<Asset> linkedAssets = new LinkedHashSet<>();
}