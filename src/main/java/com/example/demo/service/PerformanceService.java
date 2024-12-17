package com.example.demo.service;

import com.example.demo.entity.Performance;
import com.example.demo.mapper.MappingContext;
import com.example.demo.mapper.PerformanceMapper;
import com.example.demo.repository.PerformanceRepository;
import com.example.demo.service.client.AssetData;
import com.example.demo.service.client.CoinCapAssets;
import com.example.demo.web.dto.RequestedAsset;
import com.example.demo.web.dto.WalletPerformanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceMapper performanceMapper;

    public PerformanceService(final PerformanceRepository performanceRepository,
                              final PerformanceMapper performanceMapper) {
        this.performanceMapper = performanceMapper;
        this.performanceRepository = performanceRepository;
    }

    @Transactional
    public void savePerformance(final String walletId, final Instant createdAt, final WalletPerformanceResponse walletPerformanceResponse) {
        final MappingContext mappingContext = new MappingContext(createdAt, walletId);
        final Performance performance = performanceMapper.toEntity(walletPerformanceResponse, mappingContext);
        performanceRepository.save(performance);
    }

    /**
     * Calculate the value of the wallet and the best and worst performing assets.
     *
     * @param requestedAssetList
     * @param coinCapAssets
     * @return
     */
    public WalletPerformanceResponse calculateWalletValue(final Set<RequestedAsset> requestedAssetList,
                                                          final CoinCapAssets coinCapAssets) {

        // Create a map of the requested assets
        final Map<String, RequestedAsset> requestedAssetMap = Optional.ofNullable(requestedAssetList)
                .orElse(Collections.emptySet())
                .stream()
                .collect(Collectors.toMap(RequestedAsset::getSymbol, Function.identity()));

        // Create a map of the coin cap assets
        final Map<String, AssetData> assetDataMap = Optional.ofNullable(coinCapAssets)
                .map(CoinCapAssets::getData)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(AssetData::getSymbol, Function.identity()));

        BigDecimal totalValue = BigDecimal.ZERO;

        RequestedAsset bestAsset = null;
        BigDecimal bestPerformance = BigDecimal.ZERO;

        RequestedAsset worstAsset = null;
        BigDecimal worstPerformance = BigDecimal.ZERO;

        for (Map.Entry<String, RequestedAsset> entry : requestedAssetMap.entrySet()) {

            final RequestedAsset requestedAsset = entry.getValue();
            final AssetData assetData = assetDataMap.getOrDefault(entry.getKey(), null);

            if (assetData == null) {
                log.warn("Asset not found for symbol {}", entry.getKey());
                continue;
            }

            // Calculate the current value of the asset based on the held quantity and the current price
            final BigDecimal currentValue = assetData.getPriceUsd().multiply(requestedAsset.getQuantity());

            totalValue = totalValue.add(currentValue);

            // Calculation of performance
            // Subtract the starting value from the ending value
            // Then divide that number by the starting value (keep scale to 20 decimal places as database column)
            // Represents the basic rate of return.
            final BigDecimal performance = (
                    assetData.getPriceUsd().subtract(requestedAsset.getPrice()))
                    .divide(requestedAsset.getPrice(), 20, RoundingMode.HALF_UP);

            if (bestAsset != requestedAsset && performance.compareTo(bestPerformance) > 0) {
                bestAsset = requestedAsset;
                bestPerformance = performance;
            }

            if (worstAsset != requestedAsset && performance.compareTo(worstPerformance) < 0) {
                worstAsset = requestedAsset;
                worstPerformance = performance;
            }
        }

        return new WalletPerformanceResponse(
                totalValue,
                Optional.ofNullable(bestAsset).map(RequestedAsset::getSymbol).orElse(""),
                bestPerformance,
                Optional.ofNullable(worstAsset).map(RequestedAsset::getSymbol).orElse(""),
                worstPerformance);

    }
}
