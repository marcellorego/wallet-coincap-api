package com.example.demo.config;

import com.example.demo.mapper.AssetMapper;
import com.example.demo.mapper.PerformanceMapper;
import com.example.demo.mapper.WalletMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class configures the mappers
 */

@Configuration
public class MapperConfiguration {

    /**
     * Asset mapper asset mapper.
     *
     * @return the asset mapper
     */
    @Bean
    public AssetMapper assetMapper() {
        return Mappers.getMapper(AssetMapper.class);
    }

    /**
     * Wallet mapper wallet mapper.
     *
     * @return the wallet mapper
     */
    @Bean
    public WalletMapper walletMapper() {
        return Mappers.getMapper(WalletMapper.class);
    }

    @Bean
    public PerformanceMapper performanceMapper() {
        return Mappers.getMapper(PerformanceMapper.class);
    }
}