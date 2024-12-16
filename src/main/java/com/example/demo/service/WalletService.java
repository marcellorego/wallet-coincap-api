package com.example.demo.service;

import com.example.demo.entity.Wallet;
import com.example.demo.mapper.MappingContext;
import com.example.demo.mapper.WalletMapper;
import com.example.demo.repository.WalletRepository;
import com.example.demo.service.client.AssetData;
import com.example.demo.service.client.CoinCapAssets;
import com.example.demo.web.dto.RequestedAsset;
import com.example.demo.web.dto.WalletPerformanceResponse;
import com.example.demo.web.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service implementation for Wallet.
 */

@Service
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final CoinCapService coinCapService;

    public WalletService(final WalletRepository walletRepository,
                         final WalletMapper walletMapper,
                         final CoinCapService coinCapService) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
        this.coinCapService = coinCapService;
    }

    /**
     * Calculate the value of the wallet and the best and worst performing assets.
     *
     * @param requestedAssetList
     * @param coinCapAssets
     * @return
     */
    private WalletPerformanceResponse calculateWalletValue(final List<RequestedAsset> requestedAssetList,
                                                           final CoinCapAssets coinCapAssets) {

        // Create a map of the requested assets
        final Map<String, RequestedAsset> requestedAssetMap = requestedAssetList
                .stream()
                .collect(Collectors.toMap(RequestedAsset::symbol, Function.identity()));

        // Create a map of the coin cap assets
        final Map<String, AssetData> assetDataMap = coinCapAssets
                .getData()
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
            final BigDecimal currentValue = assetData.getPriceUsd().multiply(requestedAsset.quantity());

            totalValue = totalValue.add(currentValue);

            // Calculation of performance
            // Subtract the starting value from the ending value
            // Then divide that number by the starting value (keep scale to 20 decimal places as database column)
            // Represents the basic rate of return.
            final BigDecimal performance = (
                    assetData.getPriceUsd().subtract(requestedAsset.price()))
                    .divide(requestedAsset.price(), 20, RoundingMode.HALF_UP);

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
                Optional.ofNullable(bestAsset).map(RequestedAsset::symbol).orElse(""),
                bestPerformance,
                Optional.ofNullable(worstAsset).map(RequestedAsset::symbol).orElse(""),
                worstPerformance);

    }

    /**
     * Update the performance of the wallet.
     *
     * @param walletId
     * @param requestedAssetList
     * @return
     */
    @Transactional
    public WalletPerformanceResponse updatePerformance(final String walletId,
                                                       final List<RequestedAsset> requestedAssetList) {

        final List<String> symbols = requestedAssetList
                .stream()
                .map(RequestedAsset::symbol)
                .toList();

        final CoinCapAssets coinCapAssets =
                coinCapService.getCoinCapAssetsAsync(symbols);

        // Calculate the performance of the wallet
        final WalletPerformanceResponse walletPerformanceResponse =
                calculateWalletValue(requestedAssetList, coinCapAssets);

        final MappingContext mappingContext = new MappingContext(coinCapService.getDefaultClock(), walletId);
        final Wallet wallet = walletMapper.toEntity(walletPerformanceResponse, mappingContext);

        // Save the wallet
        walletRepository.save(wallet);

        return walletPerformanceResponse;
    }

    /**
     * Get the performance of the wallet.
     *
     * @param walletId
     * @return
     * @throws NotFoundException
     */
    @Transactional
    public WalletPerformanceResponse getPerformance(final String walletId) throws NotFoundException {

        final WalletPerformanceResponse result = walletRepository.findById(walletId)
                .map(walletMapper::toDto)
                .orElseThrow(() -> new NotFoundException(NotFoundException.NOT_FOUND_KEY,
                        "Wallet", "walletId", walletId));

        log.debug("Fetched wallet performance for walletId {}: {}", walletId, result);

        return result;
    }
}
