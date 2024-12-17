package com.example.demo.mapper;

import com.example.demo.entity.Asset;
import com.example.demo.entity.Wallet;
import com.example.demo.service.client.AssetData;
import com.example.demo.web.dto.WalletResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class WalletMapperTest {

    private static final WalletMapper MAPPER = Mappers.getMapper(WalletMapper.class);

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2024-12-15T10:00:00Z"),
            Clock.systemDefaultZone().getZone());

    @Test
    void mapEntityToDto() {

        Asset asset = new Asset();
        asset.setId("bitcoin");
        asset.setName("Bitcoin");
        asset.setSymbol("BTC");
        asset.setPriceUsd(BigDecimal.valueOf(100));

        Wallet wallet = new Wallet();
        wallet.setId("walletId");
        wallet.setUpdatedAt(CLOCK.instant());
        wallet.setLinkedAssets(Set.of(asset));

        WalletResponse walletResponse = MAPPER.toDto(wallet);

        AssetData assetData = new AssetData();
        assetData.setId("bitcoin");
        assetData.setName("Bitcoin");
        assetData.setSymbol("BTC");
        assetData.setPriceUsd(BigDecimal.valueOf(100));

        assertThat(walletResponse).isNotNull();
        assertThat(walletResponse.getId()).isEqualTo(wallet.getId());
        assertThat(walletResponse.getUpdatedAt()).isEqualTo(wallet.getUpdatedAt());
        assertThat(walletResponse.getAssetData())
                .hasSize(1)
                .contains(assetData);
    }
}
