package com.example.demo.mapper;

import com.example.demo.entity.Asset;
import com.example.demo.service.client.AssetData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AssetMapperTest {

    private static final AssetMapper MAPPER = Mappers.getMapper(AssetMapper.class);

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2024-12-15T10:00:00Z"),
            Clock.systemDefaultZone().getZone());

    private static final MappingContext CONTEXT = new MappingContext(CLOCK.instant());

    @Test
    void mapEntityToDto() {

        Asset asset = new Asset();
        asset.setId("bitcoin");
        asset.setName("Bitcoin");
        asset.setSymbol("BTC");

        AssetData assetData = MAPPER.toAssetData(asset);

        assertThat(assetData).isNotNull();
        assertThat(assetData.getId()).isEqualTo("bitcoin");
        assertThat(assetData.getName()).isEqualTo("Bitcoin");
        assertThat(assetData.getSymbol()).isEqualTo("BTC");
    }

    @Test
    void mapDtoToEntity() {

        AssetData assetDto = new AssetData();
        assetDto.setId("bitcoin");
        assetDto.setName("Bitcoin");
        assetDto.setSymbol("BTC");

        Asset asset = MAPPER.toAsset(assetDto, CONTEXT);

        assertThat(asset).isNotNull();
        assertThat(asset.getId()).isEqualTo("bitcoin");
        assertThat(asset.getName()).isEqualTo("Bitcoin");
        assertThat(asset.getSymbol()).isEqualTo("BTC");
        assertThat(asset.getUpdatedAt()).isEqualTo(CLOCK.instant());
    }

    @Test
    void mapEntityToDtoWithNullValues() {
        Asset asset = new Asset();

        AssetData assetDto = MAPPER.toAssetData(asset);

        assertThat(assetDto).isNotNull();
        assertThat(assetDto.getId()).isNull();
        assertThat(assetDto.getName()).isNull();
        assertThat(assetDto.getSymbol()).isNull();
    }

    @Test
    void mapDtoToEntityWithNullValues() {
        AssetData assetDto = new AssetData();

        Asset asset = MAPPER.toAsset(assetDto, CONTEXT);

        assertThat(asset).isNotNull();
        assertThat(asset.getId()).isNull();
        assertThat(asset.getName()).isNull();
        assertThat(asset.getSymbol()).isNull();
        assertThat(asset.getUpdatedAt()).isEqualTo(CLOCK.instant());
    }
}