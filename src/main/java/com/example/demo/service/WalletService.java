package com.example.demo.service;

import com.example.demo.entity.Asset;
import com.example.demo.entity.Wallet;
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
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for Wallet.
 */

@Service
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final AssetService assetService;
    private final PerformanceService performanceService;
    private final CoinCapService coinCapService;

    public WalletService(final WalletRepository walletRepository,
                         final PerformanceService performanceService,
                         final AssetService assetService,
                         final CoinCapService coinCapService) {
        this.walletRepository = walletRepository;
        this.performanceService = performanceService;
        this.assetService = assetService;
        this.coinCapService = coinCapService;
    }

    /**
     * Update the performance of the wallet.
     *
     * @param walletId
     * @param requestedAssetList
     * @param performanceAt
     * @return
     * @throws NotFoundException
     */
    @Transactional
    public WalletPerformanceResponse updatePerformance(final String walletId,
                                                       final Set<RequestedAsset> requestedAssetList,
                                                       final Instant performanceAt) {

        final Set<String> symbols = requestedAssetList
                .stream()
                .map(RequestedAsset::getSymbol)
                .collect(Collectors.toSet());

        final CoinCapAssets coinCapAssets =
                coinCapService.getCoinCapAssetsAsync(symbols)
                        .orElseThrow(() -> new NotFoundException(NotFoundException.NOT_FOUND_KEY,
                                "CoinCapAssets", "walletId", walletId));

        // Get the historical value of each asset at the specified time
        if (Objects.nonNull(performanceAt)) {

            for (AssetData assetData : coinCapAssets.getData()) {

                final BigDecimal historicalValue =
                        coinCapService.getAssetHistoryPriceAt(assetData.getId(), performanceAt);

                assetData.setPriceUsd(historicalValue);
            }

            // Update the timestamp with the specified parameter time
            coinCapAssets.setTimestamp(performanceAt.toEpochMilli());

        }

        final Instant updatedAt = Instant.ofEpochMilli(coinCapAssets.getTimestamp());

        // Update the assets
        final List<Asset> savedAssets =
                assetService.saveAssets(coinCapAssets.getData());

        // Create the wallet
        final Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setUpdatedAt(updatedAt);
        wallet.setLinkedAssets(new LinkedHashSet<>(savedAssets));

        // Save the wallet
        walletRepository.save(wallet);

        // Calculate the performance of the wallet
        final WalletPerformanceResponse walletPerformanceResponse =
                performanceService.calculateWalletValue(requestedAssetList, coinCapAssets);

        // Save the performance
        performanceService.savePerformance(walletId, updatedAt, walletPerformanceResponse);

        return walletPerformanceResponse;
    }
}
