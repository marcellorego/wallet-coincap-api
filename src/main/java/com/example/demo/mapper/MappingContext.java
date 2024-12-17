package com.example.demo.mapper;

import com.example.demo.entity.Asset;
import com.example.demo.entity.Wallet;
import com.example.demo.service.client.AssetData;
import com.example.demo.web.dto.WalletPerformanceResponse;
import lombok.Getter;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import java.time.Clock;

/**
 * Mapping context for AssetMapper and WalletMapper.
 */

@Getter
public final class MappingContext {

    private final Clock clock;
    private String walletId;

    public MappingContext(final Clock clock) {
        this.clock = clock;
    }

    public MappingContext(final Clock clock, final String walletId) {
        this(clock);
        this.walletId = walletId;
    }

    @AfterMapping
    public void afterAssetDataMapping(@MappingTarget final Asset entity,
                                 final AssetData dto) {
        entity.setUpdatedAt(clock.instant());
    }

    @AfterMapping
    public void afterWalletMapping(@MappingTarget final Wallet entity,
                                      final WalletPerformanceResponse dto) {
        final Wallet.WalletId id = Wallet.WalletId.builder()
                .id(walletId)
                .createdAt(clock.instant())
                .build();
        entity.setWalletId(id);
    }
}
