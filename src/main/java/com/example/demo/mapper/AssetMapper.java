package com.example.demo.mapper;

import com.example.demo.entity.Asset;
import com.example.demo.service.client.AssetData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for Asset.
 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AssetMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "symbol", source = "symbol")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "priceUsd", source = "priceUsd")
    @Mapping(target = "rank", ignore = true)
    @Mapping(target = "supply", ignore = true)
    @Mapping(target = "maxSupply", ignore = true)
    @Mapping(target = "marketCapUsd", ignore = true)
    @Mapping(target = "volumeUsd24Hr", ignore = true)
    @Mapping(target = "changePercent24Hr", ignore = true)
    @Mapping(target = "vwap24Hr", ignore = true)
    @Mapping(target = "explorer", ignore = true)
    AssetData toAssetData(Asset asset);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "symbol", source = "symbol")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "priceUsd", source = "priceUsd", defaultValue = "0")
    @Mapping(target = "updatedAt", ignore = true)
    Asset toAsset(AssetData assetData, @Context MappingContext context);
}
