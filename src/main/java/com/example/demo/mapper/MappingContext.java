package com.example.demo.mapper;

import com.example.demo.entity.Asset;
import com.example.demo.entity.Performance;
import com.example.demo.service.client.AssetData;
import com.example.demo.web.dto.WalletPerformanceResponse;
import lombok.Getter;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;

/**
 * Mapping context for AssetMapper and WalletMapper.
 */

@Getter
public final class MappingContext {

    private final Instant currentTime;
    private String walletId;

    public MappingContext(final Instant currentTime) {
        this.currentTime = currentTime;
    }

    public MappingContext(final Instant currentTime, final String walletId) {
        this(currentTime);
        this.walletId = walletId;
    }

    @AfterMapping
    public void afterAssetDataMapping(@MappingTarget final Asset entity,
                                 final AssetData dto) {
        entity.setUpdatedAt(currentTime);
    }

    @AfterMapping
    public void afterPerformanceMapping(@MappingTarget final Performance entity,
                                   final WalletPerformanceResponse dto) {
        final Performance.PerformanceId id = Performance.PerformanceId.builder()
                .walletId(walletId)
                .updatedAt(currentTime)
                .build();
        entity.setId(id);
    }
}
