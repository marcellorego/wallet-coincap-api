package com.example.demo.mapper;

import com.example.demo.entity.Wallet;
import com.example.demo.web.dto.WalletResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for Wallet.
 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {
                AssetMapper.class
        }
)
public interface WalletMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "assetData", source = "linkedAssets")
    WalletResponse toDto(Wallet entity);
}
